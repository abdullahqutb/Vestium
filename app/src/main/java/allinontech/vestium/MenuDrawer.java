package allinontech.vestium;

/**
 * Created by Muhammad Arham Khan on 03/03/2018.
 */
import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageButton;

/**
 * height of drawer set in res/dimens/"drawer_height"
 */

public class MenuDrawer {
    private int waitMS = 20000;  // The time after which the drawer is closing automatically
    private Handler handlCountDown;
    private  View bottomDrawer;
    private Activity activity;
    private static final int DRAWER_UP = 1;
    private static final int DRAWER_DOWN = 0;
    private static int direct;
    private int color;
    private int mainlayoutHeight;
    private int currentDrawer = -1;
    private boolean isSwitched = false;
    private RelativeLayout mainLayout;

    private enum S {OPEN_NOW, OPEN, CLOSE_NOW, CLOSE, CANCELED_NOW, CANCEL, TIME_OFF}     // States of animation
    private S animState = S.CLOSE;                                                        // Set state

    //*********************************************************************************************** Constructor
    MenuDrawer(Activity mainActivity) {
        activity = mainActivity;
        initialize();
        getLayoutHeight();


    }

    // ********************************************************************************************** Initialize
    private void initialize() {
        // Bottom Drawer
        bottomDrawer =  activity.findViewById(R.id.bottom_drawer);
        final ImageButton buttonOne = (ImageButton) bottomDrawer.findViewById(R.id.menu_button);
        assert buttonOne != null;
        buttonOne.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, android.view.MotionEvent event) {
            int action = event.getActionMasked();

            switch(action) {
                    case (android.view.MotionEvent.ACTION_DOWN) :
                        //android.widget.Toast.makeText(HomeScreen.this, "DOWN", android.widget.Toast.LENGTH_SHORT).show();
                        switchDrawer();
                        return true;

                    case (android.view.MotionEvent.ACTION_UP) :
                        //android.widget.Toast.makeText(HomeScreen.this, "UP", android.widget.Toast.LENGTH_SHORT).show();
                        switchDrawer();
                        return true;


            }
            return true;
            }


        });

        // Handler for timing for automatically closing the drawer
        handlCountDown = new Handler();
    }

    /*
    public class buttonsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switchDrawer();
        }
    }*/
    //********************************************************************************************** Open and Close the Drawer /Animation/
    private void drawerMovement(int movement){
        switch (movement) {
            case DRAWER_UP: // --------------------------------------------------------------------- Drawer UP
                float heightStatusMenu = activity.getResources().getDimension(R.dimen.drawer_height);
                bottomDrawer.animate().translationY(mainlayoutHeight - heightStatusMenu)
                        .setListener(new animationListener());
                direct = DRAWER_UP;
                break;

            case DRAWER_DOWN: // ------------------------------------------------------------------- Drawer DOWN
                bottomDrawer.animate().translationY(mainlayoutHeight)
                        .setListener(new animationListener());
                direct = DRAWER_DOWN;
                break;
        }
    }

    //**********************************************************************************************  Animation Listener
    private class animationListener implements  Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            if ((direct == DRAWER_UP) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)  && (animState != S.TIME_OFF)) animState = S.OPEN_NOW;
            if ((direct == DRAWER_DOWN) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)  && (animState != S.TIME_OFF)) animState = S.CLOSE_NOW;
            Log.d("Test", "Start Animation: " + animState);

            // Turning off the automatic timer closing drawer
            handlCountDown.removeCallbacks(closeDrawerTimer);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if ((direct == DRAWER_UP) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)){
                animState = S.OPEN;

                // Turning on the automatic timer closing drawer
                handlCountDown.postDelayed(closeDrawerTimer, waitMS);
            }
            if ((direct == DRAWER_DOWN) && (animState != S.CANCELED_NOW) && (animState != S.CANCEL)) animState = S.CLOSE;
            Log.d("Test", "End Animation: " + animState);

            // Animation Cancel
            if (animState == S.CANCELED_NOW){
                if (direct == DRAWER_UP){
                    Log.d("Test", "Animation Cancel - DIRECT UP: " + animState);
                    drawerMovement(DRAWER_DOWN);
                    animState = S.CANCEL;
                }else { // DIRECT DOWN
                    Log.d("Test", "Animation Cancel - DIRECT DOWN: " + animState);
                    animState = S.CANCEL;
                }
            }

            if ((animState != S.CANCELED_NOW) && (animState != S.CANCEL) && (animState != S.TIME_OFF))
                switchToNewDrawer();

            // Close Drawer after animation cancel
            if (animState == S.CANCEL){
                if (animState == S.CLOSE)
                animState = S.OPEN_NOW;
                drawerMovement(DRAWER_UP);
                Log.d("Test", "Animation Cancel");
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animState = S.CANCELED_NOW;
            isSwitched = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {}
    }

    // ********************************************************************************************* The action performed when you press the button with a choice of drawer
    void switchDrawer(){
        switch (animState) {
            case CLOSE: // ------------------------------------------------------------------------- DRAWER UP

                drawerMovement(DRAWER_UP);
                break;


            case OPEN: // -------------------------------------------------------------------------- DRAWER DOWN

                    drawerMovement(DRAWER_DOWN);

                break;


            case TIME_OFF: // ---------------------------------------------------------------------- Closing the drawer because time is over
                drawerMovement(MenuDrawer.DRAWER_DOWN);
                break;
        }
    }

    // --------------------------------------------------------------------------------------------- Switching between drawers
    private void switchToNewDrawer(){
        if (this.isSwitched){
            Log.d("Test" , "Switch Drawer ");

            switchDrawer();
            this.isSwitched = false;
        }
    }

    // --------------------------------------------------------------------------------------------- Changing the information on the drawer


    // --------------------------------------------------------------------------------------------- Closing the drawer
    private void closeDrawer(){
        animState = S.TIME_OFF;
        this.isSwitched = false;
        drawerMovement(DRAWER_DOWN);

        // Turning on the automatic timer closing drawer
        handlCountDown.postDelayed(closeDrawerTimer, waitMS);
    }



    // ********************************************************************************************* Get the Layout Height
    private void getLayoutHeight() {
        mainLayout = (RelativeLayout) activity.findViewById(R.id.main_layout);
        ViewTreeObserver vto = mainLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainlayoutHeight = mainLayout.getMeasuredHeight();
                bottomDrawer.setY(mainlayoutHeight);
                Log.d("Test", "Layout Height: " + mainlayoutHeight );
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    // ********************************************************************************************* Timer for closing the drawer
    // Automatically closes drawer after a set time
    private Runnable closeDrawerTimer = new Runnable() {
        @Override
        public void run() {
            closeDrawer();
        }
    };

}