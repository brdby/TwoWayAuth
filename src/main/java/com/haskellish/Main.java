package com.haskellish;

import com.haskellish.auth.AuthClient;
import com.haskellish.auth.AuthListener;

import java.util.Scanner;

public class Main {

    public static String ID;
    public static String HILL_KEY;
    public static int PORT;

    public static void main(String[] args) {
        if (args.length == 3) {
            ID = args[0];
            HILL_KEY = args[1];
            PORT = Integer.parseInt(args[2]);

            Thread authListener = new Thread(new AuthListener(PORT));
            authListener.setDaemon(true);
            authListener.start();

            Scanner in = new Scanner(System.in);
            String input = "";
            while (!input.equals("exit")) {
                input = in.nextLine();
                switch (input) {
                    case ("connect"): {
                        System.out.println("Port?");
                        new Thread(new AuthClient(Integer.parseInt(in.nextLine()))).start();
                        break;
                    }
                    case ("exit"): {
                        System.out.println("Exiting...");
                        authListener.interrupt();
                        break;
                    }
                    default:
                        System.out.println("Wrong command");
                }
            }
        }
        else System.out.println("Not all arguments accepted");
    }
}
