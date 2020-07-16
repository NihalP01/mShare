package com.mridx.share;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mridx.share.ui.Receive;
import com.mridx.share.ui.ReceiveMulticast;
import com.mridx.share.ui.Send;
import com.mridx.share.ui.SendMulticast;
import com.mridx.share.ui.Test;
import com.mridx.share.ui.UniTransfer;
import com.mridx.share.ui.UniTransfer1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void send(View view) {
        startActivity(new Intent(this, UniTransfer.class));

    }
    public void receive(View view) {
        startActivity(new Intent(this, UniTransfer1.class));

    }
}