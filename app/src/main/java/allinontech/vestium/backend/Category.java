package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Category implements Parcelable{
    ArrayList<Item> items;
    String title;

    public Category( String name){
        items = new ArrayList<>();
        this.title = name;
    }
    public String getTitle() {
        return this.title;
    }

    public void addItem( Item newItem) {
        items.add( newItem);
    }

    public void removeAllItems() {
        items.removeAll( items);
    }

    public int size() {
        return items.size();
    }

    public ArrayList<Item> getData() {
        return items;
    }

    public Item getItem(int index) {
        return (Item) items.get( index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.items);
        dest.writeString(this.title);
    }

    protected Category(Parcel in) {
        this.items = in.createTypedArrayList(Item.CREATOR);
        this.title = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
