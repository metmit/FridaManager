package com.metmit.frida.manager.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

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

    public static boolean isPortAvailable(int port) {
        try {
            new ServerSocket(port).close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void log(String message) {
        Log.i("frida_manager", message);
    }
}
