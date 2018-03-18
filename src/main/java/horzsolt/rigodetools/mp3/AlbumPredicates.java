package horzsolt.rigodetools.mp3;

import horzsolt.rigodetools.mp3.entity.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Created by horzsolt on 2017. 06. 17..
 */
public class AlbumPredicates {

    private static final Logger logger = LoggerFactory.getLogger(AlbumPredicates.class);

    public static final String[] favs = {"BUUREN", "DIGWEED","SAIZ", "PETE_TONG", "PETE.TONG", "DOORN", "GUY_J", "GUY.J", "NICK_WARREN", "NICK.WARREN", "SASHA", "MACEO_PLEX",
            "ADVISOR", "NEWIK", "MUSIC_KILLERS", "MINILOGUE", "PIG_DAN", "WATERMAT", "SHOWTEK", "MANTEY","TODD_TERJE", "MATTZO",
            "WESTBAM", "BERGHEAU", "BOOKA_SHADE", "BLIZZARD", "RANK1", "KOLETZKI", "KALKBRENNER", "REX_MUNDI", "ATB", "DASH_BERLIN", "ORJAN_NIELSEN",
            "GARETH_EMERY", "BLOMQVIST", "NADJALIND", "EXTRAWELT", "SCAVO", "KOLLEKTIV", "BERLIN", "DAHLBACK", "BENASSI", "VARIOUS",
            "COMPILED", "COMPILATION", "GUETTA", "HARRIS", "DUMONT", "CLAPTONE", "ZEDD_", "MARK_KNIGHT", "SHEPARD", "FEDDE_LE_GRAND", "ROGER_SHAH", "KYAU", "ABOVE",
            "BEST", "SELECTED", "SELECTION", "EXCLUSIVE", "TALE_OF_US", "DIXON", "_HEIDI", "HAWTIN", "GARRIX", "BEN_KLOCK", "TROXLER", "EATS_EVERYTHING", "COLLECTION",
            "HOT_SINCE", "MARCO_CAROLA", "KLARTRAUM", "MAYA_JANE_COL", "SVEN_VATH", "JACK_MASTER", "JAY_LUMEN", "NINA_KRAVIZ", "GARNIER", "LIEBING",
            "DAMIAN_LAZARUS", "CARL_COX", "LUCIANO", "LEE_BURRIDGE", "JORIS_VOORN", "BUTCH", "ZABIELA", "CATZ", "CATTANEO", "JEFF_MILLS",
            "JUSTIN_MARTIN", "LAWLER", "PACO_OSUNA", "TANZMANN", "DEETRON", "DEEP", "BENN_FINN", "TIMO_MAAS", "PICKS", "FABRIC",
            "GLOBAL_UNDERGROUND", "MOBY", "SERIES", "HOXTON", "ESSENTIAL", "MOONBEAM", "RA-TOP", "TRACKS", "UNMIXED", "BUDAI",
            "HUNGARY", "BUDAPEST", "_SOUNDS", "PODCAST", "MANTZUR", "VANNELLI", "HAWTIN", "TRENTEM", "CHYMERA", "SCUBA", "GUY_GERBER", "ZUSAMMENKLANG",
            "EULBERG, HAZENDONK", "RILEY_REINHOLD", "_TOP", "CD_POOL", "TRANCE", "RESIDENT", "ADVISOR", "FABRIK", "WILLCOX", "TYDI", "SUNBEAM", "ANTHOLOGY",
            "ADRIATIQUE", "MARLO", "BUDAKID", "YOTTO", "BICEP", "GHEIST"};

    public static final String[] banned = {"HARDSTYLE", "GOA", "DRUM_AND_BASS", "_PSY_", "90S", "TRAP_", "TANZ", "CHILLHOUSE", "RETRO","HOT_DANCE", "EDM"};

    public static Predicate<Album> isFavourite() {
        return album -> {
            boolean result =
            album.isFileFavourite() || Arrays.stream(favs).anyMatch(x -> album.getTitle().toUpperCase().contains(x));

            if (result) {
                logger.debug("album.isFileFavourite() is " + album.isFileFavourite());
                logger.debug("isFavourite predicate of " + album.getTitle() + " is " + result);
            } else {
                result = (album.getMp3Files().stream()
                        .filter(x -> Arrays.stream(favs).anyMatch(f -> x.getTitle().toUpperCase().contains(f)))
                        .count()) > 0;
            }

            return result;
        };
    }

    public static Predicate<Album> isNotBanned() {
        return album -> Arrays.stream(banned).noneMatch(x -> album.getTitle().toUpperCase().contains(x));
    }

    public static Predicate<Album> isNotRadioShow() {
        return album -> (album.getTitle().toUpperCase().contains("PETE_TONG")) && !(album.getFileCount()  < 2 && album.getSumSize() < 52914560);
    }
}
