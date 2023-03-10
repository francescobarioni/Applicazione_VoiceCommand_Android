package com.example.voicecommand.utility;

import android.content.Context;

import com.example.voicecommand.R;
import com.example.voicecommand.interface_voice_command.ICommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JsonFileManager {

    // Metodo che legge il contenuto di un file JSON e lo restituisce come stringa
    public String readJsonFile(Context context, String fileName){
        // Ottiene l'InputStream del file JSON
        int resourceId = context.getResources().getIdentifier(fileName,"raw",context.getPackageName());
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        // Crea una stringa con il contenuto dell'InputStream
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        // Restituisce la stringa del file JSON
        return jsonString;
    }

    // Metodo che elabora un file JSON contenente comandi vocali e li aggiunge all'IntentRecognizer
    public void addCommandToHashMapByJsonFile(Context context, IntentRecognizer intentRecognizer, String jsonString) {
        try {
            // Crea un oggetto JSON a partire dalla stringa del file JSON
            JSONObject json = new JSONObject(jsonString);
            // Ottiene l'array di comandi dal JSON
            JSONArray comandiArray = json.getJSONArray("comandi");

            // Cicla attraverso l'array di comandi
            for (int i = 0; i < comandiArray.length(); i++) {
                // Ottiene l'oggetto JSON che rappresenta il comando
                JSONObject comando = comandiArray.getJSONObject(i);
                // Ottiene la stringa del comando vocale dal JSON
                String comando_vocale = comando.getString("comando_vocale");
                // Ottiene la stringa del nome della classe dal JSON
                String classe = comando.getString("classe");

                // Crea un'istanza di ICommand
                ICommand command = null;

                try {
                    // Utilizza la riflessione per ottenere la classe a partire dal nome
                    Class<?> clazz = Class.forName(classe);
                    // Ottiene il costruttore con un argomento di tipo Context
                    Constructor<?> ctor;
                    ctor = clazz.getConstructor(Context.class);
                    // Crea un'istanza dell'oggetto a partire dal costruttore e dal Context
                    Object object = ctor.newInstance(context);
                    // Se l'oggetto creato è di tipo ICommand lo assegna a command
                    if (object instanceof ICommand) {
                        command = (ICommand) object;
                    }

                } catch (Exception e) {
                    // In caso di errore di riflessione stampa l'eccezione
                    e.printStackTrace();
                }

                // Aggiunge il comando vocal al IntentRecognizer associandolo all'istanza di ICommand creata
                intentRecognizer.addCommand(comando_vocale, command);
            }
        } catch (JSONException e) {
            // In caso di errore di parsing del JSON stampa l'eccezione
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<Integer,String> addMessageToHashMapByJsonFile(String jsonString) throws JSONException{
        Map<Integer,String> messageMap = new HashMap<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray messagesArray = jsonObject.getJSONArray("messages");

        for(int i = 0;i < messagesArray.length(); i++){
            JSONObject messageObject = messagesArray.getJSONObject(i);
            int id = messageObject.getInt("id");
            String text = messageObject.getString("text");
            messageMap.put(id,text);
        }

        return messageMap;
    }
}
