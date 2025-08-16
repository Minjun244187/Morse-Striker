package com.example.morsestriker;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ModeSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        Button letterMode = findViewById(R.id.letterModeButton);
        Button wordMode = findViewById(R.id.wordModeButton);
        Button sentenceMode = findViewById(R.id.sentenceModeButton);

        // by Letter
        letterMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("mode", "letter");
            startActivity(intent);
        });

        // by Word
        wordMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("mode", "word");
            startActivity(intent);
        });

        // by Sentence
        sentenceMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("mode", "sentence");
            startActivity(intent);
        });
    }
}
