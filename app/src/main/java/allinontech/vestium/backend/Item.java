package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Item implements Parcelable, Serializable{
    private static final long serialVersionUID = 124L;

    String key;
    String description;
    String image;
    String name;
    String color;
    String category;
    String style;
    boolean isPublic;
    boolean isDonation;
    boolean isLaundry;


    public Item() {
        this.description = "";
        this.isPublic = false;
        this.image = "";
        this.name = "";
        this.isDonation= false;
        this.isLaundry = false;
    }

    public boolean isDonation() {
        return isDonation;
    }

    public boolean isLaundry() {
        return isLaundry;
    }


    public void toggleDonation(boolean value){
        this.isDonation = value;
    }
    public void toggleLaundry(boolean value){
        this.isLaundry = value;
    }

    public void togglePublic(boolean value){
        this.isPublic = value;
    }
    public boolean getPublic(){
        return this.isPublic;
    }
    public String getKey() {
        return key;
    }

    public Item(String description, String image, String name, String category, String color, String style, String key) {

        this.description = description;
        this.image = image;
        this.name = name;
        this.category = category;
        this.color = color;
        this.style = style;
        this.key = key;

    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription( String description) {
        this.name = description;
    }

    public String getImage() {
        return this.image;
    }
    public void setImage( String image) {
        this.name = image;
    }

    public String getName() {
        return this.name;
    }
    public void setName( String name) {
        this.name = name;
    }

    public String toString() {
        return this.name + " ~ " + this.image + "~" + this.description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeString(this.name);
        dest.writeString(this.color);
        dest.writeString(this.category);
        dest.writeString(this.style);
        dest.writeByte(this.isPublic ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDonation ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLaundry ? (byte) 1 : (byte) 0);
    }

    protected Item(Parcel in) {
        this.key = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.name = in.readString();
        this.color = in.readString();
        this.category = in.readString();
        this.style = in.readString();
        this.isPublic = in.readByte() != 0;
        this.isDonation = in.readByte() != 0;
        this.isLaundry = in.readByte() != 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
