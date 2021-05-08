package com.metmit.frida.manager.utils;

import java.io.File;

public class Helper {

    public static boolean isExistFile(String file) {
        try {
            File f = new File(file);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
