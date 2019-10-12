package allinontech.vestium.backend;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by amir on 08.05.2018.
 */

public class VestiumDateContainer implements Serializable {
    private static final long serialVersionUID = 14L;

    private VestiumDate date;
    private ArrayList<Look> looks;

    private static final String fileName = "mydates.data";

    public VestiumDateContainer(VestiumDate date, ArrayList<Look> looks) {
        this.date = date;
        this.looks = looks;
    }

    public VestiumDate getDate() {
        return date;
    }

    public ArrayList<Look> getLooks() {
        return looks;
    }

    public void resetLooks(ArrayList<Look> inputLooks) {
        this.looks = inputLooks;
    }

    public boolean compareDate( VestiumDate inputDate) {
        return date.equals(inputDate);
    }

    public static void saveToFile(Context context, ArrayList<VestiumDateContainer> saveThis) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(saveThis);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<VestiumDateContainer> readFromFile(Context context) {
        ArrayList<VestiumDateContainer> createResumeForm = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            createResumeForm = (ArrayList<VestiumDateContainer>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return createResumeForm;
    }
}