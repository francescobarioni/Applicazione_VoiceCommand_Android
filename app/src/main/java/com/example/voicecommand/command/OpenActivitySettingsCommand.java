package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;

import com.example.voicecommand.activity_command.ActivitySettingsCommand;
import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

public class OpenActivitySettingsCommand implements ICommand {

    private Context context;
    //private Class<?> destinationActivity;

    public OpenActivitySettingsCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        TextToSpeechManager.speak("Avvio in corso!");
        Intent intent = new Intent(context,ActivitySettingsCommand.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return intent;
    }
}
