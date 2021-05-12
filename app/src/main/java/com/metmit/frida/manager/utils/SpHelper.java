package com.metmit.frida.manager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SpHelper {

    public static String SP_NAME_SETTINGS = "settings";

    public static String SP_KEY_BOOT_FRIDA = "boot_frida";

    public static String SP_KEY_FRIDA_PORT = "frida_port";

    protected Context context;
    protected String name;
    protected SharedPreferences sp;

    public SpHelper(Context context, String name) {
        this.context = context;
        this.name = name;
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public SharedPreferences.Editor getEditor() {
        return sp.edit();
    }

    public void put(HashMap<String, Object> data) {
        SharedPreferences.Editor editor = sp.edit();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            put(editor, key, value);
        }
        editor.apply();
    }

    public void put(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        put(editor, key, value);
        editor.apply();
    }

    protected void put(SharedPreferences.Editor editor, String key, Object value) {
        if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }

        if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }

        if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
    }
}
