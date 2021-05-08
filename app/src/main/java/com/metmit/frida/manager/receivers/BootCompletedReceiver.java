package com.metmit.frida.manager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.metmit.frida.manager.MainActivity;
import com.metmit.frida.manager.utils.Frida;

public class BootCompletedReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){

            Frida.startService();

            Intent newIntent = new Intent(context, MainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);

            Toast.makeText(context, "启动 Frida Manager", Toast.LENGTH_SHORT).show();
        }
    }
}
