package com.haskellish.auth;

import com.alibaba.fastjson.JSON;
import com.haskellish.Main;
import com.haskellish.entity.Message;
import com.haskellish.hill.HillCipher;
import com.haskellish.hill.HillException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class AuthServer implements Runnable {

    private final String THREAD_ID = "Server " + getNextId();
    final Socket client;

    static int currID = 0;

    public AuthServer(Socket client) {
        this.client = client;
    }

    private synchronized static int getNextId(){
        return currID++;
    }

    @Override
    public void run() {
        try (DataOutputStream output = new DataOutputStream(client.getOutputStream());
             DataInputStream input = new DataInputStream(client.getInputStream())) {

            Message msg;
            String jsonString;

            //read random num from client
            jsonString = input.readUTF();
            msg = JSON.parseObject(jsonString, Message.class);
            String authID = msg.getId();
            int authRnd = Integer.parseInt(msg.getMessage());
            System.out.println(THREAD_ID + ": received " + authRnd + " from " + authID);

            //send answer
            Random r = new Random();
            int rndNum = r.nextInt(1000);
            System.out.println(THREAD_ID + ": generated number " + rndNum);
            msg = new Message(Main.ID, HillCipher.encrypt(rndNum + "," + authRnd + "," + authID, Main.HILL_KEY));
            output.writeUTF(JSON.toJSONString(msg));

            //receive both random numbers
            jsonString = input.readUTF();
            msg = JSON.parseObject(jsonString, Message.class);
            System.out.println(THREAD_ID + ": received " + msg.getMessage());
            //decrypt
            String[] decryptedMessage = HillCipher.decrypt(msg.getMessage(), Main.HILL_KEY)
                    .replaceAll(" ","").split(",");
            System.out.println(THREAD_ID + ": decrypted " + Arrays.toString(decryptedMessage));
            //check
            if (decryptedMessage.length != 2
                    || Integer.parseInt(decryptedMessage[0]) != rndNum
                    || Integer.parseInt(decryptedMessage[1]) != authRnd
                    || !msg.getId().equals(authID))
            {
                throw new AuthException("Authentication failed");
            }

            System.out.println(THREAD_ID + ": authentication complete!");

        } catch (IOException | HillException | AuthException e) {
            System.out.println("Authentication failed!");
            e.printStackTrace();
        }
    }
}
