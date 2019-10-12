package allinontech.vestium.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import allinontech.vestium.HomeScreen;
import allinontech.vestium.R;
import allinontech.vestium.Vestium;
import allinontech.vestium.WardrobeActivity;
import allinontech.vestium.assets.Constants;
import allinontech.vestium.assets.PhotoFullPopupWindow;
import allinontech.vestium.assets.Util;
import allinontech.vestium.backend.Look;
import allinontech.vestium.backend.Lookbook;
import allinontech.vestium.backend.VestiumDate;
import allinontech.vestium.backend.VestiumDateContainer;
import allinontech.vestium.selectLook;

import static android.app.Activity.RESULT_OK;


public class CalendarFragment extends Fragment {

    //
    private ArrayList<Look> looks;
    private ArrayList <VestiumDateContainer> dates;
    private VestiumDate selectedDate;




    private RecyclerView calendarRecycler;
    public RecyclerView.Adapter mAdapter;
    private Button addLookForDateButton;
    private CalendarView mCalanderView;

    static final int PICK_CONTACT_REQUEST = 11212;

    private AdView adview;

    public CalendarFragment() {
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.calendarfragment, container, false);

        adview = v.findViewById( R.id.calendarAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);


        /*ObjectOutput out;
        try {
            File outFile = new File(Environment.getExternalStorageDirectory(), "appSaveState.data");
            out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(dates);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        looks = new ArrayList<>();
        ArrayList <VestiumDateContainer> temp = VestiumDateContainer.readFromFile(getContext());
        if ( temp == null) {
            dates = new ArrayList<>();
            Log.d("read", "onCreateView: it was null");
        }
        else {
            dates = temp;
            Log.d("read", "onCreateView: it was not null");
        }

        // get todays date
        Date iniitDate = new Date();
        selectedDate = new VestiumDate(iniitDate.getDay(), iniitDate.getMonth(), iniitDate.getYear());



        calendarRecycler = v.findViewById( R.id.calendarlooklist);
        calendarRecycler.setLayoutManager( new LinearLayoutManager( getContext()));
        mAdapter = new calendarAdapter(v.getContext(), looks);
        Log.d("first and", "onCreateView: " + mAdapter);
        calendarRecycler.setAdapter(mAdapter);
        calendarRecycler.invalidate();

        mCalanderView = v.findViewById(R.id.calendarView55);
        mCalanderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            public void onSelectedDayChange( CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = new VestiumDate(dayOfMonth, month, year);

                looks.clear();
                mAdapter.notifyDataSetChanged();

                for ( VestiumDateContainer ct : dates) {
                    if ( ct.compareDate(selectedDate)) {
                        looks.clear();
                        looks.addAll(ct.getLooks());
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        addLookForDateButton = v.findViewById( R.id.calendarFragmentAddlookButton);
        addLookForDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), selectLook.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == PICK_CONTACT_REQUEST) {
            if ( resultCode == RESULT_OK) {
                ArrayList<Look> received = data.getParcelableArrayListExtra("result");
                VestiumDateContainer temp = new VestiumDateContainer(selectedDate, received);

                // dates set changed
                dates.add( temp);
                VestiumDateContainer.saveToFile(getContext(), dates);
                // data are written to the file
                looks.clear();
                looks.addAll(received);
                mAdapter.notifyDataSetChanged();
                //looks = data.getParcelableArrayListExtra("result");
                Log.d("size", "onActivityResult: size " + looks.size());
                // refresh data
                //mAdapter.notifyItemRangeChanged(0, looks.size());
                // refresh the activity
                /*getActivity().finish();
                startActivity(getActivity().getIntent());*/
            }
        }
    }

    private class calendarAdapter extends RecyclerView.Adapter<CalendarLookViewHolder> {
        Context c;
        ArrayList<Look> looks;

        public calendarAdapter(Context c, ArrayList<Look> looks) {
            this.c = c;
            this.looks = looks;
            Log.d("onCreate", "calendarAdapter: constructor");
        }

        @Override
        public CalendarLookViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_lookbook_item, null);
            CalendarLookViewHolder viewHolder = new CalendarLookViewHolder(itemLayoutView);
            Log.d("onCreate", "onCreateViewHolder: bind**");
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final CalendarLookViewHolder holder, final int position) {
            //final int index = position;

            holder.setImage( c, looks.get( position));
            Log.d("image", "onBindViewHolder: ");
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final CharSequence colors[] = new CharSequence[] {"Remove Item"};
                    AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
                    builder.setTitle( "remove look " + looks.get(position).getName() + " for date: " + selectedDate);
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // remove it from dates
                            VestiumDateContainer cOut = null;
                            for ( VestiumDateContainer ct : dates) {
                                if ( ct.compareDate(selectedDate)) {
                                    cOut = ct;
                                    dates.get( dates.indexOf(ct)).getLooks().remove(position);
                                }
                            }
                            // let VestiumDateContainer know
                            VestiumDateContainer.saveToFile(getContext(), dates);
                            // update looks
                            looks.clear();
                            looks.addAll(dates.get( dates.indexOf(cOut)).getLooks());

                            // update recyclerView
                            mAdapter.notifyDataSetChanged();

                            /*dates.get();
                            VestiumDateContainer.saveToFile(getContext(), dates);
                            // data are written to the file
                            looks.clear();
                            looks.addAll(received);
                            mAdapter.notifyDataSetChanged();*/

                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return looks.size();
        }

    }

    public class CalendarLookViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public CalendarLookViewHolder(View lookView) {
            super(lookView);
            mView = lookView;
        }

        public void setImage( Context c, final Look look) {
            ImageView lookImage1 = (ImageView) mView.findViewById(R.id.image1);
            ImageView lookImage2 = (ImageView) mView.findViewById(R.id.image2);
            ImageView lookImage3 = (ImageView) mView.findViewById(R.id.image3);
            ImageView lookImage4 = (ImageView) mView.findViewById(R.id.image4);
            ImageView lookImage5 = (ImageView) mView.findViewById(R.id.image5);
            ImageView lookImage6 = (ImageView) mView.findViewById(R.id.image6);

            final ImageView[] viewdata = { lookImage1, lookImage2, lookImage3, lookImage4, lookImage5, lookImage6};

            // empty the looks
            for ( int i = 0; i < look.getData().size(); i++) {
                viewdata[i].setVisibility(View.VISIBLE);
                Log.d("image", "setImage: " + look.getData().get(i).getImage());
                final int a  = i;
                viewdata[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoFullPopupWindow(Vestium.getAppContext(), R.layout.popup_photo_full, viewdata[a], look.getData().get(a).getImage(), null);
                    }
                });
            }

            for ( int i = 0; i < look.getData().size(); i++) {
                viewdata[i].setVisibility(View.VISIBLE);
                Log.d("image", "setImage: " + look.getData().get(i).getImage());
                Glide.with(c)
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
    }

}