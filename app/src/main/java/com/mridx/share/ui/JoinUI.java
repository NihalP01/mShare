package com.mridx.share.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class JoinUI extends AppCompatActivity {

    private IntentIntegrator scanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (scanner == null)
            scanner = new IntentIntegrator(this);
        scanner.setOrientationLocked(true);
        scanner.setPrompt("Scan QR code to connect");
        scanner.initiateScan();

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
    }

    private void ParseResult(String r) {

    }
}
