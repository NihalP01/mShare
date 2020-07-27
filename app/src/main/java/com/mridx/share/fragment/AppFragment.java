package com.mridx.share.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mridx.share.R;
import com.mridx.share.adapter.AppAdapter;
import com.mridx.share.data.AppData;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AppFragment extends Fragment {

    private Context context;
    private AppAdapter appAdapter;
    private RecyclerView appsHolder;


    private static final long MB = 1024 * 1024;
    private static final long KB = 1024;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public AppFragment(Context context) {
        this.context = context;
    }

    public static AppFragment getInstance(Context context) {
        return new AppFragment(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.app_fragment, container, false);
        setupView(view);
        return view;
    }

    private void setupView(View view) {
        setupAdapter();
        appsHolder = view.findViewById(R.id.appsHolder);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        appsHolder.setAdapter(appAdapter);
        appsHolder.setLayoutManager(gridLayoutManager);

        appAdapter.setAppList(installedApps());
    }

    private void setupAdapter() {
        if (appAdapter == null)
            appAdapter = new AppAdapter();
    }

    public ArrayList<AppData> installedApps() {
        ArrayList<AppData> appList = new ArrayList<>();
        List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                Drawable icon = packInfo.applicationInfo.loadIcon(context.getPackageManager());
                //Log.d("kaku", "installedApps: " + packInfo.applicationInfo.sourceDir);
                String apkPath = packInfo.applicationInfo.sourceDir;
                appList.add(new AppData(appName, icon, apkPath, getFileSize(apkPath)));
            }
        }
        return appList;
    }

    private String getFileSize(String apkPath) {
        File file = new File(apkPath);
        if (file.exists()) {
            double fileSize = file.length();
            if (fileSize > MB) {
                return decimalFormat.format(fileSize / MB) + " MB";
            }
            return decimalFormat.format(fileSize / KB) + " KB";

        }
        return "00 KB";
    }

}
