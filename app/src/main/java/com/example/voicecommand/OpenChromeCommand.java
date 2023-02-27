package com.example.voicecommand;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class OpenChromeCommand implements ICommand{

    @Override
    public Intent execute() {
        Intent intent = new Intent();
        try{
            String url = "https://www.google.it/";
            intent.setPackage("com.android.chrome");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

        }catch (Exception e){
            // chrome non è installato
            e.printStackTrace();
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
