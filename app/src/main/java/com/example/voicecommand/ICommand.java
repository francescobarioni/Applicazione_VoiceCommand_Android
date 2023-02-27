package com.example.voicecommand;

import android.content.Intent;

// Questa interfaccia definisce il metodo execute per l'esecuzione di un comando.
public interface ICommand {
    Intent execute();
}
