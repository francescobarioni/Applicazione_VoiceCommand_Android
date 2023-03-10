package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;

import com.example.voicecommand.MainActivity;
import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

public class GoBackMainActivityCommand implements ICommand {

    private Context context;

    public GoBackMainActivityCommand(Context context){
        this.context = context;
    }
    @Override
    public Intent execute() {
        TextToSpeechManager.speak("Torno indietro");
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return intent;
    }
}
