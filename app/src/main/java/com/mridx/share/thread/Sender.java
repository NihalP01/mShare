package com.mridx.share.thread;

import com.mridx.share.callback.SenderCallback;
import com.mridx.share.data.Utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender implements Runnable {

    private String TAG = Sender.class.getSimpleName();
    private Utils.TYPE type;
    private String CLIENT_IP;

    SenderCallback senderCallback;

    public void setSenderCallback(SenderCallback senderCallback) {
        this.senderCallback = senderCallback;
    }

    public Sender(Utils.TYPE type, String ip) {
        this.type = type;
        this.CLIENT_IP = ip;
    }

    @Override
    public void run() {

        try {
            Socket socket;
            if (Utils.TYPE.CLIENT == type) { // if client
                //connect to client socket
                socket = new Socket(CLIENT_IP, Utils.CLIENT_PORT);
            } else {
                //connect to sever socket
                socket = new Socket(Utils.HOST_IP, Utils.HOST_PORT);
            }

            senderCallback.setOnSenderCallback(true, null);

            FileTransfer fileTransfer = new FileTransfer(socket);
            new Thread(fileTransfer).start();

        } catch (IOException e) {
            e.printStackTrace();
            senderCallback.setOnSenderCallback(false, e);
        }

    }
}
