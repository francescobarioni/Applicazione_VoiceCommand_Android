package com.example.voicecommand.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.List;

public class AppManager{
    private Context mContext;

    // costruttore
    public AppManager(Context context){
        mContext = context;
    }


    // metodo che verifica se un app è installata
    public boolean isAppInstalled(String packageName){
        boolean ok = false;
        final PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return ok; // app non installata
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        ok = !list.isEmpty();
        return ok; // app installata
    }
}
