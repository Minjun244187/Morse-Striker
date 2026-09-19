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

        // Letter mode
        letterMode.setOnClickListener(v -> {
            Intent intent = new Intent(ModeSelectActivity.this, LetterPracticeMenuActivity.class);
            intent.putExtra("mode", "letter");
            startActivity(intent);  // No related the StartActivity
        });

        // Word mode
        wordMode.setOnClickListener(v -> {
            Intent intent = new Intent(ModeSelectActivity.this, WordPracticeMenuActivity.class);
            intent.putExtra("mode", "word");
            startActivity(intent);
        });

        // Sentence mode
        sentenceMode.setOnClickListener(v -> {
            Intent intent = new Intent(ModeSelectActivity.this, SentencePracticeMenuActivity.class);
            intent.putExtra("mode", "sentence");
            startActivity(intent);
        });
    }
}
