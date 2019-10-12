package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Laundry implements Parcelable {
    ArrayList<Item> data;

    public Laundry(){
        data = new ArrayList<>();
    }

    public ArrayList<Item> getData() {
        return data;
    }

    public void clearLaundry(){
        this.data.removeAll( this.data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.data);
    }

    protected Laundry(Parcel in) {
        this.data = in.createTypedArrayList(Item.CREATOR);
    }

    public static final Parcelable.Creator<Laundry> CREATOR = new Parcelable.Creator<Laundry>() {
        @Override
        public Laundry createFromParcel(Parcel source) {
            return new Laundry(source);
        }

        @Override
        public Laundry[] newArray(int size) {
            return new Laundry[size];
        }
    };
}
