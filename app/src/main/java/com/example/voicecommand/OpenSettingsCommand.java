package com.example.voicecommand;

import android.content.Intent;
import android.provider.Settings;

// Questa classe rappresenta il comando di apertura delle impostazioni di sistema.
public class OpenSettingsCommand implements ICommand{

    @Override
    public Intent execute() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
