package allinontech.vestium.backend;

/**
 * Created by amir on 23.04.2018.
 */

public class ShoppingItem {
    private String link;
    private String brand;
    private String price;

    public ShoppingItem() {

    }

    public void setLink( String link ) {
        this.link = link;
    }

    public void setBrand ( String brand) {
        this.brand = brand;
    }

    public void setPrice ( String price ) {
        this.price = price;
    }
}
