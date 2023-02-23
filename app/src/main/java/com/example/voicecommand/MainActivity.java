package com.example.voicecommand;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
    private TextToSpeech textToSpeech_support;


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

        // Aggiungi un comando per aprire le impostazioni all'IntentRecognizer
        intentRecognizer.addCommand("apri impostazioni", new SettingsCommand());

        // Inizializza la ImageView del microfono
        microfonoImageView = findViewById(R.id.microfono);

        // Inizializza il TextToSpeech per la riproduzione vocale di un messaggio all'utente
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


        // inizializzazione textToSpeech di supporto
        textToSpeech_support = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Imposta la lingua dell'output vocale (in questo caso Italiano)
                    int result = textToSpeech_support.setLanguage(Locale.ITALIAN);
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


        // Metodo chiamato quando si clicca sull'icona del microfono
        microfonoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Messaggio di feedback vocale
                String message = "Cosa posso fare per te?";
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId");
                // Prepara l'intento per la registrazione vocale
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                // Avvia la registrazione vocale
                speechRecognizer.startListening(intent);

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
                            repeatCommand();

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
                            // Riconosce l'intent associato al comando
                            Intent intent = intentRecognizer.recognize(command);
                            // Avvia l'intent se non è nullo
                            if (intent != null) {
                                startActivity(intent);
                            }
                        }
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
        String message_one = "Mi dispiace, non ho capito!";
        String message_two = "Potresti per favore ripetere?";
        textToSpeech.speak(message_one, TextToSpeech.QUEUE_FLUSH, null, "messageId");
        textToSpeech_support.speak(message_two, TextToSpeech.QUEUE_FLUSH, null, "messageId");
    }

    // Metodo chiamato quando l'activity viene distrutta
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera le risorse utilizzate dalla registrazione vocale e dal feedback vocale
        speechRecognizer.destroy();
        textToSpeech.shutdown();
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
        // Eventuali azioni da eseguire all'apertura dell'activity
    }

    // Questo metodo viene chiamato quando l'activity viene messa in pausa
    // e serve per interrompere la registrazione audio.
    @Override
    protected void onPause() {
        super.onPause();
        speechRecognizer.stopListening();
    }

    // Questa classe gestisce il riconoscimento degli intent a partire dai comandi vocali.
    private class IntentRecognizer {
        private Map<String, IntentCommand> commands = new HashMap<>();

        // Questo metodo permette di aggiungere un comando e il relativo IntentCommand al riconoscitore.
        public void addCommand(String command, IntentCommand intentCommand) {
            commands.put(command, intentCommand);
        }

        // Questo metodo cerca il comando vocalmente pronunciato tra quelli memorizzati,
        // lo esegue attraverso l'IntentCommand associato e restituisce l'Intent risultante.
        public Intent recognize(String command) {
            IntentCommand intentCommand = commands.get(command.toLowerCase());
            if (intentCommand != null) {
                return intentCommand.execute();
            }
            return null;
        }
    }
    // Questa interfaccia definisce il metodo execute per l'esecuzione di un comando.
    private interface IntentCommand {
        Intent execute();
    }

    // Questa classe rappresenta il comando di apertura delle impostazioni di sistema.
    private class SettingsCommand implements IntentCommand {
        @Override
        public Intent execute() {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }
    }
}