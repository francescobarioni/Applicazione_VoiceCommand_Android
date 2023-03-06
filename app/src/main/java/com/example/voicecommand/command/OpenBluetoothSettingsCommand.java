package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

public class OpenBluetoothSettingsCommand implements ICommand {

    private Context context;

    public OpenBluetoothSettingsCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        TextToSpeechManager.speak("Apertura in corso!");
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return intent;
    }

}
