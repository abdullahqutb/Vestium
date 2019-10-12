package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Look implements Parcelable, Serializable {
    private static final long serialVersionUID = 13L;

    private ArrayList<Item> data;
    String key;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public Look( String key) {
        data = new ArrayList<>();
        this.key = key;
    }

    public Look( Item... items) {
        data = new ArrayList<>();
        for( Item temp: items) {
            data.add( temp);
        }
    }

    public ArrayList<Item> getData() {
        return data;
    }
     public void addItem( Item temp) {
        data.add( temp);
     }

    public void removeItem( int temp) {
        data.remove( temp);
    }


    public void clearLook() {
        data.removeAll( data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.data);
        dest.writeString(this.key);
        dest.writeString(this.name);
    }

    protected Look(Parcel in) {
        this.data = in.createTypedArrayList(Item.CREATOR);
        this.key = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Look> CREATOR = new Parcelable.Creator<Look>() {
        @Override
        public Look createFromParcel(Parcel source) {
            return new Look(source);
        }

        @Override
        public Look[] newArray(int size) {
            return new Look[size];
        }
    };
}
