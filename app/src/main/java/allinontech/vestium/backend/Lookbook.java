package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Lookbook implements Parcelable {
    private ArrayList<Look> data;

    public Lookbook() {
        data = new ArrayList<>();
    }

    public Lookbook( Look... items) {
        data = new ArrayList<>();
        for( Look temp: items) {
            data.add( temp);
        }
    }

    public ArrayList<Look> getData() {
        return data;
    }
    public void addLook( Look temp) {
        data.add( temp);
    }

    public void removeLook( int temp) {
        data.remove( temp);
    }

    public void clearLookbook() {
        data.removeAll( data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.data);
    }

    protected Lookbook(Parcel in) {
        this.data = in.createTypedArrayList(Look.CREATOR);
    }

    public static final Parcelable.Creator<Lookbook> CREATOR = new Parcelable.Creator<Lookbook>() {
        @Override
        public Lookbook createFromParcel(Parcel source) {
            return new Lookbook(source);
        }

        @Override
        public Lookbook[] newArray(int size) {
            return new Lookbook[size];
        }
    };
}
