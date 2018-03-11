package semicolons.vestium;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private MenuDrawer appDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageView;
    private TextView textName, textEmail;
    private FirebaseAuth mAuth;

    private int[] tabIcons = {
            R.drawable.ic_home_white_24dp,
            R.drawable.ic_date_range_black_24dp,
            R.drawable.ic_import_contacts_black_24dp,
            R.drawable.ic_group_black_24dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        final ImageButton buttonOne = (ImageButton) findViewById(R.id.menu_button);
        assert buttonOne != null;
        buttonOne.setOnClickListener(new buttonsListener());

        appDrawer = new MenuDrawer(this);
        /*
        //Showing the username and image etc
        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);


        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageView);

        textName.setText(user.getDisplayName().toUpperCase());

        //The sliding tab widget
        */

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);

        for(int i=0; i < tabLayout.getTabCount() - 1; i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 80, 0);
            tab.requestLayout();
        }

    }

    private void setupViewPager(ViewPager viewPager) {

        viewPager.setPageMargin(25);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Test2(), "HOME");
        adapter.addFragment(new Test1(), "CALENDAR");
        adapter.addFragment(new Test2(), "LOOKBOOK");
        adapter.addFragment(new Test1(), "CIRCLE");

        viewPager.setAdapter(adapter);
    }

    public class buttonsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            appDrawer.switchDrawer();
        }
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}