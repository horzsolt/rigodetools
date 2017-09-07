package horzsolt.rigodetools.pricecheck;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class PriceExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PriceExtractor.class);

    public static PriceResult getPrice(String url, Function<Document, PriceResult> filter) {

        long start = System.nanoTime();
        PriceResult result = null;

        try {

            logger.debug("GetPric from: " + url);
            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
            result = filter.apply(doc);

            return result;

        } catch (Exception e) {
            logger.error("getPrice: ", e);
            return new PriceResult("",0L);
        } finally {
            long end = System.nanoTime();
            logger.debug("Time taken for " + url + ": " + (end - start) / 1.0e9);
        }
    }
}
