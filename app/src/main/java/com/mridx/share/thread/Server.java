package com.mridx.share.thread;

import android.content.Context;

import com.mridx.share.callback.ServerCallback;
import com.mridx.share.data.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private String TAG = Server.class.getSimpleName();

    private ServerSocket serverSocket;
    private Socket client;

    private Context context;
    private Utils.TYPE serverType;

    ServerCallback serverCallback;

    public void setServerCallback(ServerCallback serverCallback) {
        this.serverCallback = serverCallback;
    }

    public Server(Utils.TYPE serverType) {
        //this.context = context;
        this.serverType = serverType;
    }

    @Override
    public void run() {

        try {
            if (serverType == Utils.TYPE.HOST) {
                serverSocket = new ServerSocket(Utils.HOST_PORT);
            } else {
                serverSocket = new ServerSocket(Utils.CLIENT_PORT);
            }

            serverCallback.setOnServerCallback(true, null);

            while (true) {
                client = serverSocket.accept();
                //start file saving thread
                new Thread(new FileSaver(client)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            serverCallback.setOnServerCallback(false, e);
        }

    }
}
