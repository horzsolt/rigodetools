package horzsolt.rigodetools;

import horzsolt.rigodetools.pricecheck.PriceChecker;
import horzsolt.rigodetools.pricecheck.PriceExtractor;
import horzsolt.rigodetools.pricecheck.PriceResult;
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

        PriceResult media = PriceExtractor.getPrice(priceChecker.medialUrl, 1, priceChecker::mediaMarktPriceExtractor);

        logger.debug("MediaMarktPrice: " + media);
        assertTrue(media.getPrice() > 0);

        PriceResult emag = PriceExtractor.getPrice(priceChecker.emagUrl, 1, priceChecker::emagPriceExtractor);

        logger.debug("EMagPrice: " + emag);
        assertTrue(emag.getPrice() > 0);

        PriceResult argep = PriceExtractor.getPrice(priceChecker.argepUrl, 1, priceChecker::argepPriceExtractor);

        logger.debug("argepPrice: " + argep);
        assertTrue(argep.getPrice() > 0);

        PriceResult edigital = PriceExtractor.getPrice(priceChecker.edigitalUrl, 1, priceChecker::edigitalPriceExtractor);

        logger.debug("edigitalPrice: " + edigital);
        assertTrue(edigital.getPrice() > 0);

        /*PriceResult telekom = PriceExtractor.getPrice(priceChecker.telekomUrl, priceChecker::telekomPriceExtractor);

        logger.debug("telekomPrice: " + telekom);
        assertTrue(telekom.getPrice() > 0);
        */
    }
}
