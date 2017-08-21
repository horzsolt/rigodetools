package horzsolt.rigodetools.pricecheck;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Function;

public class PriceExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PriceExtractor.class);

    public static Long getPrice(String url, Function<Document, Long> filter) {

        long start = System.nanoTime();

        try {

            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
            return filter.apply(doc);

        } catch (Exception e) {
            logger.error("getPrice: ", e);
            return 0L;
        } finally {
            long end = System.nanoTime();
            logger.debug("Time taken : " + (end - start) / 1.0e9);
        }
    }
}
