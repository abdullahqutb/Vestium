package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Circle implements Parcelable {
    ArrayList<Friend> list;

    public Circle() {
        list = new ArrayList<>();
    }

    public ArrayList<Friend> getList() {
        return list;
    }

    public void setList(ArrayList<Friend> list) {
        this.list = list;
    }

    public void addFriend( Friend temp) {
        this.list.add( temp);
    }

    public Friend findFriendFull( String username){
        for( Friend temp: list){
            if(temp.userId.equals( username))
                return temp;
        }
        return null;
    }

    public String friendName( String key){
        for( Friend temp: list){
            if(temp.getUserId().equals( key))
                return temp.getName();
        }
        return null;
    }

    public boolean findFriend( String username){
        boolean found = false;
        for( Friend temp: list){
            if(temp.userId.equals( username))
                found = true;
        }
        return found;
    }

    public void removeFriend( Friend temp) {
        for( int i = 0; i < this.list.size(); i++) {
            if( this.list.get( i) == temp)
                this.list.remove( i);
        }
    }

    public void clearCircle() {
        this.list.removeAll( this.list);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
    }

    protected Circle(Parcel in) {
        this.list = in.createTypedArrayList(Friend.CREATOR);
    }

    public static final Parcelable.Creator<Circle> CREATOR = new Parcelable.Creator<Circle>() {
        @Override
        public Circle createFromParcel(Parcel source) {
            return new Circle(source);
        }

        @Override
        public Circle[] newArray(int size) {
            return new Circle[size];
        }
    };
}
