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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by horzsolt on 2017. 08. 14..
 */
@Component
public class PriceChecker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Long basePrice = 100000L;
    public final String emagUrl = "https://www.emag.hu/sony-playstation-4-pro-1tb-ps4-konzol-57877/pd/DVV2Z7BBM/";
    public final String edigitalUrl = "https://edigital.hu/gepek/playstation-ps4-1tb-pro-jatekkonzol-p460598";
    public final String medialUrl = "http://www.mediamarkt.hu/hu/product/_sony-playstation-4-pro-1tb-1214875.html";
    public final String argepUrl = "http://www.argep.hu/product_2463768.html";
    public final String telekomUrl = "https://www.telekom.hu/shop/termek/PlayStation+4+Pro+1+TB--PS+4+PRO+1+TB+PLUSCARD?contractType=listPrice&sku=jsku1754609637&paymentType=FULL";

    @Autowired
    private MailSender mailSender;

    @Autowired
    private StatusBean statusBean;

    private static <T> CompletableFuture<List<T>> completeFutures(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }

    @Scheduled(cron = "0 0/30 7-19 ? * *")
    public void checkPlaystationProPrice() {

        logger.info("checkPLaystationProPrice triggered");
        List<CompletableFuture<PriceResult>> futures = new ArrayList<>();

        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(emagUrl, this::emagPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(edigitalUrl, this::edigitalPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(medialUrl, this::mediaMarktPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(argepUrl, this::argepPriceExtractor)));

        CompletableFuture<List<String>> discountFutures = completeFutures(futures).thenApply(prices ->
                prices.stream()
                        .peek(priceResult -> logger.debug(("Peek before filter: " + priceResult.toString())))
                        .filter(priceResult -> (priceResult.getPrice() > 0 && priceResult.getPrice() < basePrice))
                        .peek(priceResult -> logger.debug(("Peek after filter: " + priceResult.toString())))
                        .map(priceResult -> priceResult.toString())
                        .collect(Collectors.toList()));

        List<String> discounts = null;

        try {
            discounts = discountFutures.get();
        } catch (Exception e) {
            logger.error("",e);
        }

        if (discounts.size() > 0)
                mailSender.sendMail("Price drop alert notification", discounts.toString());

        statusBean.incInvokationCount();
        statusBean.setLastRun(LocalDateTime.now());
    }

    public PriceResult mediaMarktPriceExtractor(Document document) {
        return new PriceResult("MediMarkt", new Long(document.select("meta[itemprop=price]").first().attr("content").replace(".", "").trim()));
    }

    public PriceResult emagPriceExtractor(Document document) {
        return new PriceResult("EMag", new Long(document.select(".product-new-price").first().childNode(0).toString().replace(".", "").trim()));
    }

    public PriceResult argepPriceExtractor(Document document) {
        String cleaned = document.select(".OfferPrice").first().childNode(0).toString().replaceAll("[^0-9.]", "");
        return new PriceResult("Argep", new Long(cleaned.replace(" ", "").trim()));
    }

    public PriceResult telekomPriceExtractor(Document document) {
        return new PriceResult("Telekom", new Long(StringEscapeUtils.escapeHtml4(document.select("product-summary__price ff-ult fs-2-5 ng-binding").first().childNode(0).toString().replace(" Ft", "").trim())));
    }

    public PriceResult edigitalPriceExtractor(Document document) {
        Elements links = document.select(".price--large");

        return new PriceResult("Edigital", new Long(links.stream()
                .findFirst()
                .get().attributes()
                .asList().stream()
                .filter(x -> x.getKey().equals("content"))
                .mapToLong(x -> new Long(x.getValue()))
                .findFirst().getAsLong()));
    }

}
