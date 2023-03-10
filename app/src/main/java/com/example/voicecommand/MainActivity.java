package com.example.voicecommand;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.voicecommand.command.Command;
import com.example.voicecommand.interface_voice_command.ICommand;
import com.example.voicecommand.utility.ActivityManager;
import com.example.voicecommand.utility.AppManager;
import com.example.voicecommand.utility.IntentManager;
import com.example.voicecommand.utility.IntentRecognizer;
import com.example.voicecommand.utility.JsonFileManager;
import com.example.voicecommand.utility.TextToSpeechManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Costante utilizzata per la richiesta del permesso di registrazione audio
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    // Oggetti utilizzati per la registrazione e la gestione del riconoscimento vocale
    private SpeechRecognizer speechRecognizer;
    private IntentRecognizer intentRecognizer;

    // ImageView utilizzata per l'interazione dell'utente con il microfono
    private ImageView microfonoImageView;

    // Oggetto TextToSpeech utilizzato per la riproduzione vocale di un messaggio
    private TextToSpeech textToSpeech;
    private AppManager appManager = new AppManager(this);

    private ArrayList<String> commandArrayList = new ArrayList<String>();
    private Command cmd = new Command();
    private TextToSpeechManager textToSpeechManager = new TextToSpeechManager();
    private IntentManager intentManager = new IntentManager(this);
    private ActivityManager activityManager = new ActivityManager();
    private JsonFileManager jsonFileManager = new JsonFileManager();
    private Map<Integer,String> messageMap;

    // Metodo chiamato alla creazione dell'activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifica se l'applicazione ha il permesso di registrazione audio, altrimenti richiedilo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        }

        // Inizializza il SpeechRecognizer per la registrazione e il riconoscimento vocale
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Inizializza l'IntentRecognizer per la gestione degli intent
        intentRecognizer = new IntentRecognizer();

        // Inizializza la ImageView del microfono
        microfonoImageView = findViewById(R.id.microfono);

        // Inizializza il TextToSpeech per la riproduzione vocale di un messaggio all'utente
        textToSpeech = textToSpeechManager.setTextToSpeech(this);

        // creo la stringa che conterra il file json
        String jsonStringCommand = JsonFileManager.readJsonFile(this,"main_activity_command");
        // aggiungo i comandi dal file json
        JsonFileManager.addCommandToHashMapByJsonFile(this,intentRecognizer,jsonStringCommand);

        // aggiungo i messaggi vocali da un file json a un hashmap
        messageMap = TextToSpeechManager.setHashMapMessage(this);

        // Metodo chiamato quando si clicca sull'icona del microfono
        microfonoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Messaggio di feedback vocale
                TextToSpeechManager.speak(messageMap.get(1).toString());

                // Prepara l'intento per la registrazione vocale
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentManager.setIntent(intent);

                // piccolo delay tra textToSpeech e attivazione microfono
                textToSpeechManager.retardDialog(speechRecognizer,intent);

                // Imposta un listener per il riconoscimento vocale
                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                    // Metodo chiamato quando il riconoscitore vocale è pronto per la registrazione
                    @Override
                    public void onReadyForSpeech(Bundle params) {Log.d("SpeechRecognizer", "onReadyForSpeech");}

                    // Metodo chiamato all'inizio della registrazione vocale
                    @Override
                    public void onBeginningOfSpeech() {Log.d("SpeechRecognizer", "onBeginningOfSpeech");}

                    // Metodo chiamato quando il livello di rumore di fondo cambia durante la registrazione vocale
                    @Override
                    public void onRmsChanged(float rmsdB) {Log.d("SpeechRecognizer", "onRmsChanged");}

                    // Metodo chiamato quando il riconoscitore vocale riceve un buffer di dati audio durante la registrazione
                    @Override
                    public void onBufferReceived(byte[] buffer) {Log.d("SpeechRecognizer", "onBufferReceived");}

                    // Metodo chiamato quando la registrazione vocale è terminata
                    @Override
                    public void onEndOfSpeech() {Log.d("SpeechRecognizer", "onEndOfSpeech");}

                    // Metodo chiamato quando si verifica un errore durante la registrazione vocale
                    @Override
                    public void onError(int error) {
                        if(error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT){
                            // L'input vocale non è stato compreso, ripeti la richiesta
                            // Si rimette in ascolto per un nuovo input vocale
                            speechRecognizer.startListening(intent);
                        } else {Log.d("SpeechRecognizer", "onError: " + error);}
                    }

                    // Metodo chiamato quando la registrazione vocale ha restituito risultati
                    @Override
                    public void onResults(Bundle results) {
                        Log.d("SpeechRecognizer", "onResults");

                        // ottento un hash map <"stringa comando",comando da eseguire>
                        Map<String, ICommand> commands = new HashMap<>();
                        commands = intentRecognizer.getCommands();

                        // Estrae gli elementi della lista dei risultati
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        // Verifica se la lista non è vuota
                        if (matches != null && !matches.isEmpty()) {
                            // estrae il primo comando dalla lista
                            String command = matches.get(0);
                            command = command.toLowerCase();

                            // verifica se il comando è presente nell'hashMap
                            if(commands.containsKey(command)){
                                // riconosce l'intent associato al comando
                                Intent intent = intentRecognizer.recognize(command);
                                // avvia l'intent se non è nullo
                                if(intent != null)
                                    commands.get(command).execute();
                            } else {
                                // comando non trovato nell'hashmap
                                TextToSpeechManager.speak(messageMap.get(2).toString());
                            }
                        }
                    }

                    // Metodo chiamato quando sono disponibili risultati parziali della registrazione vocale
                    @Override
                    public void onPartialResults(Bundle partialResults) {Log.d("SpeechRecognizer", "onPartialResults");}

                    // Metodo chiamato quando si verifica un evento non specificato
                    @Override
                    public void onEvent(int eventType, Bundle params) {Log.d("SpeechRecognizer", "onEvent");}
                });
            }
        });


    }

    // Metodo chiamato quando l'activity viene distrutta
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera le risorse utilizzate dalla registrazione vocale e dal feedback vocale
        speechRecognizer.destroy();
        textToSpeechManager.release();
    }

    // Metodo chiamato quando si ricevono i risultati della richiesta di permesso di registrazione
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Verifica se il permesso è stato concesso
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Record audio permission granted");
        }
    }

    // Metodo chiamato quando l'activity riprende l'esecuzione
    @Override
    protected void onResume() {
        super.onResume();
    }

    // Questo metodo viene chiamato quando l'activity viene messa in pausa
    // e serve per interrompere la registrazione audio.
    @Override
    protected void onPause() {
        super.onPause();
        speechRecognizer.stopListening();
    }
}