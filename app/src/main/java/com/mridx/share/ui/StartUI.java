package com.mridx.share.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mridx.share.R;
import com.mridx.share.helper.PermissionHelper;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StartUI extends AppCompatActivity {


    private String TAG = "kaku";

    private WifiManager.LocalOnlyHotspotReservation hotspotReservation;
    private WifiManager wifiManager;

    private IntentIntegrator scanner;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ShapeableImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        findViewById(R.id.createCard).setOnClickListener(this::startHost);
        findViewById(R.id.joinCard).setOnClickListener(this::joinHost);
        img = findViewById(R.id.img);

        /*receiver = new WiFiReceiver();
        intentFilter = new IntentFilter();
        //intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED");*/

     /*   getAllAudioFromDevice(this);*/


    }

    public void getAllAudioFromDevice(final Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath());
        int count = 0;

        ArrayList<String> songs = new ArrayList<>();
        ArrayList<byte[]> arts = new ArrayList<>();


        Cursor cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int Title = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);

                String songTitle = cursor.getString(Title);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Log.d(TAG, "getAllAudioFromDevice: " + songTitle);
                Log.d(TAG, "getAllAudioFromDevice: " + path);
                File file = new File(path);
                mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
                byte[] art = mediaMetadataRetriever.getEmbeddedPicture();
                //arts.add(art);
                setToImg(art);
                songs.add(songTitle);

               /* getAlbumArtUri(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));*/


                //setToBtn(getAlbumArtUri(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString());
                //Log.d(TAG, "getAllAudioFromDevice: " + getAlbumArtUri(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))).toString());
            } while (cursor.moveToNext());

        }

        Log.d(TAG, "getAllAudioFromDevice: " + songs.size());


    }

    private void setToImg(byte[] art) {
        if (art != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            img.setImageBitmap(bitmap);
        }
    }

    public static void getAlbumArtUri(String albumId) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(albumId));
        Log.d("kaku", "getAlbumArtUri: " + uri.toString());
        uri.toString();
    }

    private void setToBtn(String art) {
        Bitmap bitmap = BitmapFactory.decodeFile(art);
        img.setImageBitmap(bitmap);
    }


    public void startHost(View view) {
        //startActivity(new Intent(this, /*SenderHost.class*/ MainUI.class));
        startActivity(new Intent(this, CreateUI.class));
        //turnOnHotspot();
    }

    public void joinHost(View view) {
        //startActivity(new Intent(this, SenderClient.class));
        startActivity(new Intent(this, /*JoinUI.class*/ MainUI.class));
        //startScanner();
    }

    private void startScanner() {
        if (!PermissionHelper.checkIfHasPermission(this)) {
            Log.d(TAG, "turnOnHotspot: permission not allowed");
            PermissionHelper.askForPermission(this);
            return;
        }
        if (scanner == null)
            scanner = new IntentIntegrator(this);
        scanner.setOrientationLocked(true);
        scanner.setPrompt("Scan QR code to connect");
        scanner.initiateScan();
    }


    private void turnOnHotspot() {
        if (!PermissionHelper.checkIfHasPermission(this)) {
            Log.d(TAG, "turnOnHotspot: permission not allowed");
            PermissionHelper.askForPermission(this);
            return;
        }
        if (!PermissionHelper.isLocationEnabled(this)) {
            askToEnableLocation();
            return;
        }
        if (wifiManager == null)
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Log.d(TAG, "Wifi Hotspot is on now");
                    hotspotReservation = reservation;
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
                Toast.makeText(this, "Please allow write system settings permission", Toast.LENGTH_LONG).show();
                Log.v("DANG", " " + !Settings.System.canWrite(this));
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, PermissionHelper.SYSTEM_PERMISSION_REQ);
                return;
            }
        }
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "AndroidHotspot-mShare";
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

    private void askToEnableLocation() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enable Location !");
        alertDialog.setMessage("Please enable location service to create hotspot and try again");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Retry", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            turnOnHotspot();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialog.show();
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
        WifiConfiguration configuration = hotspotReservation.getWifiConfiguration();
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
        BottomSheetDialog qrViewer = new BottomSheetDialog(this);
        qrViewer.setContentView(R.layout.qr_view);
        qrViewer.show();
        qrViewer.findViewById(R.id.qrClose).setOnClickListener(view -> {
            qrViewer.dismiss();
            hotspotReservation.close();
        });
        ((ShapeableImageView) qrViewer.findViewById(R.id.qrView)).setImageBitmap(bmp);
        qrViewer.setCancelable(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            } else {
                //showDialog(getString(R.string.verifyingQR));
                String r = result.getContents();
                /*Intent intent = new Intent();
                intent.putExtra("RESULT", r);*/
                ParseResult(r);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionHelper.SYSTEM_PERMISSION_REQ) {
            turnOnHotspot();
        } else if (requestCode == PermissionHelper.APP_SETTINGS_REQ) {
            turnOnHotspot();
        }
    }

    private void ParseResult(String r) {
        if (r.contains("/")) {
            String[] data = r.split("/");
            String name = data[0];
            String password = data[1];
            WifiConfiguration configuration = getWifiConfig(name);
            if (configuration == null) {
                createWPAProfile(name, password);
                //configuration = getWifiConfig(name);
            } else {
                wifiManager.disconnect();
                wifiManager.enableNetwork(configuration.networkId, true);
                wifiManager.reconnect();
            }
            //open file page
            goToFiles();
        }
    }

    private synchronized void createWPAProfile(String name, String password) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = "\"" + name + "\"";
        configuration.preSharedKey = "\"" + password + "\"";
        configuration.priority = 1;
        configuration.status = WifiConfiguration.Status.ENABLED;
        int networkId = wifiManager.addNetwork(configuration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PERMISSION_REQ) {
            turnOnHotspot();
            return;
        }
    }

    public void showPermissionRational() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Permission Request !");
        alertDialog.setMessage("Please allow location permission from App Settings.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Go to settings", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            openSettings();
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialog.show();
    }

    private void openSettings() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, PermissionHelper.APP_SETTINGS_REQ);
    }

    public void goToFiles() {
        startActivity(new Intent(this, MainUI.class));
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }*/


}
