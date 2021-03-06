package com.metmit.frida.manager.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.metmit.frida.manager.MyApplication;

import org.tukaani.xz.XZInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Frida {

    public static int DEFAULT_PORT = 27042;

    public static String version;

    protected static String servicePath = "/data/local/tmp/";

    protected static String commandFile = "frida-server";

    public static boolean isExistCommandFile() {
        return Helper.isExistFile(servicePath + commandFile);
    }
    
    public static String getXzFileName(String version) {
        String systemAbi = Build.SUPPORTED_ABIS[0];
        String cpuAbi = "";
        if (systemAbi.contains("arm64")) {
            cpuAbi = "arm64";
        } else if (systemAbi.contains("arm")) {
            cpuAbi = "arm";
        } else if (systemAbi.contains("x86")) {
            cpuAbi = "x86";
        }
        return String.format("frida-server-%s-android-%s.xz", version, cpuAbi);
    }

    public static boolean installLocal(Context context, Uri uri) {

        String fileType = PickFileHelper.getFileType(context, uri);
        if (!fileType.equals("application/octet-stream")) {
            return false;
        }
        String fileName = PickFileHelper.getFileName(context, uri);
        InputStream inputStream;
        inputStream = PickFileHelper.getInputStream(context, uri);

        if (fileName.endsWith(".xz")) {
            try {
                inputStream = new XZInputStream(inputStream);
            } catch (IOException e) {
                return false;
            }
        }

        try {
            String cacheFileName = context.getCacheDir().getAbsolutePath() + File.separator + "frida-server";

            File cacheFile = PickFileHelper.saveFile(inputStream, cacheFileName); // xz too slow

            String[] commands = {
                    "chmod +x " + cacheFileName,
                    cacheFileName + " --version"
            };

            for (String command: commands) {
                ShellHelper cacheShell = new ShellHelper().execute(command);
                if (cacheShell.getCode() != ShellHelper.SUCCESS_CODE) {
                    return false;
                }
            }

            boolean running = isRunning();

            if (running) {
                stopService();
            }

            ShellHelper installShell = new ShellHelper().executeSu("cp -r " + cacheFile.getAbsolutePath() + " " + servicePath);

            if (installShell.getCode() != ShellHelper.SUCCESS_CODE) {
                return false;
            }

            if (running) {
                startService();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean startService() {

        if (!isExistCommandFile()) {
            return false;
        }

        String[] commands = {
                "ls -al " + servicePath + commandFile,
                "chmod +x " + servicePath + commandFile,
                // String.format("%s%s -l 0.0.0.0:%s -D", servicePath, commandFile, getPort()),
                String.format("%s%s -l 0.0.0.0:%s > %s%s 2>&1 &", servicePath, commandFile, getPort(), servicePath, "frida-server.log")
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
            String tmp = pid.trim();
            if (TextUtils.isEmpty(tmp)) continue;
            String[] pids = tmp.split(" ");
            for (String p: pids) {
                p = p.replace(" ", "").trim();
                if (TextUtils.isEmpty(p)) continue;
                result.add(p);
            }
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

        if (version != null) {
            return version;
        }

        ShellHelper shellHelper = new ShellHelper();
        if (Container.isRoot) {
            shellHelper.executeSu("chmod +x " + servicePath + commandFile);
            ShellHelper result = shellHelper.executeSu(servicePath + commandFile + " --version");
            if (result.getCode() != 0) {
                return null;
            }
            version = result.getResult();
        }

        return version;
    }

    public static boolean isRunning() {
        if (Container.isRoot) {
            if (getProcessIds().size() > 0) {
                return true;
            }
        }
        try {
            new ServerSocket(DEFAULT_PORT).close();
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public static int getPort() {
        return new SpHelper(MyApplication.getContext(), SpHelper.SP_NAME_SETTINGS).getSp().getInt(SpHelper.SP_KEY_FRIDA_PORT, Frida.DEFAULT_PORT);
    }
}
