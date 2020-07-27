package com.mridx.share.data;

import android.graphics.drawable.Drawable;

public class AppData {

    private String appName, apkPath, apkSize;
    private Drawable appIcon;

    public AppData(String appName, Drawable appIcon, String apkPath, String apkSize) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.apkPath = apkPath;
        this.apkSize = apkSize;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getApkPath() {
        return apkPath;
    }

    public String getApkSize() {
        return apkSize;
    }
}
