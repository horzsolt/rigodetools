package horzsolt.rigodetools.mp3.entity;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class Mp3 {
    private String title;
    private long size;
    private String extension;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
