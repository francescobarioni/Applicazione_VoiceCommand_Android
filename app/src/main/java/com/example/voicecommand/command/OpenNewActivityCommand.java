package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;

import com.example.voicecommand.interface_voice_command.ICommand;

public class OpenNewActivityCommand implements ICommand {

    private Context context;
    private Class<?> destinationActivity;

    public OpenNewActivityCommand(Context context, Class<?> destinationActivity){
        this.context = context;
        this.destinationActivity = destinationActivity;
    }
    @Override
    public Intent execute() {
        Intent intent = new Intent(context,destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public void start(){
        Intent intent = execute();
        context.startActivity(intent);
    }
}
