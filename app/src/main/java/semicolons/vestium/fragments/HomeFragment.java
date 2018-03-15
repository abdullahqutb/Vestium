package semicolons.vestium.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import semicolons.vestium.R;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeFragment extends Fragment{

    private ImageView imageView;
    private TextView textViewName;
    private FirebaseAuth mAuth;

    public HomeFragment() {
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

        View v = inflater.inflate(R.layout.homefragment, container,false);
        //Showing the username and image etc
        mAuth = FirebaseAuth.getInstance();

        imageView = v.findViewById(R.id.imageView);
        textViewName = v.findViewById(R.id.textViewName);


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