package com.mridx.test.misc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mridx.share.R;


public class WiFiActiveService extends Service {

    private String TAG = "kaku";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // Need to wait a bit for the SSID to get picked up;
        // if done immediately all we'll get is null
        new Handler().postDelayed(() -> {
            WifiInfo info = wifiManager.getConnectionInfo();
            String mac = info.getMacAddress();
            String ssid = info.getSSID();
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "The SSID & MAC are " + ssid + " " + mac);
            }
            showNotification(ssid, mac);
            stopSelf();
        }, 5000);
        return START_NOT_STICKY;
    }

    private void showNotification(String ssid, String mac) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            setupChannel(notificationManagerCompat);
        notificationManagerCompat.notify(1000000, createNotification(ssid, mac));
    }

    private Notification createNotification(String ssid, String mac) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this,
                "notificationChannelId"
        );
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("WiFi Connected !")
                .setContentText("Connected to " + ssid)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You're connected to " + ssid + " at mac " + mac))
                .setAutoCancel(true)
                .setVibrate(createVibration())
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri());
        return builder.build();
    }

    private Uri defaultSoundUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
    }

    private long[] createVibration() {
        return new long[]{0, 1000, 0};
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannel(NotificationManagerCompat notificationManagerCompat) {
        NotificationChannel adminChannel = new NotificationChannel(
                "notificationChannelId",
                "MAIN_CHANNEL",
                NotificationManager.IMPORTANCE_HIGH
        );
        adminChannel.setDescription("this is main notification channel! \nDon't disable this");
        adminChannel.setLightColor(Color.BLUE);
        adminChannel.enableLights(true);
        adminChannel.enableVibration(true);
        notificationManagerCompat.createNotificationChannel(adminChannel);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
