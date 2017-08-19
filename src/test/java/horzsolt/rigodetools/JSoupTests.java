package horzsolt.rigodetools;

import horzsolt.rigodetools.pricecheck.PriceChecker;
import horzsolt.rigodetools.pricecheck.PriceExtractor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by horzsolt on 2017. 08. 14..
 */
public class JSoupTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void extractPriceTest() throws IOException {

        PriceChecker priceChecker = new PriceChecker();

        Long media = PriceExtractor.getPrice(priceChecker.medialUrl, priceChecker::mediaMarktPriceExtractor);

        logger.debug("MediaMarktPrice: " + media);
        assertTrue(media > 0);

        Long emag = PriceExtractor.getPrice(priceChecker.emagUrl, priceChecker::emagPriceExtractor);

        logger.debug("EMagPrice: " + emag);
        assertTrue(emag > 0);

        Long argep = PriceExtractor.getPrice(priceChecker.argepUrl, priceChecker::argepPriceExtractor);

        logger.debug("argepPrice: " + argep);
        assertTrue(argep > 0);

        Long edigital = PriceExtractor.getPrice(priceChecker.edigitalUrl, priceChecker::edigitalPriceExtractor);

        logger.debug("edigitalPrice: " + edigital);
        assertTrue(edigital > 0);

        Long telekom = PriceExtractor.getPrice(priceChecker.telekomUrl, priceChecker::telekomPriceExtractor);

        logger.debug("telekomPrice: " + telekom);
        assertTrue(telekom > 0);
    }
}
