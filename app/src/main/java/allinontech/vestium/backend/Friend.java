package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable {
    String name;
    String userId;
    String key;

    public Friend() {
    }

    public Friend( String name, String userId, String key) {
        this.name = name;
        this.userId = userId;
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.userId);
        dest.writeString(this.key);
    }

    protected Friend(Parcel in) {
        this.name = in.readString();
        this.userId = in.readString();
        this.key = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
