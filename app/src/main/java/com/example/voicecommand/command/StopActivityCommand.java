package com.example.voicecommand.command;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

public class StopActivityCommand implements ICommand {

    private Context context;

    public StopActivityCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        TextToSpeechManager.speak("Chiusura dell'app in corso!");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity) {
                    ((Activity) context).finishAndRemoveTask();
                    System.exit(0);
                }
            }
        },2500);
        return null;
    }
}
