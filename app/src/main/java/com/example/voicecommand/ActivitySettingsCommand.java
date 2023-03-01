package com.example.voicecommand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// activity utilizzata per quando si aprono le impostazioni
// da cui si possono lanciare dei sotto comandi vocali
public class ActivitySettingsCommand extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_command);
    }
}