package com.example.voicecommand.command;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.AppManager;
import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.Map;

public class OpenChromeCommand implements ICommand {

    private Context context;
    private boolean isAppInstalled = false;
    private Map<Integer,String> messageMap;

    public OpenChromeCommand(Context context){
        this.context = context;
    }

    @Override
    public Intent execute() {
        isAppInstalled = AppManager.isAppInstalled(this.context,"com.android.chrome");
        if(isAppInstalled){
            Intent intent = new Intent();
            messageMap = TextToSpeechManager.setHashMapMessage(context);
            TextToSpeechManager.speak(messageMap.get(5).toString());

            try{
                String url = "https://www.google.it/";
                intent.setPackage("com.android.chrome");
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            }catch (Exception e){
                // chrome non è installato
                e.printStackTrace();
            }

            // ritardo di poco l'evento dell'apertura dell'intent
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.startActivity(intent);
                }
            },5000);

            return intent;
        } else {TextToSpeechManager.speak(messageMap.get(6).toString());}

        return null;
    }
}
