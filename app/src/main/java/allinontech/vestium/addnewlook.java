package allinontech.vestium;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import java.util.ArrayList;

import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Category;
import allinontech.vestium.backend.Item;
import allinontech.vestium.backend.Wardrobe;
import allinontech.vestium.fragments.LookbookFragment;

public class addnewlook extends AppCompatActivity {

    private RecyclerView categoryList2;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private Wardrobe wardrobe;
    private LinearLayout test;

    private FloatingActionButton createLook;
    private MaterialTextField lookName;


    //private String[] categoryList = {"shirts", "pants", "hats", "shoes", "belts"};

    private ArrayList<Item> selectedItem;

    //private FirebaseRecyclerAdapter<Item, ItemViewHolder> FBRA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewlook);

        test = (LinearLayout) findViewById( R.id.noItem);
        lookName = (MaterialTextField) findViewById( R.id.ffname);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarwardrobe);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mRef = Util.getDatabase().getReference( mAuth.getUid()+"/looks");

        selectedItem = new ArrayList<>();
        createLook = findViewById( R.id.createLook);
        createLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( selectedItem.size() == 0)
                    Toast.makeText( getApplicationContext(), "Please select atleast one item", Toast.LENGTH_SHORT).show();
                else {
                    String tempName = lookName.getEditText().getText().toString().trim();
                    if( tempName == null || tempName.equals(""))
                        tempName = "Look";
                    String value = "";
                    final DatabaseReference newItem = mRef.push();
                    for( Item temp: selectedItem){
                        value = value + temp.getKey() + "/";
                    }
                    newItem.child("keys").setValue(value);
                    newItem.child("name").setValue( tempName);
                    Toast.makeText( Vestium.getAppContext(), "Look Added Successfully", Toast.LENGTH_SHORT).show();
                    HomeScreen.refreshData();
                    LookbookFragment.refresh();
                    onBackPressed();
                }
            }
        });
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("CREATE LOOK");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)


        wardrobe = HomeScreen.wardrobe;


        categoryList2 = (RecyclerView) findViewById(R.id.recyclerViewWhole);
        categoryList2.setLayoutManager( new LinearLayoutManager( addnewlook.this));
        addnewlook.Adapter adapter = new Adapter( addnewlook.this, wardrobe);
        categoryList2.setAdapter( adapter);
        categoryList2.invalidate();





    }

    public class MyAdapter extends RecyclerView.Adapter<addnewlook.ItemViewHolder> {
        Context c;
        Category items;

        public MyAdapter(Context c, Category category) {
            this.c = c;
            this.items = category;
        }

        @Override
        public addnewlook.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_wardrobe_item, parent, false);
            return new addnewlook.ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final addnewlook.ItemViewHolder holder, final int position) {
            //final int index = position;

            holder.setName( this.items.getItem( position).getName());
            holder.setImage( c, this.items.getItem( position).getImage());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem.add( items.getItem( position));
                    holder.toggle( true);
                    //Toast.makeText( getApplicationContext(), "Selected", Toast.LENGTH_SHORT).show();
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = findItem( selectedItem, items.getItem( position).getKey());
                    if( index != -1){
                        selectedItem.remove( index);
                        holder.toggle( false);
                        //Toast.makeText( getApplicationContext(), "De   Selected", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return items.size();

        }
    }


    //-----------------
    public class Adapter extends RecyclerView.Adapter<addnewlook.CategoryViewHolder> {
        Context c;
        Wardrobe items;
        int[] colors = {Color.rgb(113, 34, 138), Color.rgb(28, 109, 200), Color.rgb(238, 19, 19), Color.rgb(232, 128, 10)};

        public Adapter(Context c, Wardrobe items) {
            this.c = c;
            this.items = items;
        }

        @Override
        public addnewlook.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_wardrobe_category, parent, false);
            return new addnewlook.CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(addnewlook.CategoryViewHolder holder, int position) {
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
            return this.items.getCategoryCount();
        }
    }

    //-
    public class CategoryViewHolder extends RecyclerView.ViewHolder {

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
            view.addItemDecoration(new addnewlook.GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            addnewlook.MyAdapter adapter = new addnewlook.MyAdapter( addnewlook.this, wardrobe.getCategory( category));
            view.setAdapter( adapter);
            view.invalidate();


        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void toggle( boolean selected) {
            if( selected)
                mView.setBackgroundColor(Color.GREEN);
            else
                mView.setBackgroundColor( Color.WHITE);
        }

        public void setDescription( String description) {
            //to be added
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

    public int findItem( ArrayList<Item> data, String code) {
        for( int i = 0; i < data.size(); i++) {
            if( data.get(i).getKey().equals( code))
                return i;

        }
        return -1;
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
