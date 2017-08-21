package horzsolt.rigodetools.mp3;

import horzsolt.rigodetools.RigodetoolsApplication;
import horzsolt.rigodetools.tools.StatusBean;
import org.apache.commons.net.ftp.FTPClient;
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
import java.util.List;

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

    private List<String> lines = null;

    @Scheduled(cron = "0 30 19 * * ? *")
    public void doIt() throws IOException {

        Path favPath = Paths.get("/volume1/rigodetools/favs.txt");

        if (Files.exists(favPath)) {
            lines = Files.readAllLines(favPath, Charset.forName("UTF-8"));
            lines.stream().map(line -> line.toUpperCase());
        }

        FTPClient ftp = new FTPClient();
        ftp.connect(ftpAcc.getHost(), 7777);

        ftp.setDataTimeout(60 * 1000);

        ftp.login(ftpAcc.getUsername(), ftpAcc.getPassword());
        ftp.enterLocalPassiveMode();

        try {

            getDateStream(LocalDate.now(), LocalDate.now())
                    .map(dateString -> "/MP3/0-DAY/" + dateString)
                    .flatMap(parent -> {
                        return FTPHelper.listFTPFolder(parent, ftp);
                    })
                    //.peek(n -> System.out.println(n))
                    .filter(ftpFile -> ftpFile.getFtpFile().getName().length() > 3)
                    .map(ftpFile -> FileToMp3Mapper.apply(ftpFile, ftp, lines))
                    .filter(album -> album != null)
                    .filter(AlbumPredicates.isFavourite())
                    .filter(AlbumPredicates.isNotBanned())
                    .filter(AlbumPredicates.isNotRadioShow())
                    .forEach(album -> FileToMp3Mapper.dlFolder(ftp, album));
            statusBean.setLastMp3Run(LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Mp3Getter error: ", e);
        }
    }
}
