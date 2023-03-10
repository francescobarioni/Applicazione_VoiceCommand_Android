package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;

import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.Map;

// Questa classe rappresenta il comando di apertura delle impostazioni di sistema.
public class OpenSettingsCommand implements ICommand {

    private Context context;
    private Map<Integer,String> messageMap;

    public OpenSettingsCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        messageMap = TextToSpeechManager.setHashMapMessage(context);
        TextToSpeechManager.speak(messageMap.get(7).toString());
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        },3000);
        return intent;
    }
}
