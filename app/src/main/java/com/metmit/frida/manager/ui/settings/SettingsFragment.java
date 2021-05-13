package com.metmit.frida.manager.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.metmit.frida.manager.MainActivity;
import com.metmit.frida.manager.R;
import com.metmit.frida.manager.utils.Frida;
import com.metmit.frida.manager.utils.Helper;
import com.metmit.frida.manager.utils.PickFileHelper;
import com.metmit.frida.manager.utils.SpHelper;

public class SettingsFragment extends Fragment {

    private static final int PICK_FRIDA_REQUEST_CODE = 1001;

    SpHelper spHelper;

    View rootView;

    Window dialogWindow;

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

        // 安装
        initInstall();


        return rootView;
    }

    protected void initFridaPort() {

        int port = Frida.getPort();

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

        Window window = alertDialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        alertDialog.setContentView(R.layout.settings_frida_port);

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

    protected void initInstall() {

        installLocalUri = null;

        TextView textViewInstall = rootView.findViewById(R.id.settings_install);
        textViewInstall.setText(String.format("%s%s", textViewInstall.getContentDescription(), Frida.getVersion()));

        textViewInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFridaInstallDialog();
            }
        });
    }

    protected Uri installLocalUri;

    protected void showFridaInstallDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.show();

        dialogWindow = alertDialog.getWindow();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        alertDialog.setContentView(R.layout.settings_install_frida);

        EditText textViewOnline = dialogWindow.findViewById(R.id.settings_install_dialog_online);

        TextView textViewLocal = dialogWindow.findViewById(R.id.settings_install_dialog_local);
        textViewLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FRIDA_REQUEST_CODE);
            }
        });

        ConstraintLayout constraintLayoutLocal = dialogWindow.findViewById(R.id.settings_install_dialog_local_box);
        ConstraintLayout constraintLayoutOnline = dialogWindow.findViewById(R.id.settings_install_dialog_online_box);

        RadioGroup radioGroup = dialogWindow.findViewById(R.id.settings_install_dialog_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.settings_install_dialog_radio_local) {
                    constraintLayoutOnline.setVisibility(View.INVISIBLE);
                    constraintLayoutLocal.setVisibility(View.VISIBLE);
                }
                if (checkedId == R.id.settings_install_dialog_radio_online) {
                    constraintLayoutLocal.setVisibility(View.INVISIBLE);
                    constraintLayoutOnline.setVisibility(View.VISIBLE);
                }
            }
        });

        dialogWindow.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radioCheckedId = radioGroup.getCheckedRadioButtonId();

                if (installLocalUri == null) {
                    Toast.makeText(getContext(), "前选择文件", Toast.LENGTH_LONG).show();
                    return;
                }

                if (radioCheckedId != R.id.settings_install_dialog_radio_local) {
                    Toast.makeText(getContext(), "请重新从本地安装", Toast.LENGTH_LONG).show();
                    return;
                }

                Frida.installLocal(getContext(), installLocalUri);

                Frida.version = null;
                initInstall();
                alertDialog.dismiss();
            }
        });

        dialogWindow.findViewById(R.id.go_to_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String version = textViewOnline.getText().toString();
                String url = "";
                if (TextUtils.isEmpty(version)) {
                    url = "https://github.com/frida/frida/releases";
                } else {
                    url = String.format("https://github.com/frida/frida/releases/download/%s/%s", version, Frida.getXzFileName(version));
                }
                try {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(url));
                    intent.setAction(Intent.ACTION_VIEW);
                    getContext().startActivity(Intent.createChooser(intent, "请选择浏览器"));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "打开浏览器失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FRIDA_REQUEST_CODE && resultCode == MainActivity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                try {

                    installLocalUri = data.getData();

                    TextView textViewInstallLocal = dialogWindow.findViewById(R.id.settings_install_dialog_local);
                    textViewInstallLocal.setText(PickFileHelper.getFileName(getContext(), installLocalUri));

                } catch (Exception e) {
                    Toast.makeText(getContext(), "选择文件失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}