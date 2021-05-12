package com.metmit.frida.manager.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.metmit.frida.manager.R;
import com.metmit.frida.manager.utils.Frida;
import com.metmit.frida.manager.utils.Helper;
import com.metmit.frida.manager.utils.SpHelper;

public class SettingsFragment extends Fragment {

    SpHelper spHelper;

    View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        spHelper = new SpHelper(getContext(), SpHelper.SP_NAME_SETTINGS);

        // 开机启动
        final Switch bootFridaSwitch = rootView.findViewById(R.id.settings_switch_boot_frida);
        bootFridaSwitch.setChecked(spHelper.getSp().getBoolean(SpHelper.SP_KEY_BOOT_FRIDA, false));
        bootFridaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spHelper.put(SpHelper.SP_KEY_BOOT_FRIDA, bootFridaSwitch.isChecked());
            }
        });

        // 绑定端口号
        initFridaPort();


        return rootView;
    }

    protected void initFridaPort() {
        int port = spHelper.getSp().getInt(SpHelper.SP_KEY_FRIDA_PORT, Frida.DEFAULT_PORT);

        TextView textViewPort = rootView.findViewById(R.id.settings_frida_port);
        textViewPort.setText(String.format("%s%s", textViewPort.getContentDescription(), port));

        textViewPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFridaPortDialog(port);
            }
        });
    }

    protected void showFridaPortDialog(int port) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.show();
        alertDialog.setContentView(R.layout.settings_frida_port);

        Window window = alertDialog.getWindow();

        EditText editText = (EditText) window.findViewById(R.id.editTextPort);
        editText.setText(String.valueOf(port));

        window.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int port = Integer.parseInt(editText.getText().toString());
                if (port <= 1024 || port > 65535) {
                    Toast.makeText(getContext(), "端口范围 [1024~65535]", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Helper.isPortAvailable(port)) {
                    Toast.makeText(getContext(), "端口被占用", Toast.LENGTH_SHORT).show();
                    return;
                }

                spHelper.put(SpHelper.SP_KEY_FRIDA_PORT, port);

                initFridaPort();

                alertDialog.dismiss();
            }
        });

    }
}
