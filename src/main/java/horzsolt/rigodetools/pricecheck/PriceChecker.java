package horzsolt.rigodetools.pricecheck;

import horzsolt.rigodetools.tools.MailSender;
import horzsolt.rigodetools.tools.StatusBean;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by horzsolt on 2017. 08. 14..
 */
@Component
public class PriceChecker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Long basePrice = 100000L;
    public final String emagUrl = "https://www.emag.hu/sony-playstation-4-pro-neo-konzol-1-tb-fekete-ps4pro1tb/pd/DVKHF2BBM/";
    public final String edigitalUrl = "https://edigital.hu/gepek/playstation-ps4-1tb-pro-jatekkonzol-p460598";
    public final String medialUrl = "http://www.mediamarkt.hu/hu/product/_sony-playstation-4-pro-1tb-1214875.html";
    public final String argepUrl = "http://www.argep.hu/product_2463768.html";
    public final String telekomUrl = "https://www.telekom.hu/shop/termek/PlayStation+4+Pro+1+TB--PS+4+PRO+1+TB+PLUSCARD?contractType=listPrice&sku=jsku1754609637&paymentType=FULL";

    @Autowired
    private MailSender mailSender;

    @Autowired
    private StatusBean statusBean;

    @Scheduled(cron = "0 0/5 7-19 ? * *")
    //@Scheduled(cron = "* * * * * *")
    public void checkPlaystationProPrice() {

        logger.info("checkPLaystationProPrice triggered");


        CompletableFuture<Long> future1
                = CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(emagUrl, this::emagPriceExtractor));
        CompletableFuture<Long> future2
                = CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(edigitalUrl, this::edigitalPriceExtractor));
        CompletableFuture<Long> future3
                = CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(medialUrl, this::mediaMarktPriceExtractor));
        CompletableFuture<Long> future4
                = CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(argepUrl, this::argepPriceExtractor));
        /*CompletableFuture<Long> future5
                = CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(telekomUrl, this::telekomPriceExtractor));
                */

        CompletableFuture<Void> combinedFuture
                = CompletableFuture.allOf(future1, future2, future3, future4).exceptionally((error) -> {
            statusBean.incNumberOfErrors();
            return null;
        });

        try {

            combinedFuture.get();

            Long result1 = future1.get();
            Long result2 = future2.get();
            Long result3 = future3.get();
            Long result4 = future4.get();
            //Long result5 = future5.get();

            if ((result1 < basePrice) || (result2 < basePrice) || (result3 < basePrice) || (result4 < basePrice)) {
                mailSender.sendMail("Price drop alert notification", "EMag: " + result1.toString() + ", Edigital: " + result2.toString() + ", Media: " + result3.toString() + ", argep: " + result4.toString());
            }

            statusBean.incInvokationCount();
            statusBean.setLastRun(LocalDateTime.now());

        } catch (InterruptedException | ExecutionException e) {
            logger.error("Exception at checkPlaystationProPrice: ", e);
        }

    }

    public Long mediaMarktPriceExtractor(Document document) {
        return new Long(document.select("meta[itemprop=price]").first().attr("content").replace(".", "").trim());
    }

    public Long emagPriceExtractor(Document document) {
        return new Long(document.select(".product-new-price").first().childNode(0).toString().replace(".", "").trim());
    }

    public Long argepPriceExtractor(Document document) {
        String cleaned = document.select(".OfferPrice").first().childNode(0).toString().replaceAll("[^0-9.]", "");
        return new Long(cleaned.replace(" ", "").trim());
    }

    public Long telekomPriceExtractor(Document document) {
        return new Long(StringEscapeUtils.escapeHtml4(document.select("product-summary__price ff-ult fs-2-5 ng-binding").first().childNode(0).toString().replace(" Ft", "").trim()));
    }

    public Long edigitalPriceExtractor(Document document) {
        Elements links = document.select(".price--large");

        return links.stream()
                .findFirst()
                .get().attributes()
                .asList().stream()
                .filter(x -> x.getKey().equals("content"))
                .mapToLong(x -> new Long(x.getValue()))
                .findFirst().getAsLong();
    }

}
