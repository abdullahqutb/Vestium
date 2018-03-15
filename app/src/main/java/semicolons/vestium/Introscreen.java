package semicolons.vestium;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Introscreen extends AppCompatActivity {

    private LinearLayout introimages;
    private ImageView introtext;

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introscreen);

        introimages = (LinearLayout) findViewById(R.id.introimages);
        introtext = (ImageView) findViewById(R.id.introtext);

        //Putting out of the screen
        //for the logo
        ObjectAnimator b = ObjectAnimator.ofFloat(introimages, "translationY", -1000);
        b.setInterpolator(new AccelerateDecelerateInterpolator());
        b.setDuration(0);

        ObjectAnimator a = ObjectAnimator.ofFloat(introimages, "translationY", 0);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setDuration(800);

        //for the text
        ObjectAnimator c = ObjectAnimator.ofFloat(introtext, "translationY", 1000);
        c.setInterpolator(new AccelerateDecelerateInterpolator());
        c.setDuration(0);

        ObjectAnimator d = ObjectAnimator.ofFloat(introtext, "translationY", 0);
        d.setInterpolator(new AccelerateDecelerateInterpolator());
        d.setDuration(800);


        b.start();
        a.start();
        c.start();
        d.start();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Introscreen.this,Login.class);
                Introscreen.this.startActivity(mainIntent);
                Introscreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
