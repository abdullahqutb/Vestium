package allinontech.vestium;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;


import java.util.ArrayList;
import java.util.List;


import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Circle;
import allinontech.vestium.backend.Donation;
import allinontech.vestium.backend.Friend;
import allinontech.vestium.backend.Item;
import allinontech.vestium.backend.Laundry;
import allinontech.vestium.backend.Look;
import allinontech.vestium.backend.Lookbook;
import allinontech.vestium.backend.Poll;
import allinontech.vestium.backend.PollItem;
import allinontech.vestium.backend.Wardrobe;
import allinontech.vestium.fragments.HomeFragment;
import allinontech.vestium.fragments.LookbookFragment;
import allinontech.vestium.fragments.CircleFragment;
import allinontech.vestium.fragments.CalendarFragment;


public class HomeScreen extends AppCompatActivity {

    private static FirebaseAuth mAuthStatic;
    private Toolbar toolbar;
    private Button newItem;
    public static String uName;
    public static String nameUser;
    public static String gender;
    private static Button syncNow;
    private MenuDrawer appDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView textName, textEmail;
    private FirebaseAuth mAuth;

    public static DatabaseReference mDatabase;
    public static DatabaseReference lookDatabase;
    public static DatabaseReference circleDatabase;
    public static DatabaseReference mainRef;
    public static DatabaseReference pollRef;

    public static Wardrobe wardrobe;
    public static Lookbook lookbook;
    public static Circle circle;
    public static Poll currentPoll;
    public static Laundry laundry;
    public static Donation donation;


    public static String pollItemName;
    public static String pollItemImage;
    public static boolean available;



    private ViewPagerAdapter adapter;

