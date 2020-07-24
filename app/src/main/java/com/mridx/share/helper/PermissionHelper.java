package com.mridx.share.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.mridx.share.ui.StartUI;

public class PermissionHelper {

    public static final int LOCATION_REQ = 901, WIFI_REQ = 902, PERMISSION_REQ = 900, SYSTEM_PERMISSION_REQ = 903, APP_SETTINGS_REQ = 904;

    public static boolean checkIfHasPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public static void askForPermission(Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission_group.LOCATION)
                /*|| ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_WIFI_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CHANGE_WIFI_STATE)*/) {
            ((StartUI) context).showPermissionRational();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    /*Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE}*/, PERMISSION_REQ);
        }
    }

    public static void askFroWifi(Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_WIFI_STATE)
                || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CHANGE_WIFI_STATE)) {
            // TODO: 24/07/20 ask to allow permission from settings
            return;
        }
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE}, WIFI_REQ);
    }

    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

}
