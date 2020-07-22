package com.mridx.test.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.mridx.share.R;
import com.mridx.test.misc.ClientThread;
import com.mridx.test.misc.WiFiReceiver;

import java.util.List;

public class ReceiveMulticast extends AppCompatActivity {

    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_ui);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        findViewById(R.id.receiveBtn).setOnClickListener(view -> {
            startReceiving();
        });

        receiver = new WiFiReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED");
    }

    private void startReceiving() {
        getConnectedHotspot();
    }

    private void getConnectedHotspot() {
        new IntentIntegrator(this).initiateScan();
    }

    private void connectToNetwork(String contents) {
        String[] data = contents.split("/");
        String name = data[0];
        String password = data[1];
        if (checkIfConnected(name)) {
            Toast.makeText(this, "Connected to " + name, Toast.LENGTH_SHORT).show();
            return;
        }
        WifiConfiguration configuration = getWifiConfig(name);
        if (configuration == null) {
            createWPAProfile(name, password);
            //configuration = getWifiConfig(name);
        }
    }

    private WifiConfiguration getWifiConfig(String name) {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        List<WifiConfiguration> configurationList = manager.getConfiguredNetworks();
        if (configurationList != null) {
            for (WifiConfiguration wifiConfiguration : configurationList) {
                if (wifiConfiguration.SSID != null && wifiConfiguration.SSID.equalsIgnoreCase(name))
                    return wifiConfiguration;
            }
        }
        return null;
    }

    private boolean checkIfConnected(String name) {
        return wifiManager.getConnectionInfo().getSSID().equals(name);
    }

    private synchronized void createWPAProfile(String name, String password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + name + "\"";
        configuration.preSharedKey = "\"" + password + "\"";
        configuration.priority = 1;
        configuration.status = WifiConfiguration.Status.ENABLED;
        int networkId = wifiManager.addNetwork(configuration);
        //addNetwork(configuration);
        //return configuration;
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }

    public void connectToServer() {
        final WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String address = Formatter.formatIpAddress(dhcp.serverAddress);
        Thread clientThread = new Thread(new ClientThread(this, address));
        clientThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void startChat(View view) {
        startActivity(new Intent(this, Chat.class));
    }

}
