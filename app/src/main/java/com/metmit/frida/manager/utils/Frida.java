package com.metmit.frida.manager.utils;

public class Frida {

    protected static String servicePath = "/data/local/tmp/";

    protected static String commandFile = "frida-server";

    public static boolean isExistCommandFile() {
        return Helper.isExistFile(servicePath + commandFile);
    }

    public static boolean startService() {

        if (!isExistCommandFile()) {
            return false;
        }

        ShellHelper.executeSu("chmod +x " + servicePath + commandFile);

        ShellHelper.executeSu(servicePath + commandFile + " &");

        return false;
    }
}
