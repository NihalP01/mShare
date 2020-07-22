package com.mridx.share.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.mridx.share.R;
import com.mridx.share.callback.SenderCallback;
import com.mridx.share.data.Utils;
import com.mridx.share.thread.Sender;
import com.mridx.share.thread.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SenderHost extends AppCompatActivity implements SenderCallback {

    private static final String TAG = SenderHost.class.getSimpleName();
    private MaterialButton startServerBtn, sendFileBtn;
    private MaterialTextView statusView;

    private Thread serverThread, senderThread;
    private String CLIENT_IP = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_ui);


        sendFileBtn = findViewById(R.id.sendFileBtn);
        startServerBtn = findViewById(R.id.startServerBtn);
        statusView = findViewById(R.id.statusView);

        startServerBtn.setOnClickListener(this::startServer);
        sendFileBtn.setOnClickListener(this::sendFiles);

    }

    private void sendFiles(View view) {
        if (getConnectedClientList()) {
            Sender sender = new Sender(Utils.TYPE.CLIENT, CLIENT_IP);
            sender.setSenderCallback(this);
            serverThread = new Thread(sender);
            return;
        }
        // TODO: 22/07/20 show error
    }

    private void startServer(View view) {
        Server server = new Server(Utils.TYPE.HOST);
        serverThread = new Thread(server);
        serverThread.start();
        server.setServerCallback(this::handleServerCallback);
    }

    private void handleServerCallback(boolean b, IOException e) {
        if (b) {
            this.runOnUiThread(() -> Toast.makeText(this, "Server start hoxi kela", Toast.LENGTH_SHORT).show());
            return;
        }
        this.runOnUiThread(() -> Toast.makeText(this, "Failed ! " + e, Toast.LENGTH_SHORT).show());
        Log.d(TAG, "handleServerCallback: " + e);
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
                    CLIENT_IP = splitted[0];
                    Log.d(TAG, "getConnectedClientList: " + "Mac : " + mac + " IP Address : " + splitted[0]);
                    Log.d(TAG, "getConnectedClientList: " + "Client_count  " + clientcount + " MAC_ADDRESS  " + mac);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void setOnSenderCallback(boolean connected, IOException error) {
        if (connected) {
            Toast.makeText(this, "Connected to receiver", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Failed ! " + error, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "setOnSenderCallback: " + error);
    }

    @Override
    public void setOnFileTransferCallback() {

    }
}
