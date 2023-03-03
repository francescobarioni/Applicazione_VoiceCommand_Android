package com.example.voicecommand.utility;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;

public class IntentManager {

    private Context mContext;

    public IntentManager(Context context) {
        mContext = context;
    }

    public void setIntent(Intent intent) {
        String prompt = "Attivati";
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,prompt);
    }
}
