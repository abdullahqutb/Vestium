package allinontech.vestium.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by amir on 08.05.2018.
 */

public class VestiumDate implements Serializable {
    private static final long serialVersionUID = 16L;
    private int day, month, year;

    public VestiumDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean equals(Object input) {
        VestiumDate temp = (VestiumDate) input;
        return this.day == temp.day && this.month == temp.month && this.year == temp.year;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year;
    }
}