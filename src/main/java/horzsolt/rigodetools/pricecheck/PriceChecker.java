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
    private final Long basePriceController = 16000L;
    public final String emagUrl = "https://www.emag.hu/sony-playstation-4-pro-1tb-ps4-konzol-57877/pd/DVV2Z7BBM/";
    public final String edigitalUrl = "https://edigital.hu/gepek/playstation-ps4-1tb-pro-jatekkonzol-p460598";
    public final String medialUrl = "http://www.mediamarkt.hu/hu/product/_sony-playstation-4-pro-1tb-1214875.html";
    public final String argepUrl = "http://www.argep.hu/product_2463768.html";
    public final String telekomUrl = "https://www.telekom.hu/shop/termek/PlayStation+4+Pro+1+TB--PS+4+PRO+1+TB+PLUSCARD?contractType=listPrice&sku=jsku1754609637&paymentType=FULL";

    public final String controllerEmag1 = "https://www.emag.hu/xbox-one-s-wireless-controller-minecraft-creeper-limited-edition-wl3-00057/pd/DB9MDHBBM/";
    public final String controllerEmag2 = "https://www.emag.hu/xbox-one-wireless-controller-minecraft-creeper-vezetek-nelkuli-kontroller-54971261/pd/DPGGJ0BBM/";
    public final String controllerEmag3 = "https://www.emag.hu/microsoft-jatekvezerlo-xbox-one-hoz-vezetek-nelkuli-controller-minecraft-edition-zold-wl3-00057/pd/DDH3LNBBM/";
    public final String controllerEdigital = "https://edigital.hu/kiegeszito/xbox-one-vezetek-nelkuli-kontroller-minecraft-collection-creeper-zold-p519478";
    public final String controllerMedia = "http://www.mediamarkt.hu/hu/product/_xbox-one-vezet%C3%A9k-n%C3%A9lk%C3%BCli-kontroller-minecraft-creeper-1241179.html";
    public final String controllerArgep = "http://www.argep.hu/main.aspx?sucheall=xbox+one+controller+creeper";


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

        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(emagUrl, 1, this::emagPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(edigitalUrl, 1, this::edigitalPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(medialUrl, 1, this::mediaMarktPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(argepUrl, 1, this::argepPriceExtractor)));

        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(controllerEmag1, 2, this::emagPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(controllerEmag2, 2, this::emagPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(controllerEmag3, 2, this::emagPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(controllerEdigital, 2, this::edigitalPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(controllerMedia, 2, this::mediaMarktPriceExtractor)));
        futures.add(CompletableFuture.supplyAsync(() -> PriceExtractor.getPrice(controllerArgep, 2, this::argepPriceExtractor)));

        CompletableFuture<List<String>> discountFutures = completeFutures(futures).thenApply(prices ->
                prices.stream()
                        .peek(priceResult -> logger.debug(("Peek before filter: " + priceResult.toString())))
                        .filter(priceResult -> (
                                priceResult.getProductType().equals(1) ? priceResult.getPrice() > 0 && priceResult.getPrice() < basePrice
                                        : priceResult.getPrice() > 0 && priceResult.getPrice() < basePriceController))
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

    public PriceResult mediaMarktPriceExtractor(Document document, Integer productType) {
        return new PriceResult("MediMarkt", new Long(document.select("meta[itemprop=price]").first().attr("content").replace(".", "").trim()), productType);
    }

    public PriceResult emagPriceExtractor(Document document, Integer productType) {
        return new PriceResult("EMag", new Long(document.select(".product-new-price").first().childNode(0).toString().replace(".", "").trim()), productType);
    }

    public PriceResult argepPriceExtractor(Document document, Integer productType) {
        String cleaned = document.select(".OfferPrice").first().childNode(0).toString().replaceAll("[^0-9.]", "");
        return new PriceResult("Argep", new Long(cleaned.replace(" ", "").trim()), productType);
    }

    public PriceResult telekomPriceExtractor(Document document, Integer productType) {
        return new PriceResult("Telekom", new Long(StringEscapeUtils.escapeHtml4(document.select("product-summary__price ff-ult fs-2-5 ng-binding").first().childNode(0).toString().replace(" Ft", "").trim())), productType);
    }

    public PriceResult edigitalPriceExtractor(Document document, Integer productType) {
        Elements links = document.select(".price--large");

        return new PriceResult("Edigital", new Long(links.stream()
                .findFirst()
                .get().attributes()
                .asList().stream()
                .filter(x -> x.getKey().equals("content"))
                .mapToLong(x -> new Long(x.getValue()))
                .findFirst().getAsLong()), productType);
    }

}
