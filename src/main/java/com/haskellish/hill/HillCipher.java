package com.haskellish.hill;

import com.haskellish.entity.ModMatrix;

import java.math.BigInteger;

public class HillCipher {
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 .,;?";

    public static String encrypt(String message, String key) throws HillException {
        if (Math.sqrt(key.length())%1.0 != 0) throw new HillException("Wrong key");
        else {
            //if message is too short writing spaces in it
            if (message.length()%(int)Math.sqrt(key.length()) != 0){
                while (message.length()%(int)Math.sqrt(key.length()) != 0) message += " ";
            }

            int n = (int)Math.sqrt(key.length());
            BigInteger[][] keyData = new BigInteger[n][n];
            BigInteger[][] messageData = new BigInteger[n][1];
            StringBuilder encryptedMessage = new StringBuilder();

            //fill key matrix
            for (int i = 0; i < n; i++){
                for (int j = 0; j < n; j++){
                    keyData[i][j] = new BigInteger(String.valueOf(alphabet.indexOf(key.charAt(i*3+j))));
                    if (keyData[i][j].intValue() == -1) throw new HillException("No such symbol in alphabet");
                }
            }
            ModMatrix keyMatrix = new ModMatrix(keyData, new BigInteger(String.valueOf(alphabet.length())));

            return multiplyMatrix(message, n, messageData, encryptedMessage, keyMatrix);
        }
    }


    public static String decrypt(String message, String key) throws HillException {
        if (Math.sqrt(key.length())%1.0 != 0) throw new HillException("Wrong key");
        else {
            //if message is too short writing spaces in it
            if (message.length()%(int)Math.sqrt(key.length()) != 0){
                while (message.length()%(int)Math.sqrt(key.length()) != 0) message += " ";
            }

            int n = (int)Math.sqrt(key.length());
            BigInteger[][] keyData = new BigInteger[n][n];
            BigInteger[][] messageData = new BigInteger[n][1];
            StringBuilder encryptedMessage = new StringBuilder();

            //fill key matrix
            for (int i = 0; i < n; i++){
                for (int j = 0; j < n; j++){
                    keyData[i][j] = new BigInteger(String.valueOf(alphabet.indexOf(key.charAt(i*3+j))));
                    if (keyData[i][j].intValue() == -1) throw new HillException("No such symbol in alphabet");
                }
            }
            ModMatrix keyMatrix = new ModMatrix(keyData, new BigInteger(String.valueOf(alphabet.length()))).inverse();

            return multiplyMatrix(message, n, messageData, encryptedMessage, keyMatrix);
        }
    }

    private static String multiplyMatrix(String message, int n, BigInteger[][] messageData, StringBuilder encryptedMessage, ModMatrix keyMatrix) throws HillException {
        for (int c = 0; c < message.length()/n; c++){
            //fill message vector
            for (int i = 0; i < n; i++){
                messageData[i][0] = new BigInteger(String.valueOf(alphabet.indexOf(message.charAt(n*c + i))));
                if (messageData[i][0].intValue() == -1) throw new HillException("No such symbol in alphabet");
            }

            ModMatrix messageMatrix = new ModMatrix(messageData, new BigInteger(String.valueOf(alphabet.length())));

            //calculate result matrix
            ModMatrix result = keyMatrix.multiply(messageMatrix);

            //get encrypted message
            for (int i = 0; i < n; i++){
                encryptedMessage.append(alphabet.charAt(result.getValueAt(i, 0).intValue()));
            }
        }

        return encryptedMessage.toString();
    }
}
