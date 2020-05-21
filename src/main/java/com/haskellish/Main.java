package com.haskellish;

import com.haskellish.util.HillException;

public class Main {

    public static void main(String[] args) throws HillException {
        System.out.println(HillCipher.decrypt(HillCipher.encrypt("testst", "keythisab"), "keythisab"));
    }
}
