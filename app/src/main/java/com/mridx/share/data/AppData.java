package com.mridx.share.data;

import android.graphics.drawable.Drawable;

public class AppData {

    private String appName, apkPath, apkSize;
    private Drawable appIcon;
    private boolean selected;

    public AppData(String appName, Drawable appIcon, String apkPath, String apkSize, boolean selected) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.apkPath = apkPath;
        this.apkSize = apkSize;
        this.selected = selected;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
