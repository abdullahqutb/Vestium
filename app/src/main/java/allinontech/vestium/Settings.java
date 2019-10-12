package allinontech.vestium;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import allinontech.vestium.assets.Util;
import allinontech.vestium.fragments.HomeFragment;

public class Settings extends AppCompatActivity {

    private RelativeLayout userName;
    private TextView userNameText;
    private SwitchCompat genderSwitch;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsettings);
        setSupportActionBar(toolbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("SETTINGS");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        userName = (RelativeLayout) findViewById( R.id.changeUsername);
        userName.setOnClickListener( new changeUserNameListener());

        userNameText = (TextView) findViewById( R.id.username);
        userNameText.setText( HomeScreen.uName);

        genderSwitch = (SwitchCompat) findViewById( R.id.switch1);
        if( HomeScreen.gender.equals("male")){
            genderSwitch.setChecked( false);
        }
        else
            genderSwitch.setChecked( true);

        if( !isNetworkAvailable()){
            genderSwitch.setEnabled( false);
        }

        genderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked){
                    Util.getDatabase().getReference().child( mAuth.getUid()).child("gender").setValue("female", new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText( Vestium.getAppContext(), "Gender updated successfully", Toast.LENGTH_SHORT).show();
                            HomeScreen.gender = "female";
                            HomeScreen.refreshData();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    HomeFragment.reloadTheLook.performClick();
                                }
                            }, 1000);

                        }
                    });
                }
                else{
                    Util.getDatabase().getReference().child( mAuth.getUid()).child("gender").setValue("male", new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText( Vestium.getAppContext(), "Gender updated successfully", Toast.LENGTH_SHORT).show();
                            HomeScreen.gender = "male";
                            HomeScreen.refreshData();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    HomeFragment.reloadTheLook.performClick();
                                }
                            }, 1000);
                        }
                    });
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class changeUserNameListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //Toast.makeText(Vestium.getAppContext(), "Item Clicked", Toast.LENGTH_SHORT).show();
            if( !isNetworkAvailable())
                Toast.makeText(Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();
            else {
                final EditText input = new EditText(Settings.this);
                new AlertDialog.Builder(v.getContext())
                        .setTitle("CHANGE USERNAME")
                        .setView(input)
                        .setMessage("Enter new username:")
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String m_Text = input.getText().toString();
                                //Log.d( "ABABABABABABABABAB", m_Text);
                                try{
                                    if( HomeScreen.isUsernameAvailable( m_Text)) {
                                        HomeScreen.changeUsername(m_Text);
                                        Toast.makeText( Vestium.getAppContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                    else if( !HomeScreen.isUsernameAvailable( m_Text)) {
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                try {
                                                    if (HomeScreen.isUsernameAvailable(m_Text)) {
                                                        HomeScreen.changeUsername(m_Text);
                                                        Toast.makeText(Vestium.getAppContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(getIntent());
                                                    } else if (!HomeScreen.isUsernameAvailable(m_Text)) {

                                                        Toast.makeText(Vestium.getAppContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    Toast.makeText(Vestium.getAppContext(), "Username could not be changed, please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, 500);
                                    }
                                } catch ( Exception e){
                                    Log.d("UDUDONDONONDONOID", e.toString());
                                    Toast.makeText( Vestium.getAppContext(), "Username could not be changed, please try again", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).show();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

}

