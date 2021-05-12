package com.metmit.frida.manager.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PickFileHelper {

    /**
     * 获取内容
     */
    public static InputStream getInputStream(@NonNull Context context, @NonNull Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(@NonNull Context context, @NonNull Uri uri) {
        String type = context.getContentResolver().getType(uri);
        if (TextUtils.isEmpty(type)) return null;
        return type;
    }

    /**
     * 获取文件名称
     */
    public static String getFileName(@NonNull Context context, @NonNull Uri uri) {
        String filename = "";
        Cursor returnCursor = context.getContentResolver().query(uri, null,
                null, null, null);
        if (returnCursor != null) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            filename = returnCursor.getString(nameIndex);
            returnCursor.close();
        }
        return filename;
    }

    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream inputStream = getInputStream(context, uri);
        if (inputStream == null) return null;
        return getBytes(inputStream);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static File saveFile(InputStream inputStream, String outFile) throws IOException {

        File file = new File(outFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        int len;
        byte[] buffer = new byte[4096];
        while (-1 != (len = inputStream.read(buffer))) {
            fileOutputStream.write(buffer, 0, len);
            fileOutputStream.flush();
        }
        inputStream.close();
        fileOutputStream.close();
        return file;
    }
}
