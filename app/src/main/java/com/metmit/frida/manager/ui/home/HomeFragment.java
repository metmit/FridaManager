package com.metmit.frida.manager.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.metmit.frida.manager.R;
import com.metmit.frida.manager.utils.Frida;
import com.metmit.frida.manager.utils.ShellHelper;
import com.metmit.frida.manager.utils.SpHelper;

public class HomeFragment extends Fragment {

    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        init();

        final Switch fridaSwitch = rootView.findViewById(R.id.switchFridaStatus);

        fridaSwitch.setChecked(Frida.isRunning());

        fridaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (fridaSwitch.isChecked()) {
                    Frida.startService();
                } else {
                    Frida.stopService();
                }
            }
        });

        return rootView;
    }


    protected void init() {

        // Frida 版本
        TextView textViewFridaVersion = rootView.findViewById(R.id.textViewFridaVersion);
        String homeFridaVersionString = getString(R.string.home_frida_version);
        String fridaVersion = Frida.getVersion();
        if (TextUtils.isEmpty(fridaVersion)) {
            homeFridaVersionString = String.format(homeFridaVersionString, "无法获取");
        } else {
            homeFridaVersionString = String.format(homeFridaVersionString, fridaVersion);
        }
        textViewFridaVersion.setText(homeFridaVersionString);

        // root 权限
        TextView textViewRoot = rootView.findViewById(R.id.textViewRoot);
        String homeRootString = getString(R.string.home_root_permission);
        if (new ShellHelper().executeSu("ps").getCode() == ShellHelper.SUCCESS_CODE) {
            homeRootString = String.format(homeRootString, "已获得");
        } else {
            homeRootString = String.format(homeRootString, "未获得");
        }
        textViewRoot.setText(homeRootString);

        SpHelper spHelper = new SpHelper(getContext(), SpHelper.SP_NAME_SETTINGS);

        // 开机启动
        TextView textViewBoot = rootView.findViewById(R.id.textViewBoot);
        String homeBootString = getString(R.string.home_boot);
        if (spHelper.getSp().getBoolean(SpHelper.SP_KEY_BOOT_FRIDA, false)) {
            homeBootString = String.format(homeBootString, "是");
        } else {
            homeBootString = String.format(homeBootString, "否");
        }
        textViewBoot.setText(homeBootString);

        // 设备型号
        TextView textViewDevice = rootView.findViewById(R.id.textViewDeviceType);
        String homeDeviceString = getString(R.string.home_device_type);
        homeDeviceString = String.format(homeDeviceString, Build.MANUFACTURER + " " + Build.BRAND + " " + Build.MODEL + " ("+ Build.DEVICE + ")");
        textViewDevice.setText(homeDeviceString);

        // 系统版本
        TextView textViewSystem = rootView.findViewById(R.id.textViewSystemVersion);
        String homeSystemString = getString(R.string.home_system_version);
        textViewSystem.setText(String.format(homeBootString, String.format(" Android %s (API %s)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT)));

        // 系统架构
        TextView textViewAbi= rootView.findViewById(R.id.textViewAbi);
        String homeAbiString = getString(R.string.home_abi);
        textViewAbi.setText(String.format(homeAbiString, Build.SUPPORTED_ABIS[0]));
    }
}