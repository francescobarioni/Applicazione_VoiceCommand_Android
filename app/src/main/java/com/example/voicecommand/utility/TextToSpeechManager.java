package com.example.voicecommand.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextToSpeechManager {

    private static TextToSpeech textToSpeech = null;

    // metodo per impostare il textToSpeech
    public static TextToSpeech setTextToSpeech(Context context){
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

    public static void retardDialog(SpeechRecognizer speechRecognizer, Intent intent){
        // evito il conflitto tha il thread del textToSpeech e del speechRecognizer
        // aggiungendo un delay fra i due
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {speechRecognizer.startListening(intent);}
        }, 600); // imposto un delay
    }

    public static void retardDialogReimplementation(SpeechRecognizer speechRecognizer,Intent intent, int dimHashMap){
        int defaultDelay = 600;
        int dynamicDelay = defaultDelay + (dimHashMap * 550);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {speechRecognizer.startListening(intent);}
        },dynamicDelay);
    }


    // metodo per esprimere il messaggio
    public static void speak(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId");
    }

    public static Map<Integer,String> setHashMapMessage(Context context){
        // aggiungo i messaggi vocali da un file json a un hashmap
        String jsonStringMessage = JsonFileManager.readJsonFile(context,"message");
        try {
            return JsonFileManager.addMessageToHashMapByJsonFile(jsonStringMessage);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // metodo per rilasciare le risorse
    public static void release() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
