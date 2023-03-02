package com.example.voicecommand.activity_command;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.voicecommand.MainActivity;
import com.example.voicecommand.R;
import com.example.voicecommand.command.OpenBackCommand;
import com.example.voicecommand.command.OpenBluetoothSettingsCommand;
import com.example.voicecommand.command.OpenChromeCommand;
import com.example.voicecommand.command.OpenNewActivityCommand;
import com.example.voicecommand.utility.IntentRecognizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

// activity utilizzata per quando si aprono le impostazioni
// da cui si possono lanciare dei sotto comandi vocali
public class ActivitySettingsCommand extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    private ImageView microfonoImageView;
    private IntentRecognizer intentRecognizer;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;

    private boolean accepted = false;
    private ArrayList<String> commandArrayList = new ArrayList<String>();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_command);

        // Verifica se l'applicazione ha il permesso di registrazione audio, altrimenti richiedilo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        }

        microfonoImageView = findViewById(R.id.microfono);

        // Inizializza il SpeechRecognizer per la registrazione e il riconoscimento vocale
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Inizializza l'IntentRecognizer per la gestione degli intent
        intentRecognizer = new IntentRecognizer();

        intentRecognizer.addCommand("indietro",new OpenNewActivityCommand(this, MainActivity.class));
        commandArrayList.add("indietro");

        intentRecognizer.addCommand("apri impostazioni bluetooth",new OpenBluetoothSettingsCommand());
        commandArrayList.add("apri impostazioni bluetooth");


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
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

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"");

        microfonoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Messaggio di feedback vocale
                String message = "Cosa posso fare per te?";
                textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH,params,"messageId");

                // Prepara l'intento per la registrazione vocale
                String prompt = "Dimmi cosa vuoi fare";
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,prompt);


                // evito il conflitto tha il thread del textToSpeech e del speechRecognizer
                // aggiungendo un delay fra i due
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speechRecognizer.startListening(intent);
                    }
                }, 1500); // 1500 millisecondi = 1.5 secondi


                // Imposta un listener per il riconoscimento vocale
                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {
                        // Metodo chiamato quando il riconoscitore vocale è pronto per la registrazione
                        Log.d("SpeechRecognizer", "onReadyForSpeech");
                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        // Metodo chiamato all'inizio della registrazione vocale
                        Log.d("SpeechRecognizer", "onBeginningOfSpeech");
                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {
                        // Metodo chiamato quando il livello di rumore di fondo cambia durante la registrazione vocale
                        Log.d("SpeechRecognizer", "onRmsChanged");
                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {
                        // Metodo chiamato quando il riconoscitore vocale riceve un buffer di dati audio durante la registrazione
                        Log.d("SpeechRecognizer", "onBufferReceived");
                    }

                    // Metodo chiamato quando la registrazione vocale è terminata
                    @Override
                    public void onEndOfSpeech() {
                        Log.d("SpeechRecognizer", "onEndOfSpeech");
                    }

                    // Metodo chiamato quando si verifica un errore durante la registrazione vocale
                    @Override
                    public void onError(int error) {
                        if(error == SpeechRecognizer.ERROR_NO_MATCH ||
                                error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT){

                            // L'input vocale non è stato compreso, ripeti la richiesta


                            // Si rimette in ascolto per un nuovo input vocale
                            speechRecognizer.startListening(intent);
                        } else {
                            Log.d("SpeechRecognizer", "onError: " + error);
                        }

                    }

                    // Metodo chiamato quando la registrazione vocale ha restituito risultati
                    @Override
                    public void onResults(Bundle results) {
                        Log.d("SpeechRecognizer", "onResults");
                        // Estrae gli elementi della lista dei risultati
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        // Verifica se la lista non è vuota
                        if (matches != null && !matches.isEmpty()) {
                            // Estrae il primo comando della lista
                            String command = matches.get(0);
                            command = command.toLowerCase();
                            isCommandPresent(command,commandArrayList);

                            // Riconosce l'intent associato al comando
                            Intent intent = intentRecognizer.recognize(command);
                            // Avvia l'intent se non è nullo
                            if (intent != null) {

                                switch (command){

                                    case "indietro": // case per il comando "indietro"
                                            String message = "Torno indietro";
                                            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId");
                                            startActivity(intent);
                                        break;

                                    case "apri impostazioni bluetooth": // case per il comando "apri impostazioni bluetooth"
                                        String messageOpenBluetoothSettingsCommand = "Apertura in corso";
                                        textToSpeech.speak(messageOpenBluetoothSettingsCommand, TextToSpeech.QUEUE_FLUSH, null, "messageId");
                                        startActivity(intent);
                                        break;

                                }
                            }
                        }

                        if(!accepted) repeatCommand(); // ripeti il comando nel caso
                    }

                    // Metodo chiamato quando sono disponibili risultati parziali della registrazione vocale
                    @Override
                    public void onPartialResults(Bundle partialResults) {
                        Log.d("SpeechRecognizer", "onPartialResults");
                    }

                    // Metodo chiamato quando si verifica un evento non specificato
                    @Override
                    public void onEvent(int eventType, Bundle params) {
                        Log.d("SpeechRecognizer", "onEvent");
                    }
                });
            }
        });
    }

    // metodo che chiede all'utente di ripetere il comando
    private void repeatCommand() {
        // usa il text-to-speech per far ripetere l'istruzione all'utente
        String message_one = "Mi dispiace, non ho capito o il comando non è presente! Potresti per favore ripetere?";
        textToSpeech.speak(message_one, TextToSpeech.QUEUE_FLUSH, null, "messageId");
    }

    private boolean isCommandPresent(String stringa, ArrayList<String> arrayList){
        for(String elemento : arrayList){
            if(elemento.equals(stringa)){
                accepted = true;
                return accepted;
            }
        }

        return accepted;
    }
}