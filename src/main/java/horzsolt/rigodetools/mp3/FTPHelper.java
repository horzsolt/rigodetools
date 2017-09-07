package horzsolt.rigodetools.mp3;

import horzsolt.rigodetools.mp3.entity.FTPFilePlus;
import org.apache.commons.net.ftp.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by horzsolt on 2017. 06. 17..
 */
public class FTPHelper {

    public static Stream<FTPFilePlus> listFTPFolder(String folder, FTPClient ftp) {
        try {
            return Arrays.stream(ftp.listDirectories(folder))
                    .map(ftpFile -> {

                        FTPFile newFtpFile = new FTPFile();
                        newFtpFile.setSize(ftpFile.getSize());
                        newFtpFile.setName(ftpFile.getName());
                        newFtpFile.setType(FTPFile.TYPE_DIRECTORY);
                        newFtpFile.setModifiedDate(ftpFile.getTimestamp().getTime());
                        newFtpFile.setLink(ftpFile.getLink());

                        return newFtpFile;
                    })
                    .map(ftpFile -> new FTPFilePlus(ftpFile, folder));
        } catch (IOException e) {
            return null;
        }
    }
}
