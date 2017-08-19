package horzsolt.rigodetools.mp3.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class Album {
    private String title;
    private int year;
    private String genre;
    private int bitrate;
    private int fileCount = 0;
    private List<Mp3> mp3Files = new ArrayList<>();
    private String ftpDirectory;
    private long sumSize;

    public long getList_duration() {
        return list_duration;
    }

    public void setList_duration(long list_duration) {
        this.list_duration = list_duration;
    }

    private long list_duration;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public List<Mp3> getMp3Files() {
        return mp3Files;
    }

    public void addMp3File(Mp3 mp3File) {
        mp3Files.add(mp3File);
        sumSize += mp3File.getSize();
        ++ fileCount;
    }

    public String getFtpDirectory() {
        return ftpDirectory;
    }

    public void setFtpDirectory(String ftpDirectory) {
        this.ftpDirectory = ftpDirectory;
    }

    public long getSumSize() {
        return sumSize;
    }

    public void setSumSize(long sumSize) {
        this.sumSize = sumSize;
    }

    @Override
    public String toString() {
        return getFtpDirectory() + "/" + getTitle();
    }
}
