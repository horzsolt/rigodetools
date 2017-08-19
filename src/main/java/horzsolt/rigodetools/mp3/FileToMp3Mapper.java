package horzsolt.rigodetools.mp3;

import horzsolt.rigodetools.mp3.entity.Album;
import horzsolt.rigodetools.mp3.entity.FTPFilePlus;
import horzsolt.rigodetools.mp3.entity.Mp3;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class FileToMp3Mapper {

    public static Album apply(FTPFilePlus ftpFilePlus, FTPClient client) {

        Album album = new Album();
        album.setTitle(ftpFilePlus.getFtpFile().getName());
        album.setFtpDirectory(ftpFilePlus.getParent());

        try {
            FTPFile[] files = client.listFiles(album.getFtpDirectory() + "/" + album.getTitle());
            Arrays.stream(files)
                    .filter(ftpfile -> ftpfile.getName().length() > 3)
                    .forEach(ftpfile -> setAlbumDetails(album, ftpfile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return album;
    }

    private static Album setAlbumDetails(Album album, FTPFile file) {

        if (file.isDirectory()) {
            //-[House]--[2015]--[320 KBit]--[MP3TRACKZ.NET]-
            StringTokenizer sk = new StringTokenizer(file.getName().replaceAll("-", ""), "[]");

            if (sk.countTokens() == 4) {

                album.setGenre(sk.nextToken().toLowerCase());

                try {
                    album.setYear(Integer.parseInt(sk.nextToken()));
                    album.setBitrate(Integer.parseInt(sk.nextToken().replaceAll("[^0-9]", "")));
                } catch (Exception e) {
                    album.setYear(LocalDate.now().getYear());
                    album.setBitrate(256);
                }
            } else {
                System.out.println("CountTokens is not equals with 4: " + sk.countTokens() + " " + file.getName());
            }

        } else {

            Mp3 mp3File = new Mp3();
            mp3File.setTitle(file.getName());
            mp3File.setSize(file.getSize());

            //System.out.println("Getting extension: " + file.getName());
            int pos = file.getName().lastIndexOf(".");
            mp3File.setExtension(file.getName().substring(pos, pos + 4));

            if (mp3File.getExtension().toUpperCase().endsWith("MP3")) {
                album.addMp3File(mp3File);
            }
        }

        return album;
    }
}
