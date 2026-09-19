package com.example.morsestriker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WordPracticeMenuActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_practice_menu); // Check XML file names

        // -------------------------------
        // add action bar
        setupActionBarButtons();
        //------------------------------------

        // 9 Level buttons in the array
        Button[] levelButtons = new Button[]{
                findViewById(R.id.word_level01),
                findViewById(R.id.word_level02),
                findViewById(R.id.word_level03),
                findViewById(R.id.word_level04),
                findViewById(R.id.word_level05),
                findViewById(R.id.word_level06),
                findViewById(R.id.word_level07),
                findViewById(R.id.word_level08),
                findViewById(R.id.word_level09)
        };

        for (int i = 0; i < levelButtons.length; i++) {
            final int level = i + 1; // Level 1~13
            Button btn = levelButtons[i];
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(WordPracticeMenuActivity.this, WordPracticeActivity.class);
                intent.putExtra("Level", level);
                startActivity(intent);
            });
        }
    }

}
