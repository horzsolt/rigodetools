package horzsolt.rigodetools.mp3;

import horzsolt.rigodetools.mp3.entity.Album;
import horzsolt.rigodetools.mp3.entity.FTPFilePlus;
import horzsolt.rigodetools.mp3.entity.Mp3;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class FileToMp3Mapper {

    private static final Logger logger = LoggerFactory.getLogger(FileToMp3Mapper.class);

    public static void dlFolder(FTPClient client, Album album) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateFolderString = formatter.format(LocalDate.now());

        try {

            FTPFile[] files = client.listFiles(album.getFtpDirectory() + "/" + album.getTitle());
            String localPath;

            if (album.isFileFavourite()) {
                localPath = "/volume1/horzsolt/rigodetools/favourite/" + dateFolderString + "/" + album.getTitle();
            } else {
                localPath = "/volume1/horzsolt/rigodetools/0day/" + dateFolderString + "/" + album.getTitle();
            }

            Arrays.stream(files)
                    .filter(ftpFile -> ftpFile.isFile())
                    .forEach(ftpFile -> {
                try {
                    Path localFile = Paths.get(localPath + "/" + ftpFile.getName());

                    if (!Files.exists(localFile)) {
                        File f = new File(localPath);
                        logger.debug("MkDirs: " + localPath);
                        f.mkdirs();

                        logger.debug(album.getFtpDirectory() + "/" + album.getTitle() + "/" + ftpFile.getName() + " -> " + localPath);

                        OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(localPath + "/" + ftpFile.getName()));
                        boolean success = client.retrieveFile(album.getFtpDirectory() + "/" + album.getTitle() + "/" + ftpFile.getName(), outputStream1);

                        outputStream1.close();
                    }
                } catch (Exception e) {
                    logger.error("dlFolder error: ", e);

                }

                    });
        } catch (Exception e) {
            logger.error("dlFolder error: ", e);
        }
    }

    public static Album apply(FTPFilePlus ftpFilePlus, FTPClient client, List<String> fileFav) {

        Album album = new Album();
        album.setTitle(ftpFilePlus.getFtpFile().getName());
        album.setFtpDirectory(ftpFilePlus.getParent());

        try {
            FTPFile[] files = client.listFiles(album.getFtpDirectory() + "/" + album.getTitle());
            Arrays.stream(files)
                    .filter(ftpfile -> ftpfile.getName().length() > 3)
                    .forEach(ftpfile -> setAlbumDetails(album, ftpfile, fileFav));

            if ((!album.isFileFavourite()) && (fileFav.size() > 0)) {
                long count = album.getMp3Files().stream()
                        .filter(mp3 -> fileFav.stream().anyMatch(x -> mp3.getTitle().toUpperCase().contains(x.toUpperCase())))
                        .count();

                album.setFileFavourite(count > 0);
            }
        } catch (IOException e) {
            logger.error("FileToMp3Mapper.apply: ", e);
        }

        return album;
    }

    private static Album setAlbumDetails(Album album, FTPFile file, List<String> fileFav) {

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

                if (fileFav.size() > 0) {
                    album.setFileFavourite(fileFav.stream().anyMatch(x -> album.getTitle().toUpperCase().contains(x)));
                } else {
                    album.setFileFavourite(false);
                }

            } else {
                logger.info("CountTokens is not equals with 4: " + sk.countTokens() + " " + file.getName());
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
