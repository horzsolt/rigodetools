package horzsolt.rigodetools.mp3.entity;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class FTPContext {

    private FTPFile file;
    private String directory;
    private FTPClient ftp;

    public FTPFile getFile() {
        return file;
    }

    public String getDirectory() {
        return directory;
    }

    public FTPClient getFtp() {
        return ftp;
    }

    public FTPContext(FTPFile file, String directory, FTPClient ftp) {

        this.file = file;
        this.directory = directory;
        this.ftp = ftp;
    }
}
