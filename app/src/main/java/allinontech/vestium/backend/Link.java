package allinontech.vestium.backend;

/**
 * Created by esra on 03.05.2018.
 */

public class Link{

    //properties
    String fashionLink;
    String imageLink;
    String title;

    // for shopping
    String price;

    public Link() {

    }

    public Link( String fashionLink, String imageLink, String title )
    {
        this.fashionLink = fashionLink;
        this.imageLink = imageLink;
        this.title = title;
    }

    public Link( String fashionLink, String imageLink, String title, String price )
    {
        this.fashionLink = fashionLink;
        this.imageLink = imageLink;
        this.title = title;
        this.price = price;
    }

    public String getFashionLink()
    {
        return fashionLink;
    }

    public void setFashionLink( String link)
    {
        fashionLink = link;
    }

    public String getImageLink()
    {
        return imageLink;
    }

    public void setImageLink( String link)
    {
        imageLink = link;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String articleTitle)
    {
        title = articleTitle;
    }

    public String getPrice() { return price; }

    public void setPrice( String shoppingItemPrice) { this.price = shoppingItemPrice; }

    public String toString()
    {
        return "URL: " + fashionLink + " " + "Image Link: " + imageLink + " " + "Title: " + title
                + " Price: " + price;
    }


}
