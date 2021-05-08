package com.metmit.frida.manager.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ShellHelper {

    public static String executeSu(String command) {
        return execute(new String[]{"su", "-c", command});
    }

    public static String execute(String command) {
        return execute(new String[]{"sh", "-c", command});
    }

    public static String execute(String[] command) {

        BufferedReader bufferedReader = null;

        StringBuilder result = new StringBuilder();

        try {
            Process process = Runtime.getRuntime().exec(command);

            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));

            if (process.waitFor() == 0) {
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    result.append(line).append("\n");
                }
            } else {
                result.append(Arrays.toString(command)).append(" execute error!").append("\n");
            }
        } catch (Exception e) {
            result.append(e.getMessage()).append("\n");
            result.append(Arrays.toString(e.getStackTrace())).append("\n");
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    result.append(e.getMessage()).append("\n");
                    result.append(Arrays.toString(e.getStackTrace())).append("\n");
                }
            }
        }
        return result.toString();
    }
}
