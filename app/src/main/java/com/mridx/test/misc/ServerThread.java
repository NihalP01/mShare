package com.mridx.test.misc;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerThread implements Runnable {

    private String TAG = "kaku";
    String serverIp = "192.168.43.3";
    private int serverPort = 8080;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    private Context context;
    private Socket client;

    public ServerThread(String serverIp, Context context) {
        this.serverIp = serverIp;
        this.context = context;
    }


    public void stopThread() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            if (serverIp.equalsIgnoreCase("0")) {
                serverSocket = new ServerSocket(serverPort+1);
            } else {
                serverSocket = new ServerSocket(serverPort);
            }

            while (true) {
                client = serverSocket.accept();
               /* Thread transferThread = new Thread(new FileTransferThread(context, client));
                transferThread.start();*/

                startCheckingForReceiveFile(client);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }*/

    }

    private void startCheckingForReceiveFile(Socket client) {

        try {
            if (client.getInputStream() != null) {
                File file = new File(Environment.getExternalStorageDirectory(), new Date().getTime() + (serverIp.equalsIgnoreCase("0") ? ".apk" : ".mp4"));
                if (!file.exists()) {
                    file.createNewFile();
                }
                copyFile(client.getInputStream(), new FileOutputStream(file));
                Log.d(TAG, "startCheckingForReceiveFile: File downloaded");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class FileTransferThread implements Runnable {

        Socket socket;
        private Context context;

        public FileTransferThread(Context context, Socket client) {
            this.socket = client;
            this.context = context;
        }

        @Override
        public void run() {

            try {
                File file = new File(Environment.getExternalStorageDirectory(), "file1.mp4");
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
