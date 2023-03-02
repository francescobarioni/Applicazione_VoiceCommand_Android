package com.example.voicecommand.interface_voice_command;

import android.content.Intent;
import android.speech.tts.TextToSpeech;

// Questa interfaccia definisce il metodo execute per l'esecuzione di un comando.
public interface ICommand {
    Intent execute();

}
