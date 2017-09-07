package horzsolt.rigodetools.mp3;

import horzsolt.rigodetools.RigodetoolsApplication;
import horzsolt.rigodetools.tools.StatusBean;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static horzsolt.rigodetools.mp3.DateHelper.getDateStream;

/**
 * Created by horzsolt on 2017. 08. 19..
 */
@Component
public class Mp3Getter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RigodetoolsApplication.FTPAccount ftpAcc;
    @Autowired
    private StatusBean statusBean;

    private List<String> lines = Collections.emptyList();

    @Scheduled(cron = "0 20 20 * * ?")
    public void doIt() throws IOException, FTPIllegalReplyException, FTPException {

        logger.debug("Mp3Getter scheduled.");
        Path favPath = Paths.get("/volume1/horzsolt/rigodetools/favs.txt");

        if (Files.exists(favPath)) {
            lines = Files.readAllLines(favPath, Charset.forName("UTF-8"));
            lines = lines.stream() // :-(
                    .map(line -> line.toUpperCase())
                    .collect(Collectors.toList());
            logger.debug("Favs loaded.");
        }

        org.apache.commons.net.ftp.FTPClient ftp2 = new org.apache.commons.net.ftp.FTPClient();
        ftp2.connect(ftpAcc.getHost(), 7777);
        ftp2.login(ftpAcc.getUsername(), ftpAcc.getPassword());
        ftp2.setDataTimeout(60 * 1000);
        ftp2.enterLocalPassiveMode();

        FTPClient ftp = new FTPClient();

        ftp.connect(ftpAcc.getHost(), 7777);
        ftp.login(ftpAcc.getUsername(), ftpAcc.getPassword());
        ftp.setPassive(true);

        logger.debug("Connected to ftp.");

        try {

            getDateStream(LocalDate.now(), LocalDate.now())
                    .map("/MP3/0-DAY/"::concat)
                    .flatMap(parent -> FTPHelper.listFTPFolder(parent, ftp2))
                    .filter(ftpFile -> ftpFile.getFtpFile().getName().length() > 3)
                    .map(ftpFile -> FileToMp3Mapper.apply(ftpFile, ftp, lines))
                    .filter(Objects::nonNull)
                    .filter(AlbumPredicates.isFavourite())
                    .filter(AlbumPredicates.isNotBanned())
                    .filter(AlbumPredicates.isNotRadioShow())
                    .forEach(album -> FileToMp3Mapper.dlFolder(ftp, album));
            statusBean.setLastMp3Run(LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Mp3Getter error: ", e);
        } finally {

            if (ftp2.isConnected()) {
                ftp2.disconnect();
            }

            if (ftp.isConnected()) {
                ftp.disconnect(true);
            }
        }
    }
}
