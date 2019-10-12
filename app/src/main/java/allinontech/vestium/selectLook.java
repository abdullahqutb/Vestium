package allinontech.vestium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Look;
import allinontech.vestium.backend.Lookbook;
import allinontech.vestium.backend.Wardrobe;

public class selectLook extends AppCompatActivity {

    private RecyclerView looksRecyclerView;
    private SelectLookAdapter mAdapter;
    private Lookbook lookbook;
    private LinearLayout test;

    private FloatingActionButton selectButton;

    private ArrayList<Look> selectedLooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_look);

        test = (LinearLayout) findViewById( R.id.selectLooknoItem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarselectLook);
        setSupportActionBar(toolbar);




        selectedLooks = new ArrayList<>();
        selectButton = findViewById(R.id.selectLook);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( selectedLooks.size() == 0)
                    Toast.makeText( getApplicationContext(), "Please select atleast one item", Toast.LENGTH_SHORT).show();
                else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",selectedLooks);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("SELECT LOOK");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        lookbook = HomeScreen.lookbook;

        looksRecyclerView = (RecyclerView) findViewById(R.id.selectLookRecyclerView);
        looksRecyclerView.setLayoutManager( new LinearLayoutManager( this));
        mAdapter = new SelectLookAdapter( this, lookbook);
        looksRecyclerView.setAdapter( mAdapter);
        looksRecyclerView.invalidate();
    }

    public class SelectLookAdapter extends RecyclerView.Adapter<SelectItemLookViewHolder> {
        Context c;
        Lookbook internallooks;

        public SelectLookAdapter(Context c, Lookbook looks) {
            this.c = c;
            this.internallooks = looks;
        }

        @Override
        public SelectItemLookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View lookLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lookbook_item, null);

            // create ViewHolder

            SelectItemLookViewHolder viewHolder = new SelectItemLookViewHolder( lookLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final SelectItemLookViewHolder holder, final int position) {
            final int index = position;

            holder.setImage( c, internallooks.getData().get( position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedLooks.add( internallooks.getData().get( position));
                    holder.toggle( true);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = selectedLooks.indexOf( internallooks.getData().get( position));
                    selectedLooks.remove( index);
                    holder.toggle( false);
                    return true;
                }
            });


        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return internallooks.getData().size();

        }
    }

    public class SelectItemLookViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public SelectItemLookViewHolder( View lookView) {
            super(lookView);
            mView = lookView;
        }

        public void toggle( boolean selected) {
            if( selected)
                mView.setBackgroundColor(Color.GREEN);
            else
                mView.setBackgroundColor( Color.WHITE);
        }

        public void setImage( Context c, Look look) {
            //Toast.makeText( ctx, image, Toast.LENGTH_LONG).show();
            ImageView itemImage1 = (ImageView) mView.findViewById(R.id.image1);
            ImageView itemImage2 = (ImageView) mView.findViewById(R.id.image2);
            ImageView itemImage3 = (ImageView) mView.findViewById(R.id.image3);
            ImageView itemImage4 = (ImageView) mView.findViewById(R.id.image4);
            ImageView itemImage5 = (ImageView) mView.findViewById(R.id.image5);
            ImageView itemImage6 = (ImageView) mView.findViewById(R.id.image6);

            ImageView[] viewdata = { itemImage1, itemImage2, itemImage3, itemImage4, itemImage5, itemImage6};

            for( int i = 0; i < look.getData().size(); i++) {
                viewdata[i].setVisibility(View.VISIBLE);
                Glide.with( c)
                        .load( look.getData().get(i).getImage())
                        .into( viewdata[i]);

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