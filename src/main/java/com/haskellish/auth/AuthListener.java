package com.haskellish.auth;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthListener implements Runnable {

    private final int port;

    public AuthListener(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()){
            try (ServerSocket server = new ServerSocket(port)){
                Socket client = server.accept();
                System.out.println("Connection accepted to " + client.getInetAddress().getCanonicalHostName());
                new Thread(new AuthServer(client)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
