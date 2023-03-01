package com.example.voicecommand.command;

import android.app.Activity;
import android.content.Intent;

import com.example.voicecommand.interface_voice_command.ICommand;

public class OpenBackCommand implements ICommand {

    private Activity activity;

    public OpenBackCommand(Activity activity){
        this.activity = activity;
    }

    @Override
    public Intent execute() {
        activity.onBackPressed();
        return null;
    }
}
