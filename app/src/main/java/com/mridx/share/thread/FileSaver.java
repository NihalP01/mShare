package com.mridx.share.thread;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class FileSaver implements Runnable {

    private String TAG = FileSaver.class.getSimpleName();
    private Socket socket;

    public FileSaver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            if (socket.getInputStream() != null) {
                startCheckingForReceiveFile(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void startCheckingForReceiveFile(Socket client) {

        try {
            BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
            DataInputStream dis = new DataInputStream(bis);

            int filesCount = dis.readInt();
            File[] files = new File[filesCount];
            Log.d(TAG, "startCheckingForReceiveFile: started " + new Date().getTime());
            for (int i = 0; i < filesCount; i++) {
                long fileLength = dis.readLong();
                String filePath = dis.readUTF().replace("./", "/");

                String fileName = dis.readUTF();

                File x = new File(Environment.getExternalStorageDirectory() + "/mridx1", filePath);
                //File di = new File(x.getParent());
                if (!x.exists()) {
                    /*if (x.isDirectory()) {
                        x.mkdirs();
                    }*/
                    x.mkdirs();
                }

                files[i] = new File(x.getAbsolutePath(), fileName);

                FileOutputStream fos = new FileOutputStream(files[i]);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                for (int j = 0; j < fileLength; j++)
                    bos.write(bis.read());

                bos.close();
                Log.d(TAG, "startCheckingForReceiveFile: Download complete" + fileName);
            }

            dis.close();
            Log.d(TAG, "startCheckingForReceiveFile: end " + new Date().getTime());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
