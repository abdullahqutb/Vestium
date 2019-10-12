package allinontech.vestium.backend;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import allinontech.vestium.HomeScreen;

/**
 * Created by Mohammad on 5/7/18.
 */

public class LookGenerator {

    public static Wardrobe wardrobe;
    public static final String colors[] = {"red", "black", "white", "blue", "yellow", "gray"
            , "cyan", "green", "magenta", "orange", "pink"};
    private static Hashtable<String, Integer> hash;



    public static ArrayList<Item> generateLook(){
        hash = hashInitializer();
        wardrobe = HomeScreen.wardrobe;

        Log.d("ITEMS_WARDROBE", wardrobe.data.toString());

        final int CATEGORY_COUNT = 10;

        ArrayList<Integer> colors;
        ArrayList<Item> items;

        ArrayList<Item>[] categories = new ArrayList[CATEGORY_COUNT];
        for (int i = 0; i < categories.length; i++){
            categories[i] = new ArrayList<>();
        }


        colors = getPalette();

        //categorize into 10 categories
        categorize(categories);

        //now, categories is an array of arraylist of categorized items
        items = findByColor(categories, colors);
        Log.d("ITEM_POST", items.toString());
        for (Item item: items)
            Log.d("ITEM FINDER", item.getName());
        return items;

    }

    private static ArrayList<Integer> getPalette() {
        final int PALETTE_COUNT = 8;

        final int ORANGE = 0xFFA500;
        final int PINK = 0xFF69B4;

        ArrayList<ArrayList<Integer>> temp = new ArrayList<>();

        //create empty color palettes
        for (int i = 0; i < PALETTE_COUNT; i++) {
            temp.add(new ArrayList<Integer>());
        }

        //add colors to each

        //black green red
        temp.get(0).add(Color.BLACK);
        temp.get(0).add(Color.RED);
        temp.get(0).add(Color.GREEN);

        //blue white black
        temp.get(1).add(Color.BLUE);
        temp.get(1).add(Color.WHITE);
        temp.get(1).add(Color.BLACK);

        //green white black
        temp.get(2).add(Color.GREEN);
        temp.get(2).add(Color.WHITE);
        temp.get(2).add(Color.BLACK);

        //white cyan pink
        temp.get(3).add(Color.WHITE);
        temp.get(3).add(Color.CYAN);
        temp.get(3).add(PINK);

        //red black yellow
        temp.get(4).add(Color.RED);
        temp.get(4).add(Color.YELLOW);
        temp.get(4).add(Color.BLACK);

        //pink white black
        temp.get(5).add(PINK);
        temp.get(5).add(Color.WHITE);
        temp.get(5).add(Color.BLACK);

        //pink white
        temp.get(6).add(PINK);
        temp.get(6).add(Color.WHITE);

        //orange white black
        temp.get(7).add(ORANGE);
        temp.get(7).add(Color.WHITE);
        temp.get(7).add(Color.BLACK);

        return randomPalette(temp);

    }

    private static ArrayList<Integer> randomPalette(ArrayList<ArrayList<Integer>> temp) {
        int random = (int) (Math.random() * temp.size());
        return temp.get(random);
    }

    private static Hashtable<String, Integer> hashInitializer() {
        Hashtable<String, Integer> temp = new Hashtable<>();
        final int ORANGE = 0xFFA500;
        final int PINK = 0xFF69B4;

        temp.put(colors[0], Color.RED);
        temp.put(colors[1], Color.BLACK);
        temp.put(colors[2], Color.WHITE);
        temp.put(colors[3], Color.BLUE);
        temp.put(colors[4], Color.YELLOW);
        temp.put(colors[5], Color.GRAY);
        temp.put(colors[6], Color.CYAN);
        temp.put(colors[7], Color.GREEN);
        temp.put(colors[8], Color.MAGENTA);
        temp.put(colors[9], ORANGE);
        temp.put(colors[10], PINK);

        return temp;
    }

    private static ArrayList<Item> findByColor(ArrayList<Item>[] categories, ArrayList<Integer> colors) {
        ArrayList<Item> items;
        items = new ArrayList<>();
        for (int i = 0; i < categories.length; i++){
            Item item;
            //randomize the colors;
            Collections.shuffle(colors);

            //search using the shuffled colors and get an item
            Log.d("SEARCHING THIS CATEGORY", categories[i].toString());
            item = findItem(categories[i], colors);

            if (item != null) {
                items.add(item);

            }
            Log.d("POST ADDITION", items.toString());

        }
        return items;

    }

    private static Item findItem(ArrayList<Item> category, ArrayList<Integer> colors) {
        Collections.shuffle(category);
        for (int i = 0; i < category.size(); i++){
            for (int j = 0; j < colors.size(); j++){
                Log.d("CATEGORIZED ITEMS", category.toString());
                Log.d("SENDING ITEM", category.get(i).getName());
                if (compareColor(category.get(i).color, colors.get(j))){
                    Log.d("RESULT OF COMPARISON", "true @ " + category.get(i).getName() + " @ color "
                            + category.get(i).color);
                    return category.get(i);
                }
            }
        }
        return null;
    }