    private int[] tabIcons = {
            R.drawable.ic_home_white_24dp,
            R.drawable.ic_date_range_black_24dp,
            R.drawable.ic_import_contacts_black_24dp,
            R.drawable.ic_group_black_24dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        wardrobe = new Wardrobe();
        lookbook = new Lookbook();
        circle = new Circle();
        currentPoll = new Poll();
        laundry = new Laundry();
        donation = new Donation();
        mAuth = FirebaseAuth.getInstance();


        Util.getDatabase().getReference().child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                        if( dataSnapshot.child("gender").getValue() != null){
                            gender = dataSnapshot.child("gender").getValue().toString();
                        }
                        else{
                            gender = "male";
                            if( gender != null) {
                                Util.getDatabase().getReference(mAuth.getUid()).child("gender").setValue(gender);
                            }
                        }
                        if( dataSnapshot.child("username").getValue() != null && dataSnapshot.child("name").getValue() != null) {
                            nameUser = dataSnapshot.child("name").getValue().toString();
                            //Toast.makeText( Vestium.getAppContext(), nameUser, Toast.LENGTH_SHORT).show();
                            uName = dataSnapshot.child("username").getValue().toString();
                            //Toast.makeText( Vestium.getAppContext(), uName, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            uName = mAuth.getCurrentUser().getEmail();
                            nameUser = mAuth.getCurrentUser().getDisplayName();
                            uName = uName.substring(0, uName.indexOf('@'));
                            if( uName != null) {
                                Util.getDatabase().getReference(mAuth.getUid()).child("username").setValue( uName);
                            }
                            if( nameUser != null) {
                                Util.getDatabase().getReference(mAuth.getUid()).child("name").setValue( nameUser);
                            }
                        }

                }
                catch ( Exception e) {
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mAuthStatic = mAuth;

        if (mDatabase == null) {
            mDatabase = Util.getDatabase().getReference(mAuth.getUid()).child("items");
            mDatabase.keepSynced(true);
        }
        if (lookDatabase == null) {
            lookDatabase = Util.getDatabase().getReference(mAuth.getUid()).child("looks");
            lookDatabase.keepSynced(true);
        }
        if (circleDatabase == null) {
            circleDatabase = Util.getDatabase().getReference(mAuth.getUid()).child("circle");
            circleDatabase.keepSynced(true);
        }
        if (mainRef == null) {
            mainRef = Util.getDatabase().getReference();
            mainRef.keepSynced(false);
        }
        if (pollRef == null) {
            pollRef = Util.getDatabase().getReference(mAuthStatic.getUid()).child("poll");
            pollRef.keepSynced(false);
        }



        final ImageButton buttonOne = (ImageButton) findViewById(R.id.menu_button);
        final Button logout = (Button) findViewById(R.id.button7);
        final Button wardrobe = (Button) findViewById(R.id.wardrobe_button);
        final Button settings = (Button) findViewById(R.id.settingsButton);
        final Button publicWardrobe = (Button) findViewById(R.id.publicwardrobe);
        final Button laundry = (Button) findViewById(R.id.laundryButton);
        final Button donationButton = (Button) findViewById( R.id.donationButton);
        final Button style = (Button) findViewById( R.id.styleTrendsButton);
        final Button shopping = (Button) findViewById( R.id.shoppingButton);
        syncNow = (Button) findViewById( R.id.syncNow);
        syncNow.setOnClickListener( new syncNowFunction());

        assert buttonOne != null;

        //buttonOne.setOnClickListener(new buttonsListener());
        buttonOne.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, android.view.MotionEvent event) {
            int action = event.getActionMasked();

            switch(action) {
                    case (android.view.MotionEvent.ACTION_DOWN) :
                        //android.widget.Toast.makeText(HomeScreen.this, "DOWN", android.widget.Toast.LENGTH_SHORT).show();
                        appDrawer.switchDrawer();
                        return true;

                    case (android.view.MotionEvent.ACTION_UP) :
                        //android.widget.Toast.makeText(HomeScreen.this, "UP", android.widget.Toast.LENGTH_SHORT).show();
                        appDrawer.switchDrawer();
                        return true;


            }
            return true;
            }


        });

        logout.setOnClickListener(new logoutListener());
        wardrobe.setOnClickListener( new wardrobeListener());
        settings.setOnClickListener( new settingsListener());
        publicWardrobe.setOnClickListener( new publicWardrobeListener());
        laundry.setOnClickListener( new laundryListener());
        style.setOnClickListener( new styleListener());
        shopping.setOnClickListener( new shoppingListener());
        donationButton.setOnClickListener( new donationListener());

        appDrawer = new MenuDrawer(this);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        refreshData();


    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        for(int i=0; i < tabLayout.getTabCount() - 1; i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 50, 0);
            tab.requestLayout();
        }

    }

    private void setupViewPager(ViewPager viewPager) {

        viewPager.setPageMargin(25);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "HOME");
        adapter.addFragment(new CalendarFragment(), "CALENDAR");
        adapter.addFragment(new LookbookFragment(), "LOOKBOOK");
        adapter.addFragment(new CircleFragment(), "CIRCLE");

        viewPager.setAdapter(adapter);
    }

    /*
    public class buttonsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            appDrawer.switchDrawer();
        }
    }*/

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class logoutListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mAuth.getInstance().signOut();
            Intent intent = new Intent(HomeScreen.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    public class settingsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeScreen.this, Settings.class);
            startActivity(intent);
        }
    }

    public class publicWardrobeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeScreen.this, SelectPublicWardrobe.class);
            startActivity(intent);
        }
    }

    public class shoppingListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( isNetworkAvailable()){
                Intent intent = new Intent(HomeScreen.this, Shopping.class);
                startActivity(intent);
            }
            else
                Toast.makeText( Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();

        }
    }

    public class wardrobeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeScreen.this, WardrobeActivity.class);
            //intent.putExtra("wardrobe", wardrobe);
            startActivity(intent);
        }
    }

    public class styleListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( isNetworkAvailable()){
                Intent intent = new Intent(HomeScreen.this, StyleTrends.class);
                startActivity(intent);
            }
            else
                Toast.makeText( Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();
        }
    }

    public class laundryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeScreen.this, LaundryActivity.class);
            //intent.putExtra("wardrobe", wardrobe);
            startActivity(intent);
        }
    }

    public class donationListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeScreen.this, DonationActivity.class);
            //intent.putExtra("wardrobe", wardrobe);
            startActivity(intent);
        }
    }

    public class syncNowFunction implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if( !isNetworkAvailable()) {
                Toast.makeText(getApplicationContext(),"Sync Failed: internet not available",Toast.LENGTH_LONG).show();
            }
            else {
                syncNow.setBackground(ContextCompat.getDrawable( getApplicationContext(), R.drawable.roundbuttongrey ));
                syncNow.setText("SYNCING...");
                refreshData();

            }
        }
    }
    public static boolean changeUsername( String userName) {
        Util.getDatabase().getReference(mAuthStatic.getUid()).child("username").setValue( userName);
        uName = userName;
        if( Util.getDatabase().getReference(mAuthStatic.getUid()).child("username").toString().equals( userName)) {
            return true;
        }
        return false;
    }

    public static boolean isUsernameAvailable(final String userName) {

        final Query query = mainRef;
        query.keepSynced(true);

        Util.getDatabase().getReference().child("mocks").setValue(
                "Merhaba", new DatabaseReference.CompletionListener(){

                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference afb) {

                        query.addListenerForSingleValueEvent(new ValueEventListener() {

                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try{
                                    available = true;
                                    mainRef.child("mocks").removeValue();
                                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                                        if( itemSnapshot.child("username").getValue() != null) {
                                            if( itemSnapshot.child("username").getValue().equals( userName))
                                                available = false;
                                        }
                                    }


                                } catch ( Exception e) {
                                    available = false;
                                }
                            }
                            public void onCancelled(DatabaseError error) {
                            }

                        });
                    }
                });
        return available;
    }

    public static boolean returnShit( boolean temp) {
        return temp;
    }


    public static void refreshData() {
        for( int i = 0; i < wardrobe.getData().size(); i++){
            for( int j = 0; j < wardrobe.getData().get( i).size(); j++){
                Item temp = wardrobe.getData().get( i).getItem( j);
                boolean publicThingy = temp.getPublic();
                boolean donationThingy = temp.isDonation();
                boolean laundryThingy= temp.isLaundry();
                if( publicThingy == true)
                    mDatabase.child( temp.getKey()).child("public").setValue("true");
                else
                    mDatabase.child( temp.getKey()).child("public").setValue("false");

                if( donationThingy == true)
                    mDatabase.child( temp.getKey()).child("donation").setValue("true");
                else
                    mDatabase.child( temp.getKey()).child("donation").setValue("false");

                if( laundryThingy == true)
                    mDatabase.child( temp.getKey()).child("laundry").setValue("true");
                else
                    mDatabase.child( temp.getKey()).child("laundry").setValue("false");
            }
        }
        for( int j = 0; j < laundry.getData().size(); j++){
            Item temp = laundry.getData().get( j);
            boolean laundryThingy= temp.isLaundry();

            if( laundryThingy == true)
                mDatabase.child( temp.getKey()).child("laundry").setValue("true");
            else
                mDatabase.child( temp.getKey()).child("laundry").setValue("false");
        }

        for( int j = 0; j < donation.getData().size(); j++){
            Item temp = donation.getData().get( j);
            boolean donationThingy= temp.isDonation();

            if( donationThingy == true)
                mDatabase.child( temp.getKey()).child("donation").setValue("true");
            else
                mDatabase.child( temp.getKey()).child("donation").setValue("false");
        }



        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    wardrobe.clearAllCategories();
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                        if( itemSnapshot.child("gender").getValue() != null && itemSnapshot.child("laundry").getValue() != null && itemSnapshot.child("donation").getValue() != null && itemSnapshot.child("public").getValue() != null && itemSnapshot.child("name").getValue() != null && itemSnapshot.child("image").getValue() != null
                                && itemSnapshot.child("category").getValue() != null && itemSnapshot.child("description").getValue() != null
                                && itemSnapshot.child("color") != null && itemSnapshot.child("style") != null) {
                            if( itemSnapshot.child("laundry").getValue().toString().equals("false") &&
                                    itemSnapshot.child("donation").getValue().toString().equals("false") && itemSnapshot.child("gender").getValue().toString().equals( HomeScreen.gender)) {

                                String itemName = itemSnapshot.child("name").getValue().toString();
                                String itemCategory = itemSnapshot.child("category").getValue().toString();
                                String itemImage = itemSnapshot.child("image").getValue().toString();
                                String itemDescription = itemSnapshot.child("description").getValue().toString();
                                String itemColor = itemSnapshot.child("color").getValue().toString();
                                String itemStyle = itemSnapshot.child("style").getValue().toString();
                                String isPublic= itemSnapshot.child("public").getValue().toString();
                                String isDonation = itemSnapshot.child("donation").getValue().toString();
                                String isLaundry = itemSnapshot.child("laundry").getValue().toString();
                                String itemKey = itemSnapshot.getKey();

                                if( wardrobe.getCategory( itemCategory) == null) {
                                    wardrobe.addCategory( itemCategory);
                                }

                                Item newItem = new Item( itemDescription, itemImage, itemName, itemCategory, itemColor, itemStyle, itemKey);
                                if( isPublic.equals("true"))
                                    newItem.togglePublic( true);
                                else
                                    newItem.togglePublic( false);

                                if( isDonation.equals("true"))
                                    newItem.toggleDonation( true);
                                else
                                    newItem.toggleDonation( false);

                                if( isLaundry.equals("true"))
                                    newItem.toggleLaundry( true);
                                else
                                    newItem.toggleLaundry( false);

                                Log.d( "PAGAL", wardrobe.getData().toString());

                                wardrobe.getCategory( itemCategory).addItem( newItem);
                            }

                        }
                        else {
                            itemSnapshot.getRef().removeValue();
                            //Toast.makeText( Vestium.getAppContext(), "Invalid item", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch ( Exception e) {
                    //Log.d("HomeScreen", "Everything updated! + " + e.toString());
                    refreshData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    laundry.clearLaundry();
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                        if( itemSnapshot.child("gender").getValue() != null && itemSnapshot.child("laundry").getValue() != null && itemSnapshot.child("donation").getValue() != null && itemSnapshot.child("public").getValue() != null && itemSnapshot.child("name").getValue() != null && itemSnapshot.child("image").getValue() != null
                                && itemSnapshot.child("category").getValue() != null && itemSnapshot.child("description").getValue() != null
                                && itemSnapshot.child("color") != null && itemSnapshot.child("style") != null) {
                            if( itemSnapshot.child("gender").getValue().toString().equals( HomeScreen.gender) && itemSnapshot.child("laundry").getValue().toString().equals("true")) {
                                String itemName = itemSnapshot.child("name").getValue().toString();
                                String itemCategory = itemSnapshot.child("category").getValue().toString();
                                String itemImage = itemSnapshot.child("image").getValue().toString();
                                String itemDescription = itemSnapshot.child("description").getValue().toString();
                                String itemColor = itemSnapshot.child("color").getValue().toString();
                                String itemStyle = itemSnapshot.child("style").getValue().toString();
                                String isPublic= itemSnapshot.child("public").getValue().toString();
                                String isDonation = itemSnapshot.child("donation").getValue().toString();
                                String isLaundry = itemSnapshot.child("laundry").getValue().toString();
                                String itemKey = itemSnapshot.getKey();


                                Item newItem = new Item( itemDescription, itemImage, itemName, itemCategory, itemColor, itemStyle, itemKey);
                                if( isPublic.equals("true"))
                                    newItem.togglePublic( true);
                                else
                                    newItem.togglePublic( false);

                                if( isDonation.equals("true"))
                                    newItem.toggleDonation( true);
                                else
                                    newItem.toggleDonation( false);

                                if( isLaundry.equals("true"))
                                    newItem.toggleLaundry( true);
                                else
                                    newItem.toggleLaundry( false);

                                //Log.d( "PAGAL", wardrobe.getData().toString());

                                laundry.getData().add( newItem);
                            }



                        }
                        else
                            //Toast.makeText( Vestium.getAppContext(), "Invalid item", Toast.LENGTH_SHORT).show();
                            itemSnapshot.getRef().removeValue();

                    }

                } catch ( Exception e) {
                    //Log.d("HomeScreen", "Everything updated! + " + e.toString());
                    refreshData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    donation.clearDonation();
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                        if( itemSnapshot.child("gender").getValue() != null && itemSnapshot.child("laundry").getValue() != null && itemSnapshot.child("donation").getValue() != null && itemSnapshot.child("public").getValue() != null && itemSnapshot.child("name").getValue() != null && itemSnapshot.child("image").getValue() != null
                                && itemSnapshot.child("category").getValue() != null && itemSnapshot.child("description").getValue() != null
                                && itemSnapshot.child("color") != null && itemSnapshot.child("style") != null) {
                            if( itemSnapshot.child("gender").getValue().toString().equals( HomeScreen.gender) && itemSnapshot.child("donation").getValue().toString().equals("true")) {
                                String itemName = itemSnapshot.child("name").getValue().toString();
                                String itemCategory = itemSnapshot.child("category").getValue().toString();
                                String itemImage = itemSnapshot.child("image").getValue().toString();
                                String itemDescription = itemSnapshot.child("description").getValue().toString();
                                String itemColor = itemSnapshot.child("color").getValue().toString();
                                String itemStyle = itemSnapshot.child("style").getValue().toString();
                                String isPublic= itemSnapshot.child("public").getValue().toString();
                                String isDonation = itemSnapshot.child("donation").getValue().toString();
                                String isLaundry = itemSnapshot.child("laundry").getValue().toString();
                                String itemKey = itemSnapshot.getKey();


                                Item newItem = new Item( itemDescription, itemImage, itemName, itemCategory, itemColor, itemStyle, itemKey);
                                if( isPublic.equals("true"))
                                    newItem.togglePublic( true);
                                else
                                    newItem.togglePublic( false);

                                if( isDonation.equals("true"))
                                    newItem.toggleDonation( true);
                                else
                                    newItem.toggleDonation( false);

                                if( isLaundry.equals("true"))
                                    newItem.toggleLaundry( true);
                                else
                                    newItem.toggleLaundry( false);

                                //Log.d( "PAGAL", wardrobe.getData().toString());

                                donation.getData().add( newItem);
                            }

                        }
                        else
                            //Toast.makeText( Vestium.getAppContext(), "Invalid item", Toast.LENGTH_SHORT).show();
                            itemSnapshot.getRef().removeValue();

                    }

                } catch ( Exception e) {
                    //Log.d("HomeScreen", "Everything updated! + " + e.toString());
                    refreshData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        lookDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    lookbook.clearLookbook();
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                        if( itemSnapshot.child("keys").getValue() != null && itemSnapshot.child("name").getValue() != null) {
                            String allKeys = itemSnapshot.child("keys").getValue().toString();
                            String lookName = itemSnapshot.child("name").getValue().toString();
                            String itemKey = itemSnapshot.getKey();

                            Look newLook = new Look( itemKey);
                            int begin = 0;
                            int end = 0;
                            for(int i = 0; i < allKeys.length(); i++) {
                                if( allKeys.charAt(i) == '/') {
                                    end = i;
                                    if( wardrobe.getItem( allKeys.substring(begin, end)) != null)
                                        newLook.addItem( wardrobe.getItem( allKeys.substring(begin, end)));
                                    begin = i + 1;
                                }
                            }
                            newLook.setName( lookName);
                            if( newLook.getData().size() > 0)
                                lookbook.addLook( newLook);
                        }
                        else {
                            //Toast.makeText( Vestium.getAppContext(), "Invalid Look", Toast.LENGTH_SHORT).show();
                            itemSnapshot.getRef().removeValue();
                        }
                    }

                } catch ( Exception e) {
                    Toast.makeText(Vestium.getAppContext(), e.toString(), Toast.LENGTH_LONG);

                    refreshData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final Query query = pollRef;
        query.keepSynced(true);
        Util.getDatabase().getReference().child("mocks").setValue(
                "Merhaba", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference afb) {
                        try {
                            mainRef.child("mocks").removeValue();
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try{
                                        currentPoll.clear();
                                        for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                                            if( itemSnapshot.child("key").getValue() != null && itemSnapshot.child("votes").getValue() != null) {
                                                final String itemId = itemSnapshot.getKey();
                                                final String itemKey = itemSnapshot.child("key").getValue().toString();
                                                final String votes = itemSnapshot.child("votes").getValue().toString();
                                                pollItemName = "";
                                                pollItemImage = "";
                                                if( itemKey != null) {

                                                    Util.getDatabase().getReference( mAuthStatic.getUid()).child("items").child( itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot){
                                                            if( dataSnapshot != null && dataSnapshot.child( "name").getValue() != null && dataSnapshot.child( "image").getValue() != null) {
                                                                pollItemName = dataSnapshot.child("name").getValue().toString();
                                                                pollItemImage = dataSnapshot.child("image").getValue().toString();
                                                                if( votes != null && !votes.equals("") && !pollItemImage.equals("") && !pollItemName.equals("") && pollItemName != null && pollItemImage != null) {
                                                                    //Log.d( "LAANAT", "LAANAT");
                                                                    currentPoll.getData().add(new PollItem(itemKey, pollItemName, pollItemImage, votes, itemId));
                                                                    HomeFragment.updatePoll();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                    //Toast.makeText( Vestium.getAppContext(), "" + currentPoll.getData().size(), Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    Log.d("LAANAT", "NULL");
                                                }
                                            }
                                        }

                                    } catch ( Exception e) {
                                        Log.d("ERRORORORORORO", e.toString());
                                        Toast.makeText(Vestium.getAppContext(), e.toString(), Toast.LENGTH_LONG);
                                        //refreshData();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });;
                        } catch (Exception e) {
                        }
                    }
                });


        circleDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    circle.clearCircle();
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                        if( itemSnapshot.child("uid").getValue() != null) {
                            final String userid = itemSnapshot.child("uid").getValue().toString();
                            final String userkey = itemSnapshot.getKey();
                            if( userid != null) {
                                Util.getDatabase().getReference( userid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //String name = dataSnapshot.getValue(String.class);
                                        String name = dataSnapshot.getValue().toString();
                                        //Toast.makeText( Vestium.getAppContext(), name,Toast.LENGTH_LONG).show();
                                        if( name != null){
                                            Friend newFriend = new Friend( name, userid, userkey);
                                            circle.getList().add( newFriend);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }
                    syncNow.setBackground(ContextCompat.getDrawable( Vestium.getAppContext(), R.drawable.roundbutton ));
                    syncNow.setText("SYNC");
                    Toast.makeText( Vestium.getAppContext(), "Data successfully updated", Toast.LENGTH_SHORT).show();


                } catch ( Exception e) {
                    Toast.makeText(Vestium.getAppContext(), e.toString(), Toast.LENGTH_LONG);
                    refreshData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        wardrobe.refreshWardrobe();

    }





    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


}