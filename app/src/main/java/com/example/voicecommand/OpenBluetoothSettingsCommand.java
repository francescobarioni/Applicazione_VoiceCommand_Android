package com.example.voicecommand;

import android.content.Intent;
import android.provider.Settings;

public class OpenBluetoothSettingsCommand implements ICommand{

    @Override
    public Intent execute() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
