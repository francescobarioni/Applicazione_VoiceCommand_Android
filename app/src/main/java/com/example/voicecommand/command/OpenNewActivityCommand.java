package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

public class OpenNewActivityCommand implements ICommand {

    private Context context;
    private Class<?> destinationActivity;

    public OpenNewActivityCommand(Context context, Class<?> destinationActivity){
        this.context = context;
        this.destinationActivity = destinationActivity;
    }
    @Override
    public Intent execute() {
        TextToSpeechManager.speak("Torno indietro");
        Intent intent = new Intent(context,destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return intent;
    }
}
