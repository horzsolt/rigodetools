package horzsolt.rigodetools;

import horzsolt.rigodetools.mp3.AlbumPredicates;
import horzsolt.rigodetools.mp3.FTPHelper;
import horzsolt.rigodetools.mp3.FileToMp3Mapper;
import horzsolt.rigodetools.mp3.entity.Album;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static horzsolt.rigodetools.mp3.DateHelper.getDateStream;
import static org.junit.Assert.assertTrue;

/**
 * Created by horzsolt on 2017. 06. 11..
 */
public class ListerTest {

    public static final String startDir = "/MP3/0-DAY/0812";
    private final LocalDate startDate = LocalDate.parse("2017-08-12");
    private final LocalDate endDate = LocalDate.parse("2017-08-12");
    private List<String> lines = Collections.emptyList();


    @Test
    public void streamTest()  throws IOException {
        Path favPath = Paths.get("/olume1/horzsolt/rigodetools/favs.txt");

        if (Files.exists(favPath)) {
            lines = Files.readAllLines(favPath, Charset.forName("UTF-8"));
            lines = lines.stream() // :-(
                    .map(line -> line.toUpperCase())
                    .collect(Collectors.toList());
        }

        //assertTrue(lines.stream().anyMatch(x -> "axe".toUpperCase().contains(x)));
        assertTrue(lines.stream().anyMatch(x -> x.toUpperCase().contains("AXE")));
    }

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
    public void directoryLister() throws IOException, FTPIllegalReplyException, FTPException {

        Path favPath = Paths.get("/volume1/horzsolt/rigodetools/favs.txt");

        if (Files.exists(favPath)) {
            lines = Files.readAllLines(favPath, Charset.forName("UTF-8"));
            lines = lines.stream() // :-(
                    .map(line -> line.toUpperCase())
                    .collect(Collectors.toList());
        }

        String username = System.getenv("KOALA_USERNAME");
        String pws = System.getenv("KOALA_PWD");

        org.apache.commons.net.ftp.FTPClient ftp2 = new org.apache.commons.net.ftp.FTPClient();
        ftp2.connect(System.getenv("KOALA_HOST"), 7777);
        ftp2.login(username, pws);
        ftp2.setDataTimeout(60 * 1000);
        ftp2.enterLocalPassiveMode();

        FTPClient ftp = new FTPClient();
        ftp.connect(System.getenv("KOALA_HOST"), 7777);
        ftp.login(username, pws);

        ftp.setPassive(true);

        try {

            //FTPFile[] mp3Folders = ftp.listDirectories(startDir);
            List<Album> albumList = getDateStream(startDate, endDate)
                    .map(dateString -> "/MP3/0-DAY/" + dateString)
                    .flatMap(parent -> {
                            return FTPHelper.listFTPFolder(parent, ftp2);
                    })
                    //.peek(n -> System.out.println(n))
                    .filter(ftpFile -> ftpFile.getFtpFile().getName().length() > 3)
                    .map(ftpFile -> FileToMp3Mapper.apply(ftpFile, ftp, lines))
                    .filter(album -> album != null)
                    .filter(AlbumPredicates.isFavourite())
                    .filter(AlbumPredicates.isNotBanned())
                    .filter(AlbumPredicates.isNotRadioShow())
                    .collect(Collectors.toList());

            albumList.forEach(album -> System.out.println(album));

            assertTrue(albumList.stream().filter(album -> (album.getBitrate() != 0)).count() > 0);

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
