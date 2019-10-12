package allinontech.vestium.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import allinontech.vestium.HomeScreen;
import allinontech.vestium.R;
import allinontech.vestium.Vestium;
import allinontech.vestium.WardrobeActivity;
import allinontech.vestium.addnewlook;
import allinontech.vestium.assets.Constants;
import allinontech.vestium.assets.PhotoFullPopupWindow;
import allinontech.vestium.backend.Look;
import allinontech.vestium.backend.Lookbook;
import allinontech.vestium.backend.Wardrobe;


public class LookbookFragment extends Fragment{

    private Button addLook;
    private RecyclerView lookList;
    private Lookbook lookbook;
    private FirebaseAuth mAuth;

    static MyAdapter adapter;

    private InterstitialAd mInterstitialAd;

    public LookbookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.lookbookfragment, container,false);

        mAuth = FirebaseAuth.getInstance();

        lookbook = HomeScreen.lookbook;

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(Constants.LoginScreenAd);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        addLook = v.findViewById( R.id.addLook);
        lookList = v.findViewById( R.id.lookList);
        lookList.setLayoutManager( new LinearLayoutManager( getContext()));
        adapter = new MyAdapter( getContext(), lookbook);
        adapter.notifyDataSetChanged();
        lookList.setAdapter( adapter);
        lookList.invalidate();


        addLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mInterstitialAd.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            //startActivity(i);
                        }

                    });
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                    //startActivity(i);
                }
                Intent i = new Intent( getContext(), addnewlook.class);
                startActivity(i);
            }
        });




        return v;

    }
    public class MyAdapter extends RecyclerView.Adapter<LookbookFragment.ItemViewHolder> {
        Context c;
        Lookbook items;

        public MyAdapter(Context c, Lookbook items) {
            this.c = c;
            this.items = items;
        }

        @Override
        public LookbookFragment.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lookbook_item, null);

            // create ViewHolder

            ItemViewHolder viewHolder = new ItemViewHolder( itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(LookbookFragment.ItemViewHolder holder, int position) {
            final int index = position;

            holder.setName( items.getData().get( position).getName());
            holder.setImage( c, items.getData().get( position));


            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final CharSequence colors[] = new CharSequence[] {"Remove look"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                    builder.setTitle( items.getData().get( index).getName());
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference( mAuth.getUid()+"/looks").child( items.getData().get( index).getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Toast.makeText( Vestium.getAppContext(), "Look successfully deleted", Toast.LENGTH_SHORT).show();
                                    HomeScreen.refreshData();
                                    android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.detach(LookbookFragment.this).attach(LookbookFragment.this).commit();
                                    adapter.notifyDataSetChanged();
                                }
                            });
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
            return lookbook.getData().size();

        }
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setImage( Context ctx, final Look look) {
            //Toast.makeText( ctx, image, Toast.LENGTH_LONG).show();
            ImageView itemImage1 = (ImageView) mView.findViewById(R.id.image1);
            ImageView itemImage2 = (ImageView) mView.findViewById(R.id.image2);
            ImageView itemImage3 = (ImageView) mView.findViewById(R.id.image3);
            ImageView itemImage4 = (ImageView) mView.findViewById(R.id.image4);
            ImageView itemImage5 = (ImageView) mView.findViewById(R.id.image5);
            ImageView itemImage6 = (ImageView) mView.findViewById(R.id.image6);

            final ImageView[] viewdata = { itemImage1, itemImage2, itemImage3, itemImage4, itemImage5, itemImage6};

            for ( int i = 0; i < look.getData().size(); i++) {
                viewdata[i].setVisibility(View.VISIBLE);
                Log.d("image", "setImage: " + look.getData().get(i).getImage());
                Glide.with(ctx)
                        .load( look.getData().get(i).getImage())
                        .into( viewdata[i]);
                final int a  = i;
                viewdata[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoFullPopupWindow(Vestium.getAppContext(), R.layout.popup_photo_full, viewdata[a], look.getData().get(a).getImage(), null);
                    }
                });
            }

        }
        public void setName( String name) {
            TextView itemName = (TextView) mView.findViewById(R.id.lookName);
            if( name != null)
                itemName.setText( name.toUpperCase());
            //Toast.makeText( getApplicationContext(), "Merhaba", Toast.LENGTH_SHORT).show();
        }



    }
    public static void refresh() {
        adapter.notifyDataSetChanged();
    }

}

