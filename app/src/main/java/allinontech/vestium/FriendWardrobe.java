package allinontech.vestium;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import allinontech.vestium.assets.PhotoFullPopupWindow;
import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Category;
import allinontech.vestium.backend.Wardrobe;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.ValueEventListener;

import allinontech.vestium.backend.Item;
import allinontech.vestium.backend.Wardrobe;

public class FriendWardrobe extends AppCompatActivity {

    private RecyclerView categoryHolder;
    //private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Wardrobe wardrobe;
    private DatabaseReference friendWardrobe;
    private TextView header;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_wardrobe);

        //test = (LinearLayout) findViewById( R.id.noItem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarwardrobe);
        setSupportActionBar(toolbar);

        header = findViewById( R.id.header);

        Bundle extras = getIntent().getExtras();
        String userId = null;

        if(!isNetworkAvailable()){
            Toast.makeText( Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(extras != null) {
            userId = extras.getString("uid");
        }
        else{
            Toast.makeText( Vestium.getAppContext(), "Error while retrieving wardrobe", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(userId == null) {
            Toast.makeText( Vestium.getAppContext(), "Error while retrieving wardrobe", Toast.LENGTH_SHORT).show();
            finish();
        }
        //Toast.makeText( Vestium.getAppContext(), userId, Toast.LENGTH_SHORT).show();

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        String nameFinal = HomeScreen.circle.findFriendFull( userId).getName();
        int indexFinal = nameFinal.indexOf(' ');
        if( indexFinal > 0)
            nameFinal = nameFinal.substring(0, indexFinal);
        ab.setTitle( nameFinal.toUpperCase() + "'s WARDROBE");
        header.setText( nameFinal.toUpperCase() + "'s WARDROBE");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)



        mAuth = FirebaseAuth.getInstance();
        wardrobe = new Wardrobe();
        final Adapter adapter = new Adapter( FriendWardrobe.this, wardrobe);
        if( friendWardrobe == null){
            friendWardrobe = FirebaseDatabase.getInstance().getReference().child( userId).child("items");
            friendWardrobe.keepSynced( false);
        }
        friendWardrobe = FirebaseDatabase.getInstance().getReference().child( userId).child("items");
        friendWardrobe.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    wardrobe.clearAllCategories();
                    for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                        //Toast.makeText( Vestium.getAppContext(), itemSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                        //
                        if( itemSnapshot.child("public") != null && itemSnapshot.child("name").getValue() != null && itemSnapshot.child("image").getValue() != null
                                && itemSnapshot.child("category").getValue() != null && itemSnapshot.child("description").getValue() != null
                                && itemSnapshot.child("color") != null && itemSnapshot.child("style") != null) {
                            if( itemSnapshot.child("public").getValue().toString().equals("true") )
                            {
                                //Toast.makeText( Vestium.getAppContext(), "Eneteete", Toast.LENGTH_SHORT).show();
                                String itemName = itemSnapshot.child("name").getValue().toString();
                                String itemCategory = itemSnapshot.child("category").getValue().toString();
                                String itemImage = itemSnapshot.child("image").getValue().toString();
                                String itemDescription = itemSnapshot.child("description").getValue().toString();
                                String itemColor = itemSnapshot.child("color").getValue().toString();
                                String itemStyle = itemSnapshot.child("style").getValue().toString();
                                String itemKey = itemSnapshot.getKey();

                                if( wardrobe.getCategory( itemCategory) == null) {
                                    wardrobe.addCategory( itemCategory);
                                }

                                Item newItem = new Item( itemDescription, itemImage, itemName, itemCategory, itemColor, itemStyle, itemKey);
                                //newItem.togglePublic( true);
                                //Log.d( "PAGAL", wardrobe.getData().toString());

                                wardrobe.getCategory( itemCategory).addItem( newItem);
                                adapter.notifyDataSetChanged();
                                //Toast.makeText( Vestium.getAppContext(), "Wardrobe: " + wardrobe.getCategoryCount(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText( Vestium.getAppContext(), "Invalid item", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch ( Exception e) {
                    Toast.makeText( Vestium.getAppContext(), "Error loading wardrobe", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText( Vestium.getAppContext(), "Error loading wardrobe", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        categoryHolder = (RecyclerView) findViewById( R.id.recyclerViewFriend);
        categoryHolder.setLayoutManager( new LinearLayoutManager( FriendWardrobe.this));
        categoryHolder.setAdapter( adapter);
        categoryHolder.invalidate();


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private class MyAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        Context c;
        Category items;

        public MyAdapter(Context c, Category category) {
            this.c = c;
            this.items = category;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_wardrobe_item, parent, false);
            return new ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder( final ItemViewHolder holder, int position) {
            final int index = position;

            holder.setName( this.items.getItem( position).getName());
            holder.setImage( c, this.items.getItem( position).getImage());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PhotoFullPopupWindow( Vestium.getAppContext(), R.layout.popup_photo_full, holder.returnView(), items.getItem( index).getImage(), null);
                }
            });

            /*
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final CharSequence colors[] = new CharSequence[] {"Remove Item"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( FriendWardrobe.this);
                    builder.setTitle( items.getItem( index).getName());
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference( mAuth.getUid()+"/items").child( items.getItem( index).getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Toast.makeText( Vestium.getAppContext(), "Item successfully deleted", Toast.LENGTH_SHORT).show();
                                    HomeScreen.refreshData();
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                        }
                    });
                    builder.show();
                    return true;
                }
            });
            */
            //Toast.makeText( getApplicationContext(), this.items.get( position).getImage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return items.size();

        }
    }


    //-----------------
    private class Adapter extends RecyclerView.Adapter<CategoryViewHolder> {
        Context c;
        Wardrobe items;
        int[] colors = {Color.rgb(113, 34, 138), Color.rgb(28, 109, 200), Color.rgb(238, 19, 19), Color.rgb(232, 128, 10)};

        public Adapter(Context c, Wardrobe items) {
            this.c = c;
            this.items = items;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_wardrobe_category, parent, false);
            //Toast.makeText( Vestium.getAppContext(), "Binded", Toast.LENGTH_SHORT).show();
            return new CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            final int index = position;
            //Toast.makeText(Vestium.getAppContext(), "Position" + index, Toast.LENGTH_SHORT).show();
            holder.setTitle( this.items.getData().get( position).getTitle());
            if( position > colors.length - 1) {
                holder.setColor( colors[ position % (colors.length)]);
            }
            else
                holder.setColor( colors[ position]);
            holder.setRecyclerView( c, this.items.getData().get( position).getTitle());
        }

        @Override
        public int getItemCount() {
            //Toast.makeText( Vestium.getAppContext(), "Counted", Toast.LENGTH_SHORT).show();
            //Toast.makeText( Vestium.getAppContext(), "count : " + this.items.getCategoryCount(), Toast.LENGTH_SHORT).show();
            return this.items.getCategoryCount();
        }
    }

    //-
    private class CategoryViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setColor( int color) {
            LinearLayout layout = mView.findViewById( R.id.textBack);
            layout.setBackgroundColor( color);
        }
        public void setTitle( String description) {
            TextView text = (TextView) mView.findViewById( R.id.categoryName);
            text.setText( description.toUpperCase());
        }
        public void setRecyclerView( Context ctx, String category) {

            RecyclerView view = (RecyclerView) mView.findViewById( R.id.recyclerViewCategory);
            int spanCount = 2;
            int spacing = 25; // 50px
            boolean includeEdge = false;
            view.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            MyAdapter adapter = new MyAdapter( FriendWardrobe.this, wardrobe.getCategory( category));
            view.setAdapter( adapter);
            view.invalidate();


        }
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

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

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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

