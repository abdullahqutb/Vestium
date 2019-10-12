package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import allinontech.vestium.Vestium;

public class Wardrobe implements Parcelable{
    ArrayList<Category> data;
    //Hashtable<String, Integer> reference;

    public Wardrobe(){
        data = new ArrayList<>();
        //reference = new Hashtable<>();
    }

    public int getCategoryCount() {
        return this.data.size();
    }

    public void addCategory( String name) {
        Category temp = new Category( name);
        data.add( temp);
    }


    public ArrayList<Category> getData() {
        return data;
    }

    public void refreshWardrobe() {
        for( int i = 0; i < data.size(); i++) {
            if( this.data.get( i).size() == 0){
                this.data.remove( i);
            }
        }
    }

    public Category getCategory( String title) {
        for( Category temp: data){
            if( temp.getTitle().equals( title))
                return temp;
        }
        return null;
    }

    public void clearAllCategories() {
        this.data.removeAll( this.data);
    }

    public Item getItem( String key) {
        for(int i = 0; i < data.size(); i++) {
            for( Item temp: data.get(i).getData()) {
                if( temp.getKey().equals( key))
                    return temp;
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.data);
    }

    protected Wardrobe(Parcel in) {
        this.data = in.createTypedArrayList(Category.CREATOR);
    }

    public static final Creator<Wardrobe> CREATOR = new Creator<Wardrobe>() {
        @Override
        public Wardrobe createFromParcel(Parcel source) {
            return new Wardrobe(source);
        }

        @Override
        public Wardrobe[] newArray(int size) {
            return new Wardrobe[size];
        }
    };
}
