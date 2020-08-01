package com.mridx.share.ui;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mridx.share.R;
import com.mridx.share.helper.PermissionHelper;
import com.mridx.test.misc.WiFiReceiver;

import java.lang.reflect.Method;

public class CreateUI extends AppCompatActivity {

    private String TAG = "kaku";
    private WifiManager wifiManager;
    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;

    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new WiFiReceiver();
        intentFilter = new IntentFilter();
        //intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED");

        turnOnHotspot();
    }


    private void turnOnHotspot() {
        if (!PermissionHelper.checkIfHasPermission(this)) {
            Log.d(TAG, "turnOnHotspot: permission not allowed");
            PermissionHelper.askForPermission(this);
            return;
        }
        if (!PermissionHelper.isLocationEnabled(this)) {
            askToEnableLocation();
            return;
        }
        if (wifiManager == null)
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Log.d(TAG, "Wifi Hotspot is on now");
                    hotspotReservation = reservation;
                    generateQR();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.d(TAG, "onStopped: ");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.d(TAG, "onFailed: ");
                }
            }, new Handler());
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Toast.makeText(this, "Please allow write system settings permission", Toast.LENGTH_LONG).show();
                Log.v("DANG", " " + !Settings.System.canWrite(this));
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, PermissionHelper.SYSTEM_PERMISSION_REQ);
                return;
            }
        }
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "AndroidHotspot-mShare";
        configuration.preSharedKey = "password";
        configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, configuration, true);
            if (apstatus) {
                generateQR(configuration);
            }
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    private void askToEnableLocation() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enable Location !");
        alertDialog.setMessage("Please enable location service to create hotspot and try again");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Retry", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            turnOnHotspot();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialog.show();
    }

    private void generateQR(WifiConfiguration configuration) {
        String data = configuration.SSID + "/" + configuration.preSharedKey;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            showQR(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateQR() {
        WifiConfiguration configuration = hotspotReservation.getWifiConfiguration();
        String data = configuration.SSID + "/" + configuration.preSharedKey;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            showQR(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void showQR(Bitmap bmp) {
        BottomSheetDialog qrViewer = new BottomSheetDialog(this);
        qrViewer.setContentView(R.layout.qr_view);
        qrViewer.show();
        qrViewer.findViewById(R.id.qrClose).setOnClickListener(view -> {
            qrViewer.dismiss();
            hotspotReservation.close();
        });
        ((ShapeableImageView) qrViewer.findViewById(R.id.qrView)).setImageBitmap(bmp);
        qrViewer.setCancelable(false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_REQ) {
            turnOnHotspot();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionHelper.SYSTEM_PERMISSION_REQ) {
            turnOnHotspot();
        } else if (requestCode == PermissionHelper.APP_SETTINGS_REQ) {
            turnOnHotspot();
        }
    }

    public void showPermissionRational() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Permission Request !");
        alertDialog.setMessage("Please allow location permission from App Settings.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Go to settings", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            openSettings();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialog.show();
    }

    private void openSettings() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, PermissionHelper.APP_SETTINGS_REQ);
    }

    public void goToFiles() {
        Intent intent = new Intent(this, MainUI.class);
        intent.putExtra("TYPE", MainUI.USER_TYPE.HOST);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

}
