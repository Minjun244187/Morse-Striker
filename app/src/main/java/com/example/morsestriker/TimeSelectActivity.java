package com.example.morsestriker;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TimeSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_select);

        Button oneMinMode = findViewById(R.id.oneMinButton);
        Button halfMinMode = findViewById(R.id.halfMinButton);
        Button unLimMode = findViewById(R.id.unlimitedTimeButton);

        // by Letter
        oneMinMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("time", "1m");
            startActivity(intent);
        });

        // by Word
        halfMinMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("time", "30s");
            startActivity(intent);
        });

        // by Sentence
        unLimMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("time", "no");
            startActivity(intent);
        });
    }
}
