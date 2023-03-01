package com.example.voicecommand.utility;

import android.content.Intent;

import com.example.voicecommand.interface_voice_command.ICommand;

import java.util.HashMap;
import java.util.Map;

// Questa classe gestisce il riconoscimento degli intent a partire dai comandi vocali.
public class IntentRecognizer {

    private Map<String, ICommand> commands = new HashMap<>();

    // Questo metodo permette di aggiungere un comando e il relativo IntentCommand al riconoscitore.
    public void addCommand(String command, ICommand icommand){
        commands.put(command,icommand);
    }

    // Questo metodo cerca il comando vocalmente pronunciato tra quelli memorizzati,
    // lo esegue attraverso l'IntentCommand associato e restituisce l'Intent risultante.
    public Intent recognize(String command){
        ICommand iCommand = commands.get(command.toLowerCase());
        if(iCommand != null){
            return iCommand.execute();
        }

        return null;
    }
}
