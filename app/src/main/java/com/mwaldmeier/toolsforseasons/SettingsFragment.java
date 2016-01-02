package com.mwaldmeier.toolsforseasons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;


public class SettingsFragment extends android.app.Fragment {
    MainActivity mainActivity;
    Switch MuteSwitch;
    Switch P2PSwitch;
    Switch KeepScreenOnSwitch;

    public SettingsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mainActivity = (MainActivity) getActivity();

        MuteSwitch = (Switch) rootView.findViewById(R.id.muteSwitch);
        KeepScreenOnSwitch = (Switch) rootView.findViewById(R.id.keepScreenOnSwitch);
        P2PSwitch = (Switch) rootView.findViewById(R.id.p2PSwitch);

        if (mainActivity.getSoundOn().equals("1")) {
            MuteSwitch.setChecked(false);
        }
        if (mainActivity.getScreenAlwaysOn().equals("1")) {
            KeepScreenOnSwitch.setChecked(true);
        }
        if (mainActivity.getP2POn().equals("1")) {
            P2PSwitch.setChecked(true);
        }

        MuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String setting = mainActivity.SOUND_ON;
                String value = "1";
                if (isChecked) {
                    value = "0";
                }
                mainActivity.setSetting(setting, value);
            }
        });

        KeepScreenOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String setting = mainActivity.SCREEN_ALWAYS_ON;
                String value = "0";
                if (isChecked) {
                    value = "1";
                }
                mainActivity.setSetting(setting, value);
            }
        });

        P2PSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String setting = mainActivity.P2P_ON;
                String value = "0";
                if (isChecked) {
                    value = "1";
                }
                mainActivity.setSetting(setting, value);
            }
        });


        return rootView;
    }
}