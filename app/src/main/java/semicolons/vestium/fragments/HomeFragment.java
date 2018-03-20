package semicolons.vestium.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import semicolons.vestium.CaptureImage;
import semicolons.vestium.R;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFragment extends Fragment{

    private ImageView imageView;
    private TextView textViewName;
    private FirebaseAuth mAuth;
    private Button newItem;


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
            Intent i = new Intent(getActivity().getApplicationContext(), CaptureImage.class);
            startActivity(i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.homefragment, container,false);
        //Showing the username and image etc
        mAuth = FirebaseAuth.getInstance();

        imageView = v.findViewById(R.id.imageView);
        textViewName = v.findViewById(R.id.textViewName);
        newItem = v.findViewById(R.id.buttoncapture);

        newItem.setOnClickListener(new captureListener());

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
        return v;
    }



}