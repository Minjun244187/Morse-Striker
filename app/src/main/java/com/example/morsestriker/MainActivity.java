package com.example.morsestriker;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView countdownText;

    private TextView morseOutput;
    private long pressStartTime;
    private static final int DOT_THRESHOLD = 150;
    private TextView timerText;
    private CountDownTimer gameTimer;
    private static final long PAUSE_THRESHOLD = 300;

    private static final long SPACE_THRESHOLD = 3 * PAUSE_THRESHOLD;
    private Handler handler = new Handler();
    private Runnable pauseAndConvertRunnable = new Runnable() {
        @Override
        public void run() {
            appendMorse("  "); // 스페이스 입력
            autoConvertMorse();
        }
    };

    private Runnable spaceRunnable = new Runnable() {
        @Override
        public void run() {
            appendMorse(" // "); // 스페이스 입력
            autoConvertMorse();
        }
    };
    public String letterPool;

    private static final Map<String, String> MORSE_MAP = new HashMap<String, String>() {{
        put(".-", "A");
        put("-...", "B");
        put("-.-.", "C");
        put("-..", "D");
        put(".", "E");
        put("..-.", "F");
        put("--.", "G");
        put("....", "H");
        put("..", "I");
        put(".---", "J");
        put("-.-", "K");
        put(".-..", "L");
        put("--", "M");
        put("-.", "N");
        put("---", "O");
        put(".--.", "P");
        put("--.-", "Q");
        put(".-.", "R");
        put("...", "S");
        put("-", "T");
        put("..-", "U");
        put("...-", "V");
        put(".--", "W");
        put("-..-", "X");
        put("-.--", "Y");
        put("--..", "Z");
        put("-----", "0");
        put(".----", "1");
        put("..---", "2");
        put("...--", "3");
        put("....-", "4");
        put(".....", "5");
        put("-....", "6");
        put("--...", "7");
        put("---..", "8");
        put("----.", "9");
        put("........", "HH");
        put("//", " ");
    }};

    private String convertMorseToText(String morseInput) {
        StringBuilder result = new StringBuilder();
        // Segregate CW by space
        String[] morseChars = morseInput.trim().split("\\s+");

        for (String code : morseChars) {
            String letter = MORSE_MAP.get(code);
            if (letter != null) {
                result.append(letter);
            } else {
                result.append("?"); // if none match
            }
        }
        return result.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.inputText);
        countdownText = findViewById(R.id.countdownText);
        Button clearButton = findViewById(R.id.clearButton);

        clearButton.setOnClickListener(v -> {
            morseOutput.setText("Output:");  // 출력 초기화
            textView.setText("");                // 입력 텍스트도 초기화
        });


        startCountdown();

        String mode = getIntent().getStringExtra("mode");

        if ("letter".equals(mode)) {
            // 글자별 연습 로직
        } else if ("word".equals(mode)) {
            // 단어별 연습 로직
        } else if ("sentence".equals(mode)) {
            // 문장별 연습 로직
        }

        morseOutput = findViewById(R.id.morseOutput);
        Button morseKey = findViewById(R.id.morseKey);

        morseKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        handler.removeCallbacks(pauseAndConvertRunnable);
                        handler.removeCallbacks(spaceRunnable);
                        return true; // event consume

                    case MotionEvent.ACTION_UP:
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if (pressDuration < DOT_THRESHOLD) {
                            appendMorse(".");
                        } else {
                            appendMorse("-");
                        }
                        handler.postDelayed(pauseAndConvertRunnable, PAUSE_THRESHOLD);
                        handler.postDelayed(spaceRunnable, SPACE_THRESHOLD);
                        return true;
                }
                return false;
            }

        });

    }

    private void autoConvertMorse() {
        String morse = morseOutput.getText().toString().replace("Output:", "").trim();
        if (!morse.isEmpty()) {
            String text = convertMorseToText(morse);
            TextView input = findViewById(R.id.inputText);
            input.setText(text);
        }
    }

    private void appendMorse(String symbol) {

        String current = morseOutput.getText().toString();
        if (symbol.equals(" // ")) {
            if (current.substring(current.length() - 4).equals(symbol)) {
                return;
            }
        }
        morseOutput.setText(current + "" + symbol);
    }
    private void startCountdown() {
        new CountDownTimer(3000, 1000) { // 3 sec countdown
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) + 1;
                countdownText.setText(String.valueOf(seconds));
            }

            @Override
            public void onFinish() {
                countdownText.setText("GO!");
            }
        }.start();
    }

}