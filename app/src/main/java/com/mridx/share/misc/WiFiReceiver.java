package com.mridx.share.misc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.mridx.share.ui.Receive;
import com.mridx.share.ui.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class WiFiReceiver extends BroadcastReceiver {

    private String TAG = "kaku";
    private Context context;
    private Test test;

    OnClientChanged onClientChanged;

    public interface OnClientChanged {
        void onClientChanged();
    }

    public void setOnClientChanged(OnClientChanged onClientChanged) {
        this.onClientChanged = onClientChanged;
    }

    public WiFiReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())
                && WifiManager.WIFI_STATE_ENABLED == wifiState) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Wifi is now enabled");
            }
            context.startService(new Intent(context, WiFiActiveService.class));
        } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
            Log.d(TAG, "onReceive: wifi ap changed");
            if (context instanceof Receive) {
                ((Receive) context).connectToServer();
            }
        } else if ("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED".equals(action)) {
            Log.d(TAG, "onReceive: client changed");
            //((Test) context).getConnectedClientList();
            //onClientChanged.onClientChanged();
            //getConnectedClientList(context);
            if (context instanceof Test) {
                ((Test) context).getConnectedClientList();
            }
        }
    }

    public void getConnectedClientList(Context context) {
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
                            context,
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

}
