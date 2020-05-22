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

public class AuthClient implements Runnable {

    private final String THREAD_ID = "Client " + getNextId();
    private final int port;
    static int currID = 0;

    public AuthClient(int port) {
        this.port = port;
    }

    private synchronized static int getNextId(){
        return currID++;
    }

    @Override
    public void run() {
        try (Socket server = new Socket("localhost", port);
             DataOutputStream output = new DataOutputStream(server.getOutputStream());
             DataInputStream input = new DataInputStream(server.getInputStream()))
        {
            System.out.println(THREAD_ID + ": connection established to " + server.getInetAddress().getCanonicalHostName());

            Message msg;
            String jsonString;

            //send random num
            Random r = new Random();
            int randNum = r.nextInt(100);
            System.out.println(THREAD_ID + ": generated number " + randNum);
            msg = new Message(Main.ID, String.valueOf(randNum));
            jsonString = JSON.toJSONString(msg);
            output.writeUTF(jsonString);

            //receive crypted answer
            jsonString = input.readUTF();
            msg = JSON.parseObject(jsonString, Message.class);
            String authID = msg.getId();
            String messageString = msg.getMessage();
            System.out.println(THREAD_ID + ": received " + messageString);
            //decrypt
            String[] decryptedString = HillCipher.decrypt(messageString, Main.HILL_KEY)
                    .replaceAll(" ","").split(",");
            if (decryptedString.length != 3
                    || Integer.parseInt(decryptedString[1]) != randNum
                    || !decryptedString[2].equals(Main.ID))
            {
                throw new AuthException("Authentication failed");
            }
            System.out.println(THREAD_ID + ": decrypted " + Arrays.toString(decryptedString));

            //send crypted both random numbers
            msg = new Message(Main.ID, HillCipher.encrypt(decryptedString[0] + "," + randNum, Main.HILL_KEY));
            jsonString = JSON.toJSONString(msg);
            output.writeUTF(jsonString);

            System.out.println(THREAD_ID + ": authentication complete!");

        } catch (IOException | HillException | AuthException e) {
            System.out.println("Authentication failed!");
            e.printStackTrace();
        }
    }
}
