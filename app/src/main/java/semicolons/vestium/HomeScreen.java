package semicolons.vestium;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.view.View;
import android.widget.ImageView;

import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import semicolons.vestium.fragments.HomeFragment;
import semicolons.vestium.fragments.LookbookFragment;
import semicolons.vestium.fragments.CircleFragment;
import semicolons.vestium.fragments.CalendarFragment;



public class HomeScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private Button newItem;
    private MenuDrawer appDrawer;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView textName, textEmail;
    private FirebaseAuth mAuth;


    private ViewPagerAdapter adapter;

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
        final Button logout = (Button) findViewById(R.id.button7);

        assert buttonOne != null;

        buttonOne.setOnClickListener(new buttonsListener());
        logout.setOnClickListener(new logoutListener());


        appDrawer = new MenuDrawer(this);


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
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "HOME");
        adapter.addFragment(new CalendarFragment(), "CALENDAR");
        adapter.addFragment(new LookbookFragment(), "LOOKBOOK");
        adapter.addFragment(new CircleFragment(), "CIRCLE");

        viewPager.setAdapter(adapter);
    }

    public class buttonsListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            appDrawer.switchDrawer();
        }
    }

    public class logoutListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mAuth.getInstance().signOut();
            Intent intent = new Intent(HomeScreen.this, Login.class);
            startActivity(intent);
            finish();
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