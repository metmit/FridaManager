package com.metmit.frida.manager.utils;

import android.text.TextUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Frida {

    public static int DEFAULT_PORT = 27042;

    protected static String servicePath = "/data/local/tmp/";

    protected static String commandFile = "frida-server";

    public static boolean isExistCommandFile() {
        return Helper.isExistFile(servicePath + commandFile);
    }

    public static boolean startService() {

        if (!isExistCommandFile()) {
            return false;
        }

        String[] commands = {
                "ls -al " + servicePath + commandFile,
                "chmod +x " + servicePath + commandFile,
                // servicePath + commandFile + " &",
                String.format("%s%s > %s%s 2>&1 &", servicePath, commandFile, servicePath, "frida-server.log")
        };

        for (String command : commands) {
            new ShellHelper().executeSu(command);
        }

        return true;
    }

    public static ArrayList<String> getProcessIds() {
        ShellHelper shellHelper = new ShellHelper().executeSu("pidof frida-server");
        if (shellHelper.getCode() != 0) {
            return new ArrayList<>();
        }
        String res = shellHelper.getResult();
        String[] temp = res.split("\n");

        ArrayList<String> result = new ArrayList<>();
        for (String pid : temp) {
            String tmp = pid.replace(" ", "").trim();
            if (TextUtils.isEmpty(tmp)) continue;
            result.add(tmp);
        }
        return result;
    }

    public static boolean stopService() {
        if (!isExistCommandFile()) {
            return false;
        }
        ArrayList<String> process = getProcessIds();
        ShellHelper shellHelper = new ShellHelper();
        for (String pid : process) {
            shellHelper.executeSu("kill -9 " + pid);
        }
        return true;
    }

    public static String getVersion() {
        if (!isExistCommandFile()) {
            return null;
        }

        ShellHelper shellHelper = new ShellHelper();
        shellHelper.executeSu("chmod +x " + servicePath + commandFile);

        ShellHelper result = shellHelper.executeSu(servicePath + commandFile + " --version");
        if (result.getCode() != 0) {
            return null;
        }

        return result.getResult();
    }

    public static boolean isRunning() {
        if (getProcessIds().size() > 0) {
            return true;
        }
        try {
            new ServerSocket(DEFAULT_PORT).close();
        } catch (IOException e) {
            Helper.log(e.getMessage());
            return true;
        }
        return false;
    }
}
