package com.example.voicecommand.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class AppManager{
    private final Context mContext;

    // costruttore
    public AppManager(Context context){
        mContext = context;
    }


    // metodo che verifica se un app è installata
    public boolean isAppInstalled(String packageName){
        boolean isInstalled = false;
        final PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return isInstalled; // app non installata
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        isInstalled = !list.isEmpty();
        return isInstalled; // app installata
    }
}
