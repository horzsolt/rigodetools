package horzsolt.rigodetools.pricecheck;

/**
 * Created by horzsolt on 2017. 08. 24..
 */
public class PriceResult {
    private String site;
    private Long price;
    private Integer productType;

    public PriceResult(String site, Long price, Integer productType) {
        this.site = site;
        this.price = price;
        this.productType = productType;
    }

    public String getSite() {

        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return site + ": " + price.toString();
    }
}
