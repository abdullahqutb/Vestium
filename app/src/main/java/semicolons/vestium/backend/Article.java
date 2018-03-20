package semicolons.vestium.backend;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Muhammad Arham Khan on 17/03/2018.
 */

public class Article implements Serializable{
    String name;
    String type;
    String category;
    String color;
    String style;
    String brand;
    String imagePath;

    public Article(String name, String type, String category, String color, String style, String brand, String imagePath)
    {
        this.name = name;
        this.type = type;
        this.category = category;
        this.color = color;
        this.style = style;
        this.brand = brand;
        this.imagePath = imagePath;
    }

    public String getName()
    {
        return this.name;
    }
    public String getType()
    {
        return this.type;
    }
    public String getCategory()
    {
        return this.category;
    }
    public String getColor()
    {
        return this.color;
    }
    public String getStyle()
    {
        return this.style;
    }
    public String getBrand()
    {
        return this.brand;
    }
    public String getImagePath()
    {
        return this.imagePath;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> output = new HashMap<>();
        output.put("name", this.name);
        output.put("type", this.type);
        output.put("category", this.category);
        output.put("color", this.color);
        output.put("style", this.style);
        output.put("brand", this.brand);
        output.put("imagePath", this.imagePath);


        return output;
    }

}
