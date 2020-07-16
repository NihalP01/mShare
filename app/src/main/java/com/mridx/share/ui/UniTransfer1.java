package com.mridx.share.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mridx.share.R;
import com.mridx.share.misc.ClientThread;
import com.mridx.share.misc.ServerThread;

import java.io.BufferedReader;
import java.io.FileReader;

public class UniTransfer1 extends AppCompatActivity {

    private String TAG = "kaku";
    private String otherIP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uni_transfer);

        createServer();

    }

    private void createServer() {
        //create server
        //receive file
        Thread thread = new Thread(new ServerThread("0", this));
        thread.start();
    }

    public void startFileSend(View view) {
        //connect to server
        //send file
        //if (getConnectedClientList()) {
        Thread thread = new Thread(new ClientThread(this, "0"));
        thread.start();
        Toast.makeText(this, "Sender Thread Started", Toast.LENGTH_SHORT).show();
        //}

    }

    public boolean getConnectedClientList() {
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
                    /*Toast.makeText(
                            getApplicationContext(),
                            "Client_count  " + clientcount + "   MAC_ADDRESS  "
                                    + mac, Toast.LENGTH_SHORT).show();*/
                    otherIP = splitted[0];
                    Log.d(TAG, "getConnectedClientList: " + "Mac : " + mac + " IP Address : " + splitted[0]);
                    Log.d(TAG, "getConnectedClientList: " + "Client_count  " + clientcount + " MAC_ADDRESS  " + mac);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //qrViewer.dismiss();
        //String serverIP = getLocalIpAddress();
        //startServer(serverIP);
        return true;
    }

}
