package com.example.voicecommand.utility;

import android.content.Context;

import com.example.voicecommand.R;
import com.example.voicecommand.interface_voice_command.ICommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Scanner;

public class JsonFileManager {

    public String readJsonFile(Context context){
        InputStream inputStream = context.getResources().openRawResource(R.raw.main_activity_command);
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        return jsonString;
    }

    public void addCommandToHashMapByJsonFile(Context context, IntentRecognizer intentRecognizer, String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray comandiArray = json.getJSONArray("comandi");

            for (int i = 0; i < comandiArray.length(); i++) {
                // elaboro la struttura del file json
                JSONObject comando = comandiArray.getJSONObject(i);
                String comando_vocale = comando.getString("comando_vocale");
                String classe = comando.getString("classe");

                ICommand command = null;

                try {
                    // utilizzo la tecnica della riflessione per prendere in maniera dinamica
                    // i nomi delle classi
                    Class<?> clazz = Class.forName(classe);
                    Constructor<?> ctor;
                    ctor = clazz.getConstructor(Context.class);
                    Object object = ctor.newInstance(context);
                    if (object instanceof ICommand) {
                        command = (ICommand) object;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                intentRecognizer.addCommand(comando_vocale, command);
            }
        } catch (JSONException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }




}