    private static boolean compareColor(String color, int value) {
        color = color.toLowerCase();
        //return hash.get(color) == value;
        Log.d("HASH VALUE", hash.get(color) + "");
        Log.d("INT VALUE", value + "");
        Log.d("METHOD VALUE", (value == hash.get(color)) + "");
        return value == hash.get(color);

    }


    private static int randomColor(ArrayList<Integer> colors) {
        int random;
        random = (int) (Math.random() * colors.size());
        return colors.get(random);
    }


    //categorize the categories into five categories
    private static void categorize(ArrayList<Item>[] categories) {
        int random = (int) (Math.random()* 3);
        ArrayList<String> cats = initializeCategories();

        String[] categ = {"Hats", "Scarfs" , "Sunglasses", "Necklaces", "Earrings", "Rings", "Shirts", "T-shirts"
                , "Polo Shirts","Sweaters", "Jackets", "Blazers", "Coats"
                , "Watches", "Belts", "Dresses", "Suits", "Skirts", "Pants", "Jeans", "Chinos","Shoes", "Heels"};

        Log.d("RANDOMIZER", "" + random);
        for (int i = 0; i < wardrobe.data.size(); i++){
            String holder = wardrobe.data.get(i).title;
            //for head
            if (holder.equalsIgnoreCase(categ[0]) || holder.equalsIgnoreCase(categ[1])){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[0].add(wardrobe.data.get(i).getItem(j));
                        Log.d("HEAD_ITEM", wardrobe.data.get(i).items.toString());
                    }
            }
            //for glasses
            if (holder.equalsIgnoreCase(categ[2])){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[1].add(wardrobe.data.get(i).getItem(j));
                        Log.d("GLASSES", wardrobe.data.get(i).items.toString());
                    }
            }

            //for jewelery 3,4,5
            if (holder.equalsIgnoreCase(categ[3]) || holder.equalsIgnoreCase(categ[4])
                    || holder.equalsIgnoreCase(categ[5])){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[2].add(wardrobe.data.get(i).getItem(j));
                        Log.d("JEWELS", wardrobe.data.get(i).items.toString());
                    }
            }

            //for chest 6,7,8,9
            if (random > 0 && (holder.equalsIgnoreCase(categ[6]) ||holder.equalsIgnoreCase(categ[7])
                    || holder.equalsIgnoreCase(categ[8]) || holder.equalsIgnoreCase(categ[9]))){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[3].add(wardrobe.data.get(i).getItem(j));
                        Log.d("CHESTS", wardrobe.data.get(i).items.toString());
                    }
            }
            //for jacket 10, 11, 12
            if (random > 0 && (holder.equalsIgnoreCase(categ[10]) ||holder.equalsIgnoreCase(categ[11]))
                    || holder.equalsIgnoreCase(categ[12])){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[4].add(wardrobe.data.get(i).getItem(j));
                        Log.d("JACKETS", wardrobe.data.get(i).items.toString());
                    }
            }
            //for watches 13
            if (holder.equalsIgnoreCase(categ[13]) ){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[5].add(wardrobe.data.get(i).getItem(j));
                        Log.d("WATCHES", wardrobe.data.get(i).items.toString());
                    }
            }

            //for belts 14
            if (holder.equalsIgnoreCase(categ[14]) ){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[6].add(wardrobe.data.get(i).getItem(j));
                        Log.d("BELTS", wardrobe.data.get(i).items.toString());
                    }
            }

            //for suits and dresses 15, 16
            if (random == 0 && (holder.equalsIgnoreCase(categ[15]) || holder.equalsIgnoreCase(categ[16]))  ){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[7].add(wardrobe.data.get(i).getItem(j));
                        Log.d("DRESS/SUIT", wardrobe.data.get(i).items.toString());
                    }
            }

            //for pants 17, 18, 19, 20
            if (random > 0 && (holder.equalsIgnoreCase(categ[17]) ||holder.equalsIgnoreCase(categ[18])
                    || holder.equalsIgnoreCase(categ[19]) || holder.equalsIgnoreCase(categ[20]) ) ){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[8].add(wardrobe.data.get(i).getItem(j));
                        Log.d("PANTS", wardrobe.data.get(i).items.toString());
                    }
            }

            //for shoes 21, 22
            if (holder.equalsIgnoreCase(categ[21]) ||holder.equalsIgnoreCase(categ[2])){
                for (int j = 0; j < wardrobe.data.get(i).size(); j++)
                    if (wardrobe.data.get(i).getItem(j) != null) {
                        categories[9].add(wardrobe.data.get(i).getItem(j));
                        Log.d("SHOES", wardrobe.data.get(i).items.toString());
                    }
            }
        }
    }

    private static ArrayList<String> initializeCategories() {
        ArrayList<String> temp = new ArrayList<>();

        temp.add("head");
        temp.add("glasses");
        temp.add("jewelery");
        temp.add("chest");
        temp.add("jacket");
        temp.add("watches");
        temp.add("belts");
        temp.add("dress");
        temp.add("pants");
        temp.add("shoes");

        return temp;

    }


}
