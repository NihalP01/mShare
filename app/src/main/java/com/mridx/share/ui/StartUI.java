package com.mridx.share.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mridx.share.R;

public class StartUI extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        findViewById(R.id.createCard).setOnClickListener(this::startHost);
        findViewById(R.id.joinCard).setOnClickListener(this::joinHost);

    }

    public void startHost(View view) {
        startActivity(new Intent(this, /*SenderHost.class*/ MainUI.class));

    }

    public void joinHost(View view) {
        startActivity(new Intent(this, SenderClient.class));

    }

}
