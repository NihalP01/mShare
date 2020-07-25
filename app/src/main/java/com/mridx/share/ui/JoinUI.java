package com.mridx.share.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mridx.share.helper.PermissionHelper;
import com.mridx.test.misc.WiFiReceiver;

import java.util.List;

public class JoinUI extends AppCompatActivity {

    private IntentIntegrator scanner;

    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    private WifiManager wifiManager;

    private String TAG = "kaku";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (wifiManager == null)
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);


        receiver = new WiFiReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        //intentFilter.addAction("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED");


        //goToFiles();
        startScanner();

    }

    private void startScanner() {
        if (!PermissionHelper.checkIfHasPermission(this)) {
            Log.d(TAG, "turnOnHotspot: permission not allowed");
            PermissionHelper.askForPermission(this);
            return;
        }
        if (!PermissionHelper.isLocationEnabled(this)) {
            askToEnableLocation();
            return;
        }
        if (scanner == null)
            scanner = new IntentIntegrator(this);
        scanner.setOrientationLocked(true);
        scanner.setPrompt("Scan QR code to connect");
        scanner.initiateScan();
    }

    private void askToEnableLocation() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enable Location !");
        alertDialog.setMessage("Please enable location service to join ");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Retry", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            startScanner();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                finish();
            } else {
                String r = result.getContents();
                ParseResult(r);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionHelper.SYSTEM_PERMISSION_REQ) {
            startScanner();
        } else if (requestCode == PermissionHelper.APP_SETTINGS_REQ) {
            startScanner();
        }
    }

    private void ParseResult(String r) {
        if (r.contains("/")) {
            String[] data = r.split("/");
            String name = data[0];
            String password = data[1];
            WifiConfiguration configuration = getWifiConfig(name);
            if (configuration == null) {
                createWPAProfile(name, password);
                //configuration = getWifiConfig(name);
            } else {
                wifiManager.disconnect();
                wifiManager.enableNetwork(configuration.networkId, true);
                wifiManager.reconnect();
            }
            //open file page
            //goToFiles();
        }
    }

    private synchronized void createWPAProfile(String name, String password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + name + "\"";
        configuration.preSharedKey = "\"" + password + "\"";
        configuration.priority = 1;
        configuration.status = WifiConfiguration.Status.ENABLED;
        int networkId = wifiManager.addNetwork(configuration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }

    private WifiConfiguration getWifiConfig(String name) {
        if (wifiManager == null)
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List<WifiConfiguration> configurationList = wifiManager.getConfiguredNetworks();
        if (configurationList != null) {
            for (WifiConfiguration wifiConfiguration : configurationList) {
                if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equalsIgnoreCase(name))
                    return wifiConfiguration;
            }
        }
        return null;
    }

    public void wifiConnected() {
        if (wifiManager == null)
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        goToFiles();
    }

    public void goToFiles() {
        Intent intent = new Intent(this, MainUI.class);
        intent.putExtra("TYPE", MainUI.USER_TYPE.CLIENT);
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
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiManager = null;
    }
}
