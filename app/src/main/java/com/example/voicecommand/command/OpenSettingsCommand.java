package com.example.voicecommand.command;

import android.content.Intent;
import android.provider.Settings;

import com.example.voicecommand.interface_voice_command.ICommand;

// Questa classe rappresenta il comando di apertura delle impostazioni di sistema.
public class OpenSettingsCommand implements ICommand {

    @Override
    public Intent execute() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
