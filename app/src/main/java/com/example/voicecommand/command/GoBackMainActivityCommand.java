package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;

import com.example.voicecommand.MainActivity;
import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.Map;

public class GoBackMainActivityCommand implements ICommand {

    private Context context;
    private Map<Integer,String> messageMap;

    public GoBackMainActivityCommand(Context context){
        this.context = context;
    }
    @Override
    public Intent execute() {
        messageMap = TextToSpeechManager.setHashMapMessage(context);
        TextToSpeechManager.speak(messageMap.get(4).toString());

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return intent;
    }
}
