package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

public class PollItem implements Parcelable {
    String key;
    String id;
    String name;
    String image;
    String votes;

    public PollItem( String key, String name, String image, String votes, String id){
        this.key = key;
        this.id = id;
        this.name = name;
        this.image = image;
        this.votes = votes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getVotes() {
        return votes;
    }


    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.votes);
    }

    protected PollItem(Parcel in) {
        this.key = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
        this.votes = in.readString();
    }

    public static final Creator<PollItem> CREATOR = new Creator<PollItem>() {
        @Override
        public PollItem createFromParcel(Parcel source) {
            return new PollItem(source);
        }

        @Override
        public PollItem[] newArray(int size) {
            return new PollItem[size];
        }
    };
}
