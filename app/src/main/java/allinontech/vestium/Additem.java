package allinontech.vestium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import allinontech.vestium.assets.Constants;
import allinontech.vestium.assets.Util;

public class Additem extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewName;
    private Uri imageUri;
    private ProgressBar progressBar;

    private MaterialTextField name;
    private MaterialTextField description;

    private Button categoryButton;
    private Button colorButton;
    private Button styleButton;

    private String category;
    private String color;
    private String style;


    private Button addItem;

    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private byte[] byteArray;

    FirebaseAuth mAuth;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        FirebaseStorage.getInstance().setMaxUploadRetryTimeMillis(8000);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.ItemAddAd);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaradditem);
        setSupportActionBar(toolbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("ADD ITEM");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        progressBar = findViewById(R.id.progressBar);

        if( !isNetworkAvailable()) {
            Toast.makeText( getApplication(), "Cannot add item: Internet unavailable", Toast.LENGTH_LONG).show();
            onBackPressed();
        }


        categoryButton = findViewById( R.id.categoryButton);
        colorButton = findViewById( R.id.colorButton);
        styleButton = findViewById( R.id.styleButton);

        categoryButton.setOnClickListener( new categoryListener());
        colorButton.setOnClickListener( new colorListener());
        styleButton.setOnClickListener( new styleListener());



        imageView = findViewById(R.id.imageView1);
        imageUri = (Uri) getIntent().getParcelableExtra("image");
        //Bitmap photo = BitmapFactory.decodeFile( imageUri.toString());



        //uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(
                    imageUri);
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Item Add failed", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        byteArray = stream.toByteArray();
        try {
            stream.close();
            stream = null;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Item Add failed", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }



        imageView.setImageURI(imageUri);

        name = (MaterialTextField) findViewById(R.id.item_name);
        description = (MaterialTextField) findViewById(R.id.item_description);

        mAuth = FirebaseAuth.getInstance();

        mRef = Util.getDatabase().getReference( mAuth.getUid()+"/items");

        addItem = (Button) findViewById(R.id.additembutton);
        addItem.setOnClickListener( new View.OnClickListener(){
            public void onClick(View arg0) {
                onAddClicked();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("ItemImages/" + mAuth.getUid());


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private File createImageFile() throws IOException {
        // variables
        String timeStamp;
        String imageFileName;
        String albumName;
        Date date;
        File storageDir;
        File image;


        // Create an image file name
        date = new Date();
        albumName = new SimpleDateFormat("yyyyMMdd").format( date);
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format( date);
        imageFileName = "VESTIUM_" + timeStamp + "_";

        // albumName is just a name which has todays date.. I want to make a subfoler and add files there
        // folder in the apps directory, which is available only to this app
        storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //System.out.println(storageDir);


        // rest uses the storageDir directory and makes a unique File object called image
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public class categoryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            //changed
            final CharSequence categories[];
            if (HomeScreen.gender.equals("male")) {
                categories= new CharSequence[]{"Hats", "Scarfs", "Sunglasses", "Rings", "Shirts", "T-shirts"
                        , "Polo Shirts", "Sweaters", "Jackets", "Blazers", "Coats"
                        , "Watches", "Belts", "Suits", "Pants", "Jeans", "Chinos", "Shoes"};
            } else{
                categories = new CharSequence[]{"Hats", "Scarfs", "Sunglasses", "Necklaces", "Earrings",
                        "Rings", "Shirts", "T-shirts"
                        , "Polo Shirts", "Sweaters", "Jackets", "Blazers", "Coats"
                        , "Watches", "Belts", "Dresses", "Suits", "Skirts", "Pants", "Jeans", "Chinos", "Shoes", "Heels"};
            }

            AlertDialog.Builder builder = new AlertDialog.Builder( Additem.this);
            builder.setTitle("Select a category:");
            builder.setItems(categories, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    category = "" + categories[ which];
                    categoryButton.setText( category);
                }
            });
            builder.show();

        }
    }

    public class colorListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            //changed
            final CharSequence colors[] = new CharSequence[] {"Red", "Black", "White", "Blue", "Yellow", "Gray"
                    , "Cyan", "Green", "Magenta", "Orange", "Pink"};
            AlertDialog.Builder builder = new AlertDialog.Builder( Additem.this);
            builder.setTitle("Select a color:");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    color = "" + colors[ which];
                    colorButton.setText( colors[ which]);
                }
            });
            builder.show();

        }
    }

    public class styleListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            final CharSequence styles[] = new CharSequence[] {"casual", "formal", "party"};
            AlertDialog.Builder builder = new AlertDialog.Builder( Additem.this);
            builder.setTitle("Select a style:");
            builder.setItems(styles, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    style = "" + styles[ which];
                    styleButton.setText( styles[ which]);
                }
            });
            builder.show();

        }
    }

    public void onAddClicked() {
        final String name_text = name.getEditText().getText().toString().trim();
        final String desription_text = description.getEditText().getText().toString().trim();
        final String tempColor = color;
        final String tempCategory = category;
        final String tempStyle = style;



        try {
            if( !TextUtils.isEmpty( name_text) && color != null && category != null && style != null) {
                addItem.setEnabled( false);
                StorageReference filepath = storageReference.child( "" + Math.abs(imageUri.hashCode()));
                filepath.putBytes( byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri downloadurl = taskSnapshot.getDownloadUrl();
                        if( downloadurl != null ) {


                            addItem.setBackground(ContextCompat.getDrawable( getApplicationContext(), R.drawable.roundbuttongrey ));
                            addItem.setText( "ADDING...");


                            final DatabaseReference newItem = mRef.push();
                            newItem.child("name").setValue(name_text);
                            newItem.child("description").setValue(desription_text);
                            newItem.child("image").setValue( downloadurl.toString());
                            newItem.child("category").setValue( tempCategory);
                            newItem.child("color").setValue( tempColor);
                            newItem.child("style").setValue( tempStyle);
                            newItem.child("public").setValue("false");
                            newItem.child("laundry").setValue("false");
                            newItem.child("donation").setValue("false");
                            newItem.child("gender").setValue( HomeScreen.gender);
                            Toast.makeText( Additem.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                            HomeScreen.refreshData();
                            onBackPressed();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility( View.VISIBLE);
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                        int currentprogress = (int) progress;
                        progressBar.setProgress(currentprogress);
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText( getApplicationContext(), "Internet connection not available", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else {
                Toast.makeText( getApplicationContext(), "The item must have a Name, Category, Color and Style", Toast.LENGTH_SHORT).show();
            }
        } catch( Exception e) {
            Toast.makeText(getApplicationContext(), "Item could not be added", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onBackPressed(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onBackPressed();
    }


}

