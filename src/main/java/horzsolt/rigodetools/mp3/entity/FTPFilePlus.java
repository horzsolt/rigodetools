package horzsolt.rigodetools.mp3.entity;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Created by horzsolt on 2017. 06. 17..
 */
public class FTPFilePlus {

    FTPFile ftpFile;
    String parent;

    public FTPFilePlus(FTPFile ftpFile, String parent) {
        this.ftpFile = ftpFile;
        this.parent = parent;
    }

    public FTPFile getFtpFile() {
        return ftpFile;
    }

    public String getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "FPTFilePlus: " + getParent() + "/" + getFtpFile().getName();
    }
}
