package allinontech.vestium.fragments;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Scanner;

import allinontech.vestium.Creatingpoll;
import allinontech.vestium.FriendWardrobe;
import allinontech.vestium.HomeScreen;
import allinontech.vestium.R;
import allinontech.vestium.Vestium;
import allinontech.vestium.WardrobeActivity;
import allinontech.vestium.addnewlook;
import allinontech.vestium.assets.PhotoFullPopupWindow;
import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Category;
import allinontech.vestium.backend.Circle;
import allinontech.vestium.backend.Friend;
import allinontech.vestium.backend.Item;
import allinontech.vestium.backend.Look;
import allinontech.vestium.backend.Lookbook;
import allinontech.vestium.backend.Poll;
import allinontech.vestium.backend.PollItem;


public class CircleFragment extends Fragment{


    FirebaseAuth mAuth;
    Button createpoll;
    RecyclerView friendlist;
    RecyclerView pollsList;
    LinearLayout pollsContainer;
    Circle circle;
    FloatingActionButton addFriend;
    TextView noItems;
    ProgressBar loading;

    DatabaseReference mainRef;

    ArrayList<Poll> allPolls;

    static MyAdapter adapter;
    static PollAdapter pollsAdapter;

    static Poll temp;
    static int i;

    public CircleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.circlefragment, container,false);

        circle = HomeScreen.circle;

        loading = v.findViewById( R.id.progressBarPolls);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                if( !isNetworkAvailable()){
                    Toast.makeText( Vestium.getAppContext(), "Polls could not be loaded, internet not available", Toast.LENGTH_SHORT).show();
                }
            }
        }, 7000);


        pollsList = v.findViewById( R.id.pollsList);
        pollsContainer = v.findViewById( R.id.circlesContainer);
        if( isNetworkAvailable()){
            pollsContainer.setVisibility(View.VISIBLE);
        }

        allPolls = new ArrayList<>();
        createpoll = v.findViewById( R.id.createPoll);
        createpoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( getContext(), Creatingpoll.class);
                startActivity(i);
            }
        });

        if( mainRef == null){
            mainRef = Util.getDatabase().getReference();
            mainRef.keepSynced( true);
        }

        mAuth = FirebaseAuth.getInstance();


        addFriend = (FloatingActionButton) v.findViewById( R.id.addFriend);
        friendlist = (RecyclerView) v.findViewById(R.id.friendList);


        if( isNetworkAvailable()){
            allPolls.removeAll(allPolls);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    getPolls( 0);
                }
            }, 500);


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.d("dmdmdmodmodmodmdmd", allPolls.size() + "");
                    ArrayList<Poll> temp = updateList( allPolls);
                    allPolls = temp;
                    Log.d("dmdmdmodmodmodmdmd", allPolls.size() + "");
                    if( allPolls.size() == 0){
                        pollsList.setVisibility( View.GONE);
                        noItems.setVisibility( View.VISIBLE);
                    }
                    else{
                        pollsAdapter = new CircleFragment.PollAdapter( getContext(), allPolls);
                        pollsList.setAdapter( pollsAdapter);
                        pollsAdapter.notifyDataSetChanged();
                        pollsList.invalidate();
                        Log.d( "ABABABABABABABA", "SIZE OF POLLS: " + allPolls.get( 0).getData().size());
                    }
                }
            }, 7000);
            //pollsAdapter = new CircleFragment.PollAdapter( getContext(), allPolls);
            //pollsList.setAdapter( pollsAdapter);


        }

        noItems = v.findViewById( R.id.noPollsInCircle);

        addFriend.setOnClickListener( new addFriendListener());

        pollsList.setLayoutManager( new LinearLayoutManager( getContext()));


        friendlist.setLayoutManager( new LinearLayoutManager( getContext()));
        adapter = new CircleFragment.MyAdapter( getContext(), circle);
        adapter.notifyDataSetChanged();
        friendlist.setAdapter( adapter);
        friendlist.invalidate();

        return v;
    }

    public void getPolls( final int user){
        if( isNetworkAvailable() && circle.getList().size() > 0){
            temp = new Poll();
            final String key = circle.getList().get( user).getUserId();
            //Toast.makeText( Vestium.getAppContext(), key + "  " + circle.getList().get( user).getName(), Toast.LENGTH_SHORT).show();
            final Query query = Util.getDatabase().getReference().child( key).child("poll");
            query.keepSynced(true);
            //Log.d( "ABABABABABABABA", circle.getList().size() + " ~ " + user);
            Util.getDatabase().getReference().child("mocks").setValue(
                    "Merhaba", new DatabaseReference.CompletionListener(){

                        @Override
                        public void onComplete(DatabaseError error, DatabaseReference afb) {
                            Log.d( "ABABABABABABABA", circle.getList().size() + " ~ " + user);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {

                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try{
                                        if( !dataSnapshot.exists()){
                                            if( user < circle.getList().size() - 1) {
                                                getPolls(user + 1);
                                            }
                                        }
                                        Util.getDatabase().getReference().child("mocks").removeValue();
                                        final int count = (int) dataSnapshot.getChildrenCount();
                                        for( DataSnapshot item: dataSnapshot.getChildren()){

                                            if( item.child("key").getValue() != null && item.child("votes").getValue() != null) {
                                                final String itemId = item.getKey();
                                                final String itemKey = item.child("key").getValue().toString();
                                                final String itemVotes = item.child("votes").getValue().toString();
                                                //String itemName = null;
                                                //String itemImage = null;

                                                if( itemKey != null){

                                                    i = 0;
                                                    final Query query1 = Util.getDatabase().getReference().child( key).child("items").child( itemKey);
                                                    query1.keepSynced(true);
                                                    Util.getDatabase().getReference().child("mocks").setValue(
                                                            "Merhaba", new DatabaseReference.CompletionListener(){

                                                                @Override
                                                                public void onComplete(DatabaseError error, DatabaseReference afb) {

                                                                    query1.addListenerForSingleValueEvent(new ValueEventListener() {

                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            try{
                                                                                i++;

                                                                                Util.getDatabase().getReference().child("mocks").removeValue();
                                                                                if( dataSnapshot.child("name").getValue() != null && dataSnapshot.child("image").getValue() != null) {
                                                                                    String itemName = dataSnapshot.child("name").getValue().toString();
                                                                                    String itemImage = dataSnapshot.child("image").getValue().toString();
                                                                                    if( !itemVotes.equals("") && !itemName.equals("") && !itemImage.equals("")){

                                                                                        PollItem tempItem = new PollItem(itemKey, itemName, itemImage, itemVotes, itemId);
                                                                                        temp.getData().add( tempItem);
                                                                                        //Log.d("ABABABABABABABA", "" + tempItem.getName());
                                                                                        //Log.d("ABABABABABABABA", "" + tempItem.getImage());
                                                                                        //Toast.makeText( Vestium.getAppContext(), temp.getName(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                                if( i >= count && user < circle.getList().size() - 1){
                                                                                    getPolls( user + 1);
                                                                                }
                                                                            } catch ( Exception e) {
                                                                            }
                                                                        }
                                                                        public void onCancelled(DatabaseError error) {
                                                                        }

                                                                    });
                                                                }
                                                            });
                                                }
                                            }
                                        }

                                    } catch ( Exception e) {

                                    }
                                }
                                public void onCancelled(DatabaseError error) {
                                    Log.d( "YOK", "YOK");
                                }

                            });
                        }
                    });
            if( circle.friendName( key) != null) {
                Log.d("NAHINNN", "NAHINNNNNN + " + circle.friendName( key));
                temp.setName(circle.friendName(key));
                temp.setKey( key);
            }
            allPolls.add( temp);
            Log.d("FINALAAALALALA", "" + temp.getName() + "   " + allPolls.size());

        }
    }

    public class addFriendListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( !isNetworkAvailable())
                Toast.makeText(Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();
            else {
                final EditText input = new EditText(getContext());
                new AlertDialog.Builder( v.getContext())
                        .setTitle("ADD FRIEND")
                        .setView(input)
                        .setMessage("Enter username:")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                                final String m_Text = input.getText().toString();
                                if( m_Text.equals( HomeScreen.uName)) {
                                    dialog.cancel();
                                    Toast.makeText( Vestium.getAppContext(), "Invalid username", Toast.LENGTH_SHORT).show();
                                }
                                else if( m_Text.length() == 0 || m_Text == null) {
                                    dialog.cancel();
                                    Toast.makeText(Vestium.getAppContext(), "Please enter a valid username", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    try {
                                        final Query query = mainRef;
                                        query.keepSynced(true);

                                        Util.getDatabase().getReference().child("mocks").setValue(
                                                "Merhaba", new DatabaseReference.CompletionListener(){

                                                    @Override
                                                    public void onComplete(DatabaseError error, DatabaseReference afb) {

                                                        query.addListenerForSingleValueEvent(new ValueEventListener() {

                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                try {
                                                                    mainRef.child("mocks").removeValue();
                                                                    //Toast.makeText(Vestium.getAppContext(), "check", Toast.LENGTH_SHORT).show();
                                                                    boolean found = false;
                                                                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                                                        //Log.d("FRIEND AAYA", itemSnapshot.getKey());
                                                                        Log.d("FRIEND", "" + itemSnapshot.getKey());
                                                                        if (itemSnapshot.child("username").getValue() != null) {
                                                                            //String friendName = itemSnapshot.child("name").getValue().toString();
                                                                            String friendUsername = itemSnapshot.child("username").getValue().toString();
                                                                            Log.d("FRIEND AAYA", friendUsername);
                                                                            if (friendUsername.equals(m_Text)) {
                                                                                String userHash = itemSnapshot.getKey();
                                                                                if(!HomeScreen.circle.findFriend( userHash)){
                                                                                    found = true;
                                                                                    Util.getDatabase().getReference(mAuth.getUid()).child("circle").push().child("uid").setValue( userHash);
                                                                                    HomeScreen.refreshData();
                                                                                    refresh();
                                                                                    Handler handler = new Handler();
                                                                                    handler.postDelayed(new Runnable() {
                                                                                        public void run() {
                                                                                            adapter.notifyDataSetChanged();
                                                                                            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                                                            ft.detach(CircleFragment.this).attach(CircleFragment.this).commit();
                                                                                        }
                                                                                    }, 500);


                                                                                }
                                                                                else{
                                                                                    found = true;
                                                                                    Toast.makeText(Vestium.getAppContext(), "Friend already exists", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    if (found == false)
                                                                        Toast.makeText(Vestium.getAppContext(), "User not found", Toast.LENGTH_SHORT).show();

                                                                } catch (Exception e) {
                                                                    Toast.makeText(Vestium.getAppContext(), "Friend could not be added", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            public void onCancelled(DatabaseError error) {
                                                            }
                                                        });
                                                    }
                                                });
                                    } catch (Exception e) {
                                        dialog.cancel();
                                        //Toast.makeText(Vestium.getAppContext(), "Friend could not be added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();


            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class MyAdapter extends RecyclerView.Adapter<CircleFragment.ItemViewHolder> {
        Context c;
        Circle circle;

        public MyAdapter(Context c, Circle circle) {
            this.c = c;
            this.circle = circle;
        }

        @Override
        public CircleFragment.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_friend_item, null);

            // create ViewHolder

            CircleFragment.ItemViewHolder viewHolder = new CircleFragment.ItemViewHolder( itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CircleFragment.ItemViewHolder holder, final int position) {
            final int index = position;
            String name = circle.getList().get( position).getName();
            if( name != null && name.length() > 20)
                name = name.substring(0, 20);

            holder.setName( name);
            final String name2 = name;

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final CharSequence colors[] = new CharSequence[] {"Remove friend", "View Wardrobe"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                    builder.setTitle( name2);
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if( which == 0) {
                                Util.getDatabase().getReference( mAuth.getUid()+"/circle").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                                //Log.d("FRIEND AAYA", itemSnapshot.getKey());
                                                if (itemSnapshot.child("uid").getValue().toString().toString().equals( circle.getList().get( position).getUserId())) {

                                                    Util.getDatabase().getReference( mAuth.getUid()+"/circle").child( circle.getList().get(position).getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            Toast.makeText( Vestium.getAppContext(), "Unfriended", Toast.LENGTH_SHORT).show();
                                                            HomeScreen.refreshData();

                                                            adapter.notifyDataSetChanged();
                                                            android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                            ft.detach(CircleFragment.this).attach(CircleFragment.this).commit();

                                                        }
                                                    });
                                                }
                                            }

                                        } catch (Exception e) {
                                            Toast.makeText(Vestium.getAppContext(), "Friend could not be deleted", Toast.LENGTH_SHORT).show();
                                        }
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.detach(CircleFragment.this).attach(CircleFragment.this).commit();
                                            }
                                        }, 1000);
                                        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.detach(CircleFragment.this).attach(CircleFragment.this).commit();
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                            else if( which == 1) {
                                if(!isNetworkAvailable()){
                                    Toast.makeText( Vestium.getAppContext(), "Internet not available", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String latLong = "test";
                                    Intent i = new Intent(getContext(), FriendWardrobe.class);
                                    i.putExtra("uid", circle.getList().get( position).getUserId());
                                    startActivity(i);
                                }
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return circle.getList().size();

        }
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName( String name) {
            TextView friendName = (TextView) mView.findViewById(R.id.friendName);
            if( name != null)
                friendName.setText( name.toUpperCase());
        }



    }

    public class PollAdapter extends RecyclerView.Adapter<CircleFragment.PollHolder> {
        Context c;
        ArrayList<Poll> polls;

        public PollAdapter(Context c, ArrayList<Poll> polls) {
            this.c = c;
            this.polls = polls;
        }

        @Override
        public CircleFragment.PollHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_circle_poll, null);

            // create ViewHolder
            CircleFragment.PollHolder viewHolder = new CircleFragment.PollHolder( itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CircleFragment.PollHolder holder, final int position) {
            final int index = position;

            holder.setName( polls.get( index).getName());
            holder.setRecyclerView( getContext(), polls.get( index));
        }

        @Override
        public int getItemCount() {
            //Log.d("Get Data", "" + items.size());
            return polls.size();

        }
    }
    public class PollHolder extends RecyclerView.ViewHolder {

        View mView;
        public PollHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName( String name) {
            TextView friendName = (TextView) mView.findViewById(R.id.myFriendName);
            if( name != null)
                friendName.setText( name.toUpperCase());
        }
        public void setRecyclerView( Context ctx, Poll temp) {

            RecyclerView view = (RecyclerView) mView.findViewById( R.id.allPollsView);
            int spanCount = 2;
            int spacing = 25; // 50px
            boolean includeEdge = false;
            view.setLayoutManager(new GridLayoutManager( getContext(), 2));
            view.addItemDecoration(new CircleFragment.GridSpacingItemDecoration(spanCount, spacing, includeEdge));
            CircleFragment.EachPollAdapter adapter = new CircleFragment.EachPollAdapter( getContext(), temp);
            view.setAdapter( adapter);
            view.setNestedScrollingEnabled(true);
            view.invalidate();


        }


    }

    public class EachPollAdapter extends RecyclerView.Adapter<CircleFragment.PollItemHolder> {
        Context c;
        Poll items;
        String votesNew;

        public EachPollAdapter(Context c, Poll poll) {
            this.c = c;
            this.items = poll;
        }

        @Override
        public CircleFragment.PollItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(c).inflate(R.layout.single_poll_list_item, parent, false);
            return new CircleFragment.PollItemHolder(v);
        }

        @Override
        public void onBindViewHolder(final CircleFragment.PollItemHolder holder, final int position) {
            final int index = position;

            holder.setName( this.items.getData().get( index).getName());
            holder.setImage( c, this.items.getData().get( index).getImage());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PhotoFullPopupWindow( Vestium.getAppContext(), R.layout.popup_photo_full, holder.returnView(), items.getData().get( index).getImage(), null);
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final String key = items.getKey();
                    //Log.d( "ABDNDNDNDND", key + "");
                    Log.d( "ABDNDNDNDND", items.getData().get( index).getId() + "");
                    final Query query = mainRef.child( key).child( "poll").child( items.getData().get( index).getId());
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
                                                //Log.d( "ABDNDNDNDND", dataSnapshot.child("votes").getValue().toString() + "");
                                                if (dataSnapshot.child("votes").getValue() != null) {
                                                    votesNew = dataSnapshot.child("votes").getValue().toString();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if( friendFound( votesNew, mAuth.getUid())) {
                                                                Toast.makeText( Vestium.getAppContext(), "You have already voted for this poll", Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Toast.makeText( Vestium.getAppContext(), "Adding your vote...", Toast.LENGTH_SHORT).show();
                                                                Log.d("ABDNDNDNDND", votesNew + "");
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        votesNew = votesNew + mAuth.getUid() + "/";
                                                                        Log.d("ABDNDNDNDND", votesNew + "");
                                                                        mainRef.child(key).child("poll").child(items.getData().get(index).getId()).child("votes").removeValue();
                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                mainRef.child(key).child("poll").child(items.getData().get(index).getId()).child("votes").setValue(votesNew, new DatabaseReference.CompletionListener() {
                                                                                    @Override
                                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                                        holder.toggle(true);
                                                                                    }
                                                                                });
                                                                            }
                                                                        }, 800);
                                                                    }
                                                                }, 200);
                                                            }
                                                        }
                                                    }, 200);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(Vestium.getAppContext(), "Your vote could not be made", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    } catch (Exception e) {
                                    }

                                }
                            });

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

    public class PollItemHolder extends RecyclerView.ViewHolder {

        View mView;
        public PollItemHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void toggle( boolean selected){
            if( selected)
                mView.setBackgroundColor(Color.GREEN);
            else
                mView.setBackgroundColor( Color.WHITE);
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



    public boolean friendFound( String data, String userId){
        Log.d("FINDING FRAANDS", data);
        Scanner scan = new Scanner( data);
        scan.useDelimiter("/");
        while( scan.hasNext()){
            if( scan.next().equals( userId))
                return true;
        }
        return false;
    }

    public static void refresh() {
        adapter.notifyDataSetChanged();
        pollsAdapter.notifyDataSetChanged();
    }

    public ArrayList<Poll> updateList( ArrayList<Poll> temp){
        ArrayList<Poll> output = new ArrayList<>();
        for( int i = 0; i < temp.size(); i++){
            Log.d("dmdmdmodmodmodmdmd", temp.get( i).getName() + "");
            Log.d("dmdmdmodmodmodmdmddddd", temp.get( i).getData().size() + "");
            if( temp.get( i).getData().size() > 0){
                output.add( temp.get( i));
            }
        }
        return output;
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