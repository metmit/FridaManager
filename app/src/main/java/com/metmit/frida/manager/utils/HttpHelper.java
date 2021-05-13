package com.metmit.frida.manager.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    public static File download(final String requestUrl, final String filename) {

        Helper.log(filename);

        new Thread() {
            public void run() {
                File file = downloadRequest(requestUrl, filename);
            }
        }.start();
        return null;
    }

    private static File downloadRequest(final String requestUrl, final String filename) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
            HttpURLConnection connection = getConnection(requestUrl);

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(50000);

            connection.connect();

            if (connection.getResponseCode() == 200) {

                inputStream = connection.getInputStream();

                byte[] outputBytes = readInputStream(inputStream);

                file = new File(filename);

                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(outputBytes);
            } else {
                Helper.log(String.format("下载失败：<%s> %s", connection.getResponseCode(), connection.getResponseMessage()));
            }
        } catch (Exception e) {
            Helper.log("下载失败：" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException ignored) {
            }
        }

        return file;
    }


    public static HttpURLConnection getConnection(String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        return connection;
    }

    public static byte[] readInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int readLen = 0;
            while ((readLen = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readLen);
            }
        } catch (IOException ignored) {
        } finally {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
        }
        return outputStream.toByteArray();
    }
}