package com.example.morsestriker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Connect with a minimal layout (an empty screen is also possible)
        setContentView(android.R.layout.simple_list_item_1);
    }
}
