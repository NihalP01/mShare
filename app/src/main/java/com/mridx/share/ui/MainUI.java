package com.mridx.share.ui;

import android.os.Bundle;
import android.os.Environment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mridx.share.R;
import com.mridx.share.adapter.FilesListAdapter;
import com.mridx.share.adapter.ViewPagerAdapter;
import com.mridx.share.data.FileData;
import com.mridx.share.fragment.AppFragment;
import com.mridx.share.fragment.FileFragment;
import com.mridx.share.fragment.FilesListFragment;
import com.mridx.share.fragment.MusicFragment;
import com.mridx.share.fragment.PhotoFragment;
import com.mridx.share.fragment.VideoFragment;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainUI extends AppCompatActivity implements FilesListAdapter.OnAdapterItemClicked {

    public ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;


    OnItemClickedListener onItemClickedListener;


    public interface OnItemClickedListener {
        void onClicked(FileData fileData);
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    OnBackPressed onBackPressed;

    public interface OnBackPressed {
        void onPressed();
    }

    public void setOnBackPressed(OnBackPressed onBackPressed) {
        this.onBackPressed = onBackPressed;
    }

    public static enum USER_TYPE {
        HOST, CLIENT
    }

    public USER_TYPE userType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ui);


        if (getIntent().getExtras() == null)
            //finish();
            // userType = (USER_TYPE) getIntent().getExtras().get("TYPE");

            viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager.setOffscreenPageLimit(5);

        tabLayout.setupWithViewPager(viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new AppFragment(this), "APP");
        viewPagerAdapter.addFragment(new PhotoFragment(), "PHOTO");
        viewPagerAdapter.addFragment(new MusicFragment(), "MUSIC");
        viewPagerAdapter.addFragment(new VideoFragment(), "VIDEO");
        viewPagerAdapter.addFragment(new FileFragment(), "FILE");

        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void onClicked(FileData fileData) {
        onItemClickedListener.onClicked(fileData);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (viewPager.getCurrentItem() == 4)
            onBackPressed.onPressed();
        else
            super.onBackPressed();
    }
}