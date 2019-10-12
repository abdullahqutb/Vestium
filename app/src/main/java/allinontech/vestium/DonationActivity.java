package allinontech.vestium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import allinontech.vestium.assets.PhotoFullPopupWindow;
import allinontech.vestium.backend.Wardrobe;

public class DonationActivity extends AppCompatActivity {

    private static RecyclerView categoryHolder;
    //private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private allinontech.vestium.backend.Donation donation;
    private Wardrobe wardrobe;
    private Button addDonation;

    public static DonationActivity.MyAdapter adapter;
    //private LinearLayout test;

    private AdView adview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);


        donation = HomeScreen.donation;
        //test = (LinearLayout) findViewById( R.id.noItem);

        adview = findViewById( R.id.donationsAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        addDonation = findViewById( R.id.additembutton);
        addDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonationActivity.this, addDonation.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarlaundry);
        setSupportActionBar(toolbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("DONATION");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)



        mAuth = FirebaseAuth.getInstance();
        /*
        wardrobe = HomeScreen.wardrobe;
        for ( int i = 0; i < wardrobe.getCategoryCount(); i++ ) {
            for ( int j = 0; j < wardrobe.getData().get(i).size(); j++ ) {
                if ( wardrobe.getData().get(i).getItem(j).isLaundry() ) {
                    laundry.getData().add(wardrobe.getData().get(i).getItem(j));
                }
            }
        }
        */
        categoryHolder = (RecyclerView) findViewById( R.id.recyclerViewWhole);
        int spanCount = 2;
        int spacing = 25; // 50px
        boolean includeEdge = false;
        categoryHolder.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        categoryHolder.addItemDecoration(new DonationActivity.GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        adapter = new DonationActivity.MyAdapter( DonationActivity.this, donation);
        categoryHolder.setAdapter( adapter);
        categoryHolder.invalidate();


    }
    public static void refresh(){
        HomeScreen.refreshData();
        adapter.notifyDataSetChanged();
        categoryHolder.invalidate();
    }



    public class MyAdapter extends RecyclerView.Adapter<DonationActivity.ItemViewHolder> {
        Context c;
        allinontech.vestium.backend.Donation items;

        public MyAdapter(Context c, allinontech.vestium.backend.Donation input) {
            this.c = c;
            this.items = input;
        }

        @Override
        public DonationActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_wardrobe_item, parent, false);
            return new DonationActivity.ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder( final DonationActivity.ItemViewHolder holder, int position) {
            final int index = position;

            holder.setName( this.items.getData().get( position).getName());
            holder.setImage( c, this.items.getData().get( position).getImage());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PhotoFullPopupWindow( Vestium.getAppContext(), R.layout.popup_photo_full, holder.returnView(), items.getData().get( index).getImage(), null);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final CharSequence colors[] = new CharSequence[] {"Remove Item"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( DonationActivity.this);
                    builder.setTitle( items.getData().get(index).getName());
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            donation.getData().get( index).toggleDonation( false);
                            HomeScreen.refreshData();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    refresh();
                                }
                            }, 500);
                            /*
                            String key = laundry.getData().get( index).getKey();
                            for( int i = 0; i < wardrobe.getData().size(); i++){
                                for( int j = 0; j < wardrobe.getData().get( i).size(); j++) {
                                    if( wardrobe.getData().get( i).getItem( j).getKey().equals( key))
                                        wardrobe.getData().get( i).getItem( j).toggleLaundry( false);
                                }
                            }
                            */
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    builder.show();
                    return true;
                }
            });
            //Toast.makeText( getApplicationContext(), this.items.get( position).getImage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return items.getData().size();

        }
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescription( String description) {
            //to be added
        }
        public ImageView returnView(){
            ImageView itemImage = (ImageView) mView.findViewById(R.id.itemImage);
            return itemImage;
        }
        public void setImage( Context ctx, String image) {
            //Toast.makeText( ctx, image, Toast.LENGTH_LONG).show();
            ImageView itemImage = (ImageView) mView.findViewById(R.id.itemImage);

            Glide.with(ctx)
                    .load(image)
                    .into(itemImage);
            //Picasso.get().
        }
        public void setName( String name) {
            TextView itemName = (TextView) mView.findViewById(R.id.itemName);
            itemName.setText( name.toUpperCase());
            //Toast.makeText( getApplicationContext(), "Merhaba", Toast.LENGTH_SHORT).show();
        }


    }






    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
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
