package com.example.voicecommand.utility;

import android.content.Context;
import android.content.Intent;

public class ActivityManager {

    public boolean openNewActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context,cls);
        context.startActivity(intent);
        return false;
    }


}
