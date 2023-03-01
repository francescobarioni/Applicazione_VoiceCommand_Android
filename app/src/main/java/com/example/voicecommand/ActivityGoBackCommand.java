package com.example.voicecommand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// activity che permette di avere il microfono sempre in ascolto in modo che
// riconosca il comando torna in dietro per evitare di premere il pulsante fisico.
// Server per navigare tra le varie activity.
public class ActivityGoBackCommand extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_back_command);
    }
}