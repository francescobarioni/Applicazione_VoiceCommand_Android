package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;

import com.example.voicecommand.activity_command.SettingsActivityCommand;
import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.Map;

public class OpenActivitySettingsCommand implements ICommand {

    private Context context;
    private Map<Integer,String> messageMap;

    public OpenActivitySettingsCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        messageMap = TextToSpeechManager.setHashMapMessage(context);
        TextToSpeechManager.speak(messageMap.get(3).toString());

        Intent intent = new Intent(context, SettingsActivityCommand.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return intent;
    }
}
