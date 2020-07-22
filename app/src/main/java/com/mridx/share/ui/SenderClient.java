package com.mridx.share.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.mridx.share.R;
import com.mridx.share.data.Utils;
import com.mridx.share.thread.Sender;
import com.mridx.share.thread.Server;

public class SenderClient extends AppCompatActivity {

    private static final String TAG = SenderClient.class.getSimpleName();
    private MaterialButton startServerBtn, sendFileBtn;
    private MaterialTextView statusView;

    private Thread serverThread, senderThread;

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
        serverThread = new Thread(new Sender(Utils.TYPE.HOST, ""));

    }

    private void startServer(View view) {
        serverThread = new Thread(new Server(Utils.TYPE.CLIENT));
        serverThread.start();
    }
}
