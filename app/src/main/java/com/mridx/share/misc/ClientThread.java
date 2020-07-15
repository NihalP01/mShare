package com.mridx.share.misc;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
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

    private String ip;
    private int port = 8080;

    long start = System.currentTimeMillis();
    int bytesRead;
    int current = 0;
    boolean connected;
    private Socket socket;

    public ClientThread(String address) {
        this.ip = address;
    }


    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);
            File file = new File(Environment.getExternalStorageDirectory(), "app-test.apk");
            if (!file.exists()) {
                file.createNewFile();
            }
            //byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            copyFile(inputStream, fileOutputStream);
            /*BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            int bytesRead = inputStream.read(bytes, 0, bytes.length);
            bufferedOutputStream.write(bytes, 0, bytesRead);
            bufferedOutputStream.close();*/
            socket.close();


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
        }
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
}
