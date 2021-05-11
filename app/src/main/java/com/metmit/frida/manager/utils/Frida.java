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
}
