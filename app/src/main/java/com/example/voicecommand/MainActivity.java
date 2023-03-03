package com.example.voicecommand;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.voicecommand.activity_command.ActivitySettingsCommand;
import com.example.voicecommand.command.Command;
import com.example.voicecommand.command.OpenChromeCommand;
import com.example.voicecommand.command.OpenSettingsCommand;
import com.example.voicecommand.utility.ActivityManager;
import com.example.voicecommand.utility.AppManager;
import com.example.voicecommand.utility.IntentManager;
import com.example.voicecommand.utility.IntentRecognizer;
import com.example.voicecommand.utility.TextToSpeechManager;

import java.util.ArrayList;
import java.util.Locale;

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

    private boolean openSettingsCommand = false;
    private boolean accepted = false;
    private ArrayList<String> commandArrayList = new ArrayList<String>();
    private Command cmd = new Command();
    private TextToSpeechManager textToSpeechManager = new TextToSpeechManager();
    private IntentManager intentManager = new IntentManager(this);


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
        intentRecognizer.addCommand("apri impostazioni", new OpenSettingsCommand());
        commandArrayList.add("apri impostazioni"); // aggiungo comando al array list

        intentRecognizer.addCommand("apri chrome",new OpenChromeCommand());
        commandArrayList.add("apri chrome"); // aggiungo comando al array list

        // Inizializza la ImageView del microfono
        microfonoImageView = findViewById(R.id.microfono);

        // Inizializza il TextToSpeech per la riproduzione vocale di un messaggio all'utente
        textToSpeech = textToSpeechManager.setTextToSpeech(this);

        // Metodo chiamato quando si clicca sull'icona del microfono
        microfonoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Messaggio di feedback vocale
                String message = "Cosa posso fare per te?";
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId");

                // Prepara l'intento per la registrazione vocale
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentManager.setIntent(intent);

                // piccolo delay tra textToSpeech e attivazione microfono
                textToSpeechManager.retardFirstTextToSpeechDialog(speechRecognizer,intent);

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
                        // Estrae gli elementi della lista dei risultati
                        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        // Verifica se la lista non è vuota
                        if (matches != null && !matches.isEmpty()) {
                            // Estrae il primo comando della lista
                            String command = matches.get(0);
                            command = command.toLowerCase();
                            accepted = cmd.isCommandPresent(command,commandArrayList);

                            // Riconosce l'intent associato al comando
                            Intent intent = intentRecognizer.recognize(command);
                            // Avvia l'intent se non è nullo
                            if (intent != null) {
                                switch (command){

                                    case "apri chrome": // case per il comando "apri chrome"
                                        boolean isAppInstalledAndUpToDate = appManager.isAppInstalled("com.android.chrome");
                                        if(isAppInstalledAndUpToDate) {
                                            String message= "Applicazione presente nel dispositivo. Applicazione chrome in avvio!!";
                                            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId");

                                            // ritardo di poco l'evento dell'apertura dell'intent
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(intent);
                                                }
                                            },5000);
                                        }
                                        break;

                                    case "apri impostazioni": // case per il comando "apri impostazioni"
                                        String message = "Applicazione impostazioni in avvio!!";
                                        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(intent);
                                                openSettingsCommand = true;
                                            }
                                        },3000);

                                        break;
                                }


                            }
                        }

                        if(!accepted) cmd.repeatCommand(textToSpeech);
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
        textToSpeechManager.shutdown(textToSpeech);
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
        ActivityManager activityManager = new ActivityManager();
        if(openSettingsCommand) openSettingsCommand = activityManager.openNewActivity(this,ActivitySettingsCommand.class);
    }

    // Questo metodo viene chiamato quando l'activity viene messa in pausa
    // e serve per interrompere la registrazione audio.
    @Override
    protected void onPause() {
        super.onPause();
        speechRecognizer.stopListening();
    }

}