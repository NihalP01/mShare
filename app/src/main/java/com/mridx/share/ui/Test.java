package com.mridx.share.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mridx.share.R;
import com.mridx.share.misc.WiFiBroadcastReceiver;
import com.mridx.share.misc.WiFiReceiver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class Test extends AppCompatActivity implements WifiP2pManager.PeerListListener, WiFiReceiver.OnClientChanged {

    private static final String TAG = "kaku";
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;

    WifiP2pDevice device;
    WifiP2pConfig config = new WifiP2pConfig();

    WifiManager wifiManager;
    WifiManager.LocalOnlyHotspotReservation mReservation;


    IntentFilter intentFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        findViewById(R.id.startHP).setOnClickListener(view -> turnOnHotspot(wifiManager));
        findViewById(R.id.connect).setOnClickListener(view -> getConnectedHotspot());

        receiver = new WiFiReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED");

        new WiFiReceiver().setOnClientChanged(this);


        /*manager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });*/

    }

    private void getConnectedHotspot() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                connectToNetwork(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        /*wifiManager.disconnect();
        wifiManager.enableNetwork(configuration.networkId, true);
        wifiManager.reconnect();*/

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

    private synchronized void addNetwork(WifiConfiguration configuration) {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        new Handler().postDelayed(() -> {
            if (manager.pingSupplicant()) {
                manager.addNetwork(configuration);
                return;
            }
            addNetwork(configuration);
        }, 1000);
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

    private void getAll(WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        Log.d(TAG, "getAll: " + wifiInfo.getBSSID());
        Log.d(TAG, "getAll: " + wifiInfo.getSSID());
    }

    public static String getLocalIPAddress(WifiManager wm) {
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public String getip(WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        try {
            String ip = InetAddress.getByAddress(
                    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array())
                    .getHostAddress();
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        Log.d(TAG, "onPeersAvailable: " + wifiP2pDeviceList.getDeviceList().size());
    }

    @SuppressWarnings("MissingPermission")
    private void turnOnHotspot(WifiManager wifiManager) {
        /*WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "testAp";
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Log.d(TAG, "Wifi Hotspot is on now");
                    mReservation = reservation;
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
                Log.v("DANG", " " + !Settings.System.canWrite(this));
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "AndroidTest";
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
        WifiConfiguration configuration = mReservation.getWifiConfiguration();
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.qr_view);
        bottomSheetDialog.show();
        ((AppCompatImageView) bottomSheetDialog.findViewById(R.id.qrView)).setImageBitmap(bmp);
    }

    private void turnOffHotspot() {
        if (mReservation != null) {
            mReservation.close();
        }
    }

    public void getConnectedClientList() {
        int clientcount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                String mac = splitted[3];
                System.out.println("Mac : Outside If " + mac);
                if (mac.matches("..:..:..:..:..:..")) {
                    clientcount++;
                    System.out.println("Mac : " + mac + " IP Address : " + splitted[0]);
                    System.out.println("Client_count  " + clientcount + " MAC_ADDRESS  " + mac);
                    Toast.makeText(
                            getApplicationContext(),
                            "Client_count  " + clientcount + "   MAC_ADDRESS  "
                                    + mac, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "getConnectedClientList: " + "Mac : " + mac + " IP Address : " + splitted[0]);
                    Log.d(TAG, "getConnectedClientList: " + "Client_count  " + clientcount + " MAC_ADDRESS  " + mac);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClientChanged() {
        Log.d(TAG, "onClientChanged: ahixi bal");
        getConnectedClientList();
    }
}
