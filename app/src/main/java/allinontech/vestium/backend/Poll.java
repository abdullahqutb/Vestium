package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Poll implements Parcelable {
    ArrayList<PollItem> data;
    String name;
    String key;
    //Date startDate;


    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {

        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public Poll(){
        data = new ArrayList<>();
    }

    public ArrayList<PollItem> getData() {
        return data;
    }
    public void clear(){
        this.data.removeAll( this.data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.data);
        dest.writeString(this.name);
        dest.writeString(this.key);
    }

    protected Poll(Parcel in) {
        this.data = in.createTypedArrayList(PollItem.CREATOR);
        this.name = in.readString();
        this.key = in.readString();
    }

    public static final Creator<Poll> CREATOR = new Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel source) {
            return new Poll(source);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
}
