package horzsolt.rigodetools;

import horzsolt.rigodetools.mp3.AlbumPredicates;
import horzsolt.rigodetools.mp3.FTPHelper;
import horzsolt.rigodetools.mp3.FileToMp3Mapper;
import horzsolt.rigodetools.mp3.entity.Album;
import horzsolt.rigodetools.mp3.entity.AlbumSerializer;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static horzsolt.rigodetools.mp3.DateHelper.getDateStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class ListerTest {

    public static final String startDir = "/MP3/0-DAY/0610";
    private final LocalDate startDate = LocalDate.parse("2017-06-01");
    private final LocalDate endDate = LocalDate.parse("2017-06-02");
    ;


    @Test
    public void albumFavFilterTest() {
        Album album = new Album();
        album.setTitle("VA-Electronica_Spotlights_Trance_Edition-WEB-2017-ENSLAVE");

        Album album1 = new Album();
        album1.setTitle("VA-Pete_Tong-WEB-2017-ENSLAVE");

        Arrays.stream(new Album[] {album, album1})
                .filter(AlbumPredicates.isFavourite())
                .forEach(System.out::println);
    }

    @Test
    public void generateDates() {

        getDateStream(startDate, endDate)
                .map(dateString -> "/MP3/0-DAY/" + dateString)
                .forEach(System.out::println);
    }

    @Test
    public void directoryLister() throws IOException {

        FTPClient ftp = new FTPClient();
        ftp.connect(System.getenv("KOALA_HOST"), 7777);

        ftp.setDataTimeout(60 * 1000);

        String username = System.getenv("KOALA_USERNAME");
        String pws = System.getenv("KOALA_PWD");

        ftp.login(username, pws);
        ftp.enterLocalPassiveMode();

        try {

            //FTPFile[] mp3Folders = ftp.listDirectories(startDir);
            List<Album> albumList = getDateStream(startDate, endDate)
                    .map(dateString -> "/MP3/0-DAY/" + dateString)
                    .flatMap(parent -> {
                            return FTPHelper.listFTPFolder(parent, ftp);
                    })
                    //.peek(n -> System.out.println(n))
                    .filter(ftpFile -> ftpFile.getFtpFile().getName().length() > 3)
                    .map(ftpFile -> FileToMp3Mapper.apply(ftpFile, ftp))
                    .filter(album -> album != null)
                    .filter(AlbumPredicates.isFavourite())
                    .filter(AlbumPredicates.isNotBanned())
                    .filter(AlbumPredicates.isNotRadioShow())
                    .collect(Collectors.toList());

            albumList.forEach(album -> {
                    System.out.println(album);
                    AlbumSerializer.serialize(album);
            });

            assertTrue(albumList.stream().filter(album -> (album.getBitrate() != 0)).count() > 0);

        } finally {

            if (ftp.isConnected()) {
                ftp.disconnect();
            }
        }

    }

}
