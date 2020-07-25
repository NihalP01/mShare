package com.mridx.share.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mridx.share.R;
import com.mridx.share.adapter.ViewPagerAdapter;

public class MainUI extends AppCompatActivity {

    public ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;

    public static enum USER_TYPE {
        HOST, CLIENT
    }

    public USER_TYPE userType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);


        if (getIntent().getExtras() == null)
            finish();
        userType = (USER_TYPE) getIntent().getExtras().get("TYPE");

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Fragment(R.layout.files_fragment), "APP"); // TODO: 23/07/20 change layout
        viewPagerAdapter.addFragment(new Fragment(R.layout.music_fragment), "PHOTO"); // TODO: 23/07/20 change layout
        viewPagerAdapter.addFragment(new Fragment(R.layout.music_fragment), "MUSIC");
        viewPagerAdapter.addFragment(new Fragment(R.layout.music_fragment), "VIDEO"); // TODO: 23/07/20 change layout
        viewPagerAdapter.addFragment(new Fragment(R.layout.files_fragment), "FILE");

        viewPager.setAdapter(viewPagerAdapter);
    }
}
