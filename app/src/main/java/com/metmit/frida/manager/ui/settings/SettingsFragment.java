package com.metmit.frida.manager.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.metmit.frida.manager.R;
import com.metmit.frida.manager.utils.SpHelper;

public class SettingsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        SpHelper spHelper = new SpHelper(getContext(), SpHelper.SP_NAME_SETTINGS);

        final Switch bootFridaSwitch = root.findViewById(R.id.switcher_2);

        bootFridaSwitch.setChecked(spHelper.getSp().getBoolean(SpHelper.SP_KEY_BOOT_FRIDA, false));

        bootFridaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spHelper.put(SpHelper.SP_KEY_BOOT_FRIDA, bootFridaSwitch.isChecked());
            }
        });

        return root;
    }
}