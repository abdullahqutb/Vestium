package allinontech.vestium;

import android.app.Application;
import android.content.Context;

public class Vestium extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Vestium.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Vestium.context;
    }
}