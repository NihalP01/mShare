package com.mridx.share;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mridx.share.ui.SenderClient;
import com.mridx.share.ui.SenderHost;
import com.mridx.test.ui.UniTransfer;
import com.mridx.test.ui.UniTransfer1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void send(View view) {
        startActivity(new Intent(this, SenderHost.class));

    }

    public void receive(View view) {
        startActivity(new Intent(this, SenderClient.class));

    }




}