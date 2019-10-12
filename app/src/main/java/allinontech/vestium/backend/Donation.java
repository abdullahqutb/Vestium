package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Donation implements Parcelable {
    ArrayList<Item> data;

    public Donation(){
        data = new ArrayList<>();
    }

    public ArrayList<Item> getData() {
        return data;
    }

    public void clearDonation(){
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

    protected Donation(Parcel in) {
        this.data = in.createTypedArrayList(Item.CREATOR);
    }

    public static final Creator<Donation> CREATOR = new Creator<Donation>() {
        @Override
        public Donation createFromParcel(Parcel source) {
            return new Donation(source);
        }

        @Override
        public Donation[] newArray(int size) {
            return new Donation[size];
        }
    };
}
