package com.example.voicecommand.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Locale;

public class TextToSpeechManager {

    private TextToSpeech textToSpeech = null;

    // metodo per impostare il textToSpeech
    public TextToSpeech setTextToSpeech(Context context){
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Imposta la lingua dell'output vocale (in questo caso Italiano)
                    int result = textToSpeech.setLanguage(Locale.ITALIAN);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Se la lingua non è supportata, stampa un messaggio di errore
                        Log.e("TextToSpeech", "Language not supported");
                    }
                } else {
                    // Se si verifica un errore nella creazione del TextToSpeech, stampa un messaggio di errore
                    Log.e("TextToSpeech", "Initialization failed");
                }
            }
        });

        return textToSpeech;
    }

    public void retardFirstTextToSpeechDialog(SpeechRecognizer speechRecognizer, Intent intent){
        // evito il conflitto tha il thread del textToSpeech e del speechRecognizer
        // aggiungendo un delay fra i due
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {speechRecognizer.startListening(intent);}
        }, 600); //  600 millisecondi
    }

    public void shutdown(@NonNull TextToSpeech textToSpeech){
        textToSpeech.shutdown();
    }
}