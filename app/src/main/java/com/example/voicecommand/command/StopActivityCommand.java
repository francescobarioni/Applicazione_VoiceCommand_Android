package com.example.voicecommand.command;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.voicecommand.MainActivity;
import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.Map;

public class StopActivityCommand implements ICommand {

    private Context context;
    private Map<Integer,String> messageMap;

    public StopActivityCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        messageMap = TextToSpeechManager.setHashMapMessage(context);
        TextToSpeechManager.speak(messageMap.get(8).toString());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                System.exit(0);
            }
        },2500);
        return null;
    }
}
