package com.mridx.share.misc;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.mridx.share.ui.Receive;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread implements Runnable {

    private String TAG = "kaku";

    private String ip;
    private int port = 8080;

    long start = System.currentTimeMillis();
    int bytesRead;
    int current = 0;
    boolean connected;
    private Socket socket;
    private Context context;

    public ClientThread(String address) {
        this.ip = address;
    }

    public ClientThread(Context context, String address) {
        this.context = context;
        this.ip = address;
    }


    @Override
    public void run() {

        try {
            Socket client;
            if (ip.equalsIgnoreCase("0")) {
                client = new Socket("192.168.43.1", 8080);
            } else {
                client = new Socket(ip, 8080+1);
            }
            Thread transferThread = new Thread(new FileTransferThread(context, client, ip));
            transferThread.start();
        } catch (IOException e) {
            Log.d(TAG, "run: socket error " + e);
        }

        /*try {
            socket = new Socket("192.168.43.1", port);
            File file = new File(Environment.getExternalStorageDirectory(), "testFile.png");
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            copyFile(inputStream, fileOutputStream, context);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            int bytesRead = inputStream.read(bytes, 0, bytes.length);
            bufferedOutputStream.write(bytes, 0, bytesRead);
            bufferedOutputStream.close();
            //socket.close();
            Toast.makeText(context, "File received", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("kaku", "run: Failed " + e.toString());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }*/
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("kaku", e.toString());
            return false;
        }
        return true;
    }

    static class FileTransferThread implements Runnable {

        Socket socket;
        private Context context;
        private String ip;

        public FileTransferThread(Context context, Socket client) {
            this.socket = client;
            this.context = context;
        }

        public FileTransferThread(Context context, Socket client, String ip) {
            this.socket = client;
            this.context = context;
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
                File file;
                if (ip.equalsIgnoreCase("0")) {
                    file = new File(Environment.getExternalStorageDirectory(), "file.mp4");
                } else {
                    file = new File(Environment.getExternalStorageDirectory(), "app-release.apk");
                }
                ContentResolver contentResolver = context.getContentResolver();
                InputStream inputStream = contentResolver.openInputStream(Uri.fromFile(file));
                OutputStream outputStream = socket.getOutputStream();
                copyFile(inputStream, outputStream);
                //socket.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }
}
