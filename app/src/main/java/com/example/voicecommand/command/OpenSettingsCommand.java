package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;

import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

// Questa classe rappresenta il comando di apertura delle impostazioni di sistema.
public class OpenSettingsCommand implements ICommand {

    private Context mcontext;

    public OpenSettingsCommand(Context context){
        this.mcontext = context;
    }

    @Override
    public Intent execute() {
        String message = "Applicazione impostazioni in avvio!!";
        TextToSpeechManager.speak(message);
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mcontext.startActivity(intent);
            }
        },3000);
        return intent;
    }
}
