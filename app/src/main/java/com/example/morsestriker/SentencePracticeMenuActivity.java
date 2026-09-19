package com.example.morsestriker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SentencePracticeMenuActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_practice_menu); // Check XML file name

        // -------------------------------
        // add action bar
        setupActionBarButtons();
        //------------------------------------

        //5 Level buttons 5 in the array
        Button[] levelButtons = new Button[]{
                findViewById(R.id.sentence_level01),
                findViewById(R.id.sentence_level02),
                findViewById(R.id.sentence_level03),
                findViewById(R.id.sentence_level04),
                findViewById(R.id.sentence_level05)
        };

        for (int i = 0; i < levelButtons.length; i++) {
            final int level = i + 1; // Level 1~13
            Button btn = levelButtons[i];
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(SentencePracticeMenuActivity.this, SentencePracticeActivity.class);
                intent.putExtra("Level", level);
                startActivity(intent);
            });
        }
    }
}
