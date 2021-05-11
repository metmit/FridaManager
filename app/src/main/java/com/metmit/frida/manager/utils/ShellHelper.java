package com.metmit.frida.manager.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ShellHelper {

    public static int SUCCESS_CODE = 0;

    public static int FAILURE_CODE = -1;

    protected int code;

    protected String result = "";

    public ShellHelper() {
        code = FAILURE_CODE;
        result = "";
    }

    public String getResult() {
        return result;
    }

    public int getCode() {
        return code;
    }

    public ShellHelper executeSu(String command) {
        return execute(command, true);
    }

    public ShellHelper execute(String command) {
        return execute(command, false);
    }

    public ShellHelper execute(String command, Boolean isRoot) {

        int code = -1;

        StringBuilder result = new StringBuilder();

        Process process = null;
        DataOutputStream outputStream = null;
        BufferedReader inputReader = null;
        BufferedReader errorReader = null;

        try {
            if (isRoot) {
                process = Runtime.getRuntime().exec("su");
                outputStream = new DataOutputStream(process.getOutputStream());
                outputStream.writeBytes(command + "\n");
                outputStream.writeBytes("exit\n");
                outputStream.flush();
            } else {
                process = Runtime.getRuntime().exec(command);
            }

            inputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
            String line;
            while ((line = inputReader.readLine()) != null) {
                result.append(line).append("\n");
            }

            errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()));
            while ((line = errorReader.readLine()) != null) {
                result.append("error: ").append(line).append("\n");
            }

            code = process.waitFor();

            if (code != 0) {
                result.append(command).append(" execute error!").append("\n");
            }

        } catch (Exception e) {
            result.append(e.getMessage()).append("\n");
            result.append(Arrays.toString(e.getStackTrace())).append("\n");
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (Exception e) {
                result.append(e.getMessage()).append("\n");
                result.append(Arrays.toString(e.getStackTrace())).append("\n");
            }
            try {
                if (errorReader != null) {
                    errorReader.close();
                }
            } catch (Exception e) {
                result.append(e.getMessage()).append("\n");
                result.append(Arrays.toString(e.getStackTrace())).append("\n");
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                result.append(e.getMessage()).append("\n");
                result.append(Arrays.toString(e.getStackTrace())).append("\n");
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                result.append(e.getMessage()).append("\n");
                result.append(Arrays.toString(e.getStackTrace())).append("\n");
            }

            this.result = result.toString();
            this.code = code;
        }

        return this;
    }
}
