package com.example.voicecommand.command;

import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;

import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.ArrayList;

public class Command {

    public void repeatCommand(@NonNull TextToSpeech textToSpeech) {
        // usa il text-to-speech per far ripetere l'istruzione all'utente
        TextToSpeechManager.speak("Mi dispiace, non ho capito o il comando non è presente! Potresti per favore ripetere?");
    }

    public boolean isCommandPresent(String stringa, @NonNull ArrayList<String> arrayList){
        for(String elemento : arrayList){
            if(elemento.equals(stringa)){
                return true;
            }
        }

        return false;
    }
}
