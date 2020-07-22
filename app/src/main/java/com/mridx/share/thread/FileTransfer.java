package com.mridx.share.thread;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class FileTransfer implements Runnable {

    private Socket socket;

    public FileTransfer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            sendFiles(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendFiles(Socket socket) {
        try {
            File file1 = new File(Environment.getExternalStorageDirectory(), "mridx1");
            File[] files = file1.listFiles();

            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dos = new DataOutputStream(bos);

            dos.writeInt(files.length);

            for (File file : files) {
                long length = file.length();
                dos.writeLong(length);

                String name = file.getName();
                dos.writeUTF(name);

                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);

                int theByte = 0;
                while ((theByte = bis.read()) != -1) bos.write(theByte);

                bis.close();
            }

            dos.close();


        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(" IOEXception " + e);
        }
    }
}
