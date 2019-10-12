package allinontech.vestium.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import allinontech.vestium.Additem;
import allinontech.vestium.FriendWardrobe;
import allinontech.vestium.HomeScreen;
import allinontech.vestium.LaundryActivity;
import allinontech.vestium.R;
import allinontech.vestium.Vestium;
import allinontech.vestium.WardrobeActivity;
import allinontech.vestium.assets.PhotoFullPopupWindow;
import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Category;
import allinontech.vestium.backend.Item;
import allinontech.vestium.backend.LookGenerator;
import allinontech.vestium.backend.Poll;
import allinontech.vestium.backend.PollItem;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment{


    private static final int GALLREQ = 1;
    private static final int CAMERA_REQUEST = 2;

    public static HomeFragment.MyAdapter adapter;
    public static HomeFragment.LookMyAdapter adapter1;

    public static ArrayList<Item> currentLook;

    private ImageView imageView;
    private TextView textViewName;
    private FirebaseAuth mAuth;
    private Button newItem;
    public static Button reloadTheLook;
    private RecyclerView pollsView;
    private RecyclerView looksView;
    private Poll poll;
    private Button buttonLaundry;
    private ProgressBar loading;


    // this is the path of the taken image
    String mCurrentPhotoPath;
    // this is the uri of the taken image
    private Uri photoURI;


    private AdView adview;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public class captureListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( isNetworkAvailable()){
                CharSequence colors[] = new CharSequence[] {"CAMERA", "GALLERY"};

                mCurrentPhotoPath = "";

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("ADD IMAGE FROM?");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if( which == 0)
                        {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                // Create the File where the photo should go

                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    //ADD stuff
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    HomeFragment.this.photoURI = FileProvider.getUriForFile(getContext(),
                                            "allinontech.vestium.fileprovider",
                                            photoFile);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                            }
                        }
                        else if ( which == 1)
                        {
                            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            galleryIntent.setType("image/*");
                            startActivityForResult(galleryIntent, GALLREQ);

                        }
                    }
                });
                builder.show();
            }
            else
                Toast.makeText(Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();



        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.homefragment, container,false);
        //Showing the username and image etc
        mAuth = FirebaseAuth.getInstance();


        reloadTheLook = v.findViewById( R.id.reloadLook);
        reloadTheLook.setVisibility( View.GONE);
        pollsView = v.findViewById( R.id.pollView);
        looksView = v.findViewById( R.id.todaysLook);
        imageView = v.findViewById(R.id.imageView);
        textViewName = v.findViewById(R.id.textViewName);
        newItem = v.findViewById(R.id.buttoncapture);
        loading = v.findViewById( R.id.progressBarLook);

        adview = v.findViewById( R.id.homeAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        buttonLaundry = v.findViewById( R.id.buttonlaundry);
        buttonLaundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getActivity(), LaundryActivity.class);
                startActivity(intent);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                reloadTheLook.setVisibility( View.VISIBLE);
            }
        }, 3500);



        final int spanCount = 2;
        final int spacing = 25; // 50px
        final boolean includeEdge = false;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getLook();
                looksView.setLayoutManager(new GridLayoutManager( getContext(), 2));
                looksView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(spanCount, spacing, includeEdge));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //Log.d( "BBSBSBSBS", currentLook.size() + "");
                        //Toast.makeText( Vestium.getAppContext(), "cbcbc" + currentLook.size(), Toast.LENGTH_SHORT).show();
                        adapter1 = new HomeFragment.LookMyAdapter( getContext(), currentLook);
                        looksView.setAdapter( adapter1);
                        looksView.invalidate();
                        adapter1.notifyDataSetChanged();
                    }
                }, 500);
            }
        }, 3000);






        reloadTheLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLook();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //Log.d( "BBSBSBSBS", currentLook.size() + "");
                        //Toast.makeText( Vestium.getAppContext(), "cbcbc" + currentLook.size(), Toast.LENGTH_SHORT).show();
                        adapter1 = new HomeFragment.LookMyAdapter( getContext(), currentLook);
                        looksView.setAdapter( adapter1);
                        looksView.invalidate();
                        adapter1.notifyDataSetChanged();
                    }
                }, 500);
            }
        });


        newItem.setOnClickListener(new captureListener());
        poll = HomeScreen.currentPoll;

        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);


        String name = user.getDisplayName().toUpperCase();
        int i = -1;
        do {
            i++;
        } while(name.charAt(i) != ' ' && i < 15);
        name = name.substring(0, i);
        textViewName.setText(name);




        pollsView.setLayoutManager(new GridLayoutManager( getContext(), 2));
        pollsView.addItemDecoration(new HomeFragment.GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        adapter = new HomeFragment.MyAdapter( getContext(), poll);
        pollsView.setAdapter( adapter);
        pollsView.invalidate();


        return v;
    }

    public static void updatePoll(){
        adapter.notifyDataSetChanged();
    }

    public void getLook(){
		/*
		ArrayList<Item> temp = LookGenerator.generateLook();
		while( temp == null){
			temp = c
		}
		return temp;

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				currentLook =  LookGenerator.generateLook();
			}
		}, );
*/
        currentLook =  LookGenerator.generateLook();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLREQ && resultCode == Activity.RESULT_OK)
        {

            photoURI = (Uri) data.getData();
            //imageView.setImageURI(photoURI);


            Intent intent = new Intent(getActivity(), Additem.class);
            intent.putExtra("image", photoURI);
            startActivity(intent);
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);

            //photoURI = (Uri) data.getData();
            //imageView.setImageURI(photoURI);

            Intent intent = new Intent(getActivity(), Additem.class);
            intent.putExtra("image", photoURI);
            startActivity(intent);

        }
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
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //System.out.println(storageDir);


        // rest uses the storageDir directory and makes a unique File object called image
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // store the current path of the image file
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class LookMyAdapter extends RecyclerView.Adapter<HomeFragment.LookItemViewHolder> {
        Context c;
        ArrayList<Item> items;

        public LookMyAdapter(Context c, ArrayList<Item> items) {
            this.c = c;
            this.items = items;
        }

        @Override
        public HomeFragment.LookItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_wardrobe_item, parent, false);
            return new HomeFragment.LookItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final HomeFragment.LookItemViewHolder holder, int position) {
            final int index = position;


            //Toast.makeText( Vestium.getAppContext(), "Binded"+ position, Toast.LENGTH_SHORT).show();
            holder.setName( this.items.get( index).getName());
            holder.setImage( c, this.items.get( index).getImage());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PhotoFullPopupWindow( Vestium.getAppContext(), R.layout.popup_photo_full, holder.returnView(), items.get( index).getImage(), null);
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.d("Get Data", "" + items.size());
            return items.size();
        }
    }



    private class MyAdapter extends RecyclerView.Adapter<HomeFragment.ItemViewHolder> {
        Context c;
        Poll items;
        int highest;

        public MyAdapter(Context c, Poll poll) {
            this.c = c;
            this.items = poll;
        }

        @Override
        public HomeFragment.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_current_poll_item, parent, false);
            highest = 0;
            for(PollItem temp: this.items.getData()){
                if( countVotes(temp.getVotes()) > highest)
                    highest = countVotes(temp.getVotes());
            }

            return new HomeFragment.ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final HomeFragment.ItemViewHolder holder, int position) {
            final int index = position;

            //Toast.makeText( Vestium.getAppContext(), "Binded"+ position, Toast.LENGTH_SHORT).show();
            holder.setName( this.items.getData().get( index).getName());
            holder.setImage( c, this.items.getData().get( index).getImage());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PhotoFullPopupWindow( Vestium.getAppContext(), R.layout.popup_photo_full, holder.returnView(), items.getData().get( index).getImage(), null);
                }
            });
            holder.setVotes( "" + countVotes(this.items.getData().get( index).getVotes()));
            Toast.makeText( Vestium.getAppContext(), "Updating polls...", Toast.LENGTH_SHORT).show();
            if( countVotes(this.items.getData().get( index).getVotes()) >= highest)
                holder.makeGreen();

        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return items.getData().size();

        }
    }
    public int countVotes( String value){
        int count = 0;
        for( int i = 0; i < value.length(); i++){
            if( value.charAt( i) == '/'){
                count++;
            }
        }
        count--;
        return count;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void makeGreen(){
            TextView votesView = (TextView) mView.findViewById( R.id.itemVotes);
            votesView.setTextColor(Color.GREEN);
        }

        public void setVotes( String votes) {
            TextView votesView = (TextView) mView.findViewById( R.id.itemVotes);
            votesView.setText( votes.toUpperCase());
        }
        public void setImage( Context ctx, String image) {
            //Toast.makeText( ctx, image, Toast.LENGTH_LONG).show();
            ImageView itemImage = (ImageView) mView.findViewById(R.id.itemImage);

            Glide.with(ctx)
                    .load(image)
                    .into(itemImage);
            //Picasso.get().
        }
        public ImageView returnView(){
            ImageView itemImage = (ImageView) mView.findViewById(R.id.itemImage);
            return itemImage;
        }
        public void setName( String name) {
            TextView itemName = (TextView) mView.findViewById(R.id.itemName);
            itemName.setText( name.toUpperCase());
            //Toast.makeText( getApplicationContext(), "Merhaba", Toast.LENGTH_SHORT).show();
        }


    }

    private class LookItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public LookItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setImage( Context ctx, String image) {
            //Toast.makeText( ctx, image, Toast.LENGTH_LONG).show();
            ImageView itemImage = (ImageView) mView.findViewById(R.id.itemImage);

            Glide.with(ctx)
                    .load(image)
                    .into(itemImage);
            //Picasso.get().
        }
        public ImageView returnView(){
            ImageView itemImage = (ImageView) mView.findViewById(R.id.itemImage);
            return itemImage;
        }
        public void setName( String name) {
            TextView itemName = (TextView) mView.findViewById(R.id.itemName);
            itemName.setText( name.toUpperCase());
            //Toast.makeText( getApplicationContext(), "Merhaba", Toast.LENGTH_SHORT).show();
        }


    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }



}