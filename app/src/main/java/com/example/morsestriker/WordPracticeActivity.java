package com.example.morsestriker;

import android.annotation.SuppressLint;
import android.view.View;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WordPracticeActivity extends BaseActivity {

    private Handler handler = new Handler();

    // Dot/Dash touch states
    private boolean isPressingDot = false;
    private boolean isPressingDash = false;
    private boolean isKeyPracticeActive = false;

    // Button play states
    private boolean[] isPlaying = new boolean[3];
    private int[] currentIndex = new int[3];
    private Runnable[] runnables = new Runnable[3];

    private TextView[] dotDashViews = new TextView[3];
    private TextView[] wordViews = new TextView[3];
    private Button[] playButtons = new Button[3];

    private TextView morseOutput;
    private TextView qView;

    private Button keyPracticeBtn;


    private int userDelay = 1500;
    private int user_pause_delay = 3000;
    private boolean Incorrect = false;
    private Handler userHandler = new Handler();
    private final Object morseLock = new Object();
    private Runnable clearRunnable = null;
    private ToneGenerator ding = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    // Key Practice input
    private StringBuilder userInputMorse = new StringBuilder();
    private int currentLevel;
    // Define checkUserInput Runnable
    private final Runnable checkUserInputRunnable = this::checkUserInput;

    private KeyPracticeGame word_gameView;

    private final int word = 1;
    private long lastInputTime = 0; // assign last/dot/dash time


    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_practice);

        // -------------------------------
        // add action bar
        setupActionBarButtons();
        //------------------------------------

        // Initialize views
        dotDashViews = new TextView[] {
                findViewById(R.id.dot_dash1),
                findViewById(R.id.dot_dash2),
                findViewById(R.id.dot_dash3)
        };

        wordViews = new TextView[] {
                findViewById(R.id.letter1),
                findViewById(R.id.letter2),
                findViewById(R.id.letter3)
        };

        playButtons = new Button[] {
                findViewById(R.id.play1),
                findViewById(R.id.play2),
                findViewById(R.id.play3)
        };

        int level = getIntent().getIntExtra("Level", 1) - 1; // 0-based
        currentLevel = level;

        // Dot/Dash buttons
        Button dot_key = findViewById(R.id.dot_key);
        Button dash_key = findViewById(R.id.dash_key);

        morseOutput = findViewById(R.id.morseOutput);
        qView = findViewById(R.id.sView);
        word_gameView = findViewById(R.id.gameArea);
        keyPracticeBtn = findViewById(R.id.keyPracticeButton);

        // Start game when clicking the Key Practice button
        keyPracticeBtn.setOnClickListener(v -> {
            keyPracticeBtn.setText("Key Practice");
            qView.setText("");

            // init input output
            morseOutput.setText("");
            userInputMorse.setLength(0);
            lastInputTime = 0;


            word_gameView.setCharList(word, currentLevel);
            word_gameView.startGame();
            isKeyPracticeActive = true;
        });
        // register callback
        word_gameView.setGameEndCallback(() -> {
            //call when all words fall down.
            runOnUiThread(() -> {
                checkUserInput(); // cal score and mark
                isKeyPracticeActive = false;
                keyPracticeBtn.setText("Try Again ?");
            });
        });

// Dot key
        dot_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 단발 점 입력
                    playDotOnce();
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, user_pause_delay);
                    return true;
            }
            return false;
        });

// Dash key
        dash_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // input dash
                    playDashOnce();
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, user_pause_delay);
                    return true;
            }
            return false;
        });
/*
        // Dot key
        dot_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressingDot = true;
                    playDotWithDelay(); //
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPressingDot = false;
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, user_pause_delay);
                    return true;
            }
            return false;
        });

        // Dash key
        dash_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressingDash = true;
                    playDashWithDelay(); //
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPressingDash = false;
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, user_pause_delay);
                    return true;
            }
            return false;
        });
*/
        // Setup letters and buttons based on level
        for (int i = 0; i < dotDashViews.length; i++) {
            if (i < PracticeData.level_words[level].length) {
                dotDashViews[i].setVisibility(View.VISIBLE);
                dotDashViews[i].setText(""); // clear dot/dash
                wordViews[i].setVisibility(View.VISIBLE);
                wordViews[i].setText(PracticeData.level_words[level][i][0]); // show letter immediately
                playButtons[i].setVisibility(View.VISIBLE);

                int index = i;
                String word = PracticeData.level_words[level][i][0];
                String morse = PracticeData.level_words[level][i][1];
                playButtons[i].setOnClickListener(v -> togglePlay(index, morse));

            } else {
                dotDashViews[i].setVisibility(View.GONE);
                wordViews[i].setVisibility(View.GONE);
                playButtons[i].setVisibility(View.GONE);
            }
        }
    }

    private void recordUserSymbol(char c) {
        if (isKeyPracticeActive) {
            userInputMorse.append(c); // update answer-comparing buffer
        }
    }
private void playDotWithDelay() {
    if (!isPressingDot) return;

    // play dot sound
    MorseSoundPlayer.playTone(MorseSoundPlayer.getDotSoundDuration());

    if (isKeyPracticeActive) {
        appendMorseOutText("."); // call outtext
        recordUserSymbol('.');  // directly update the input info.
    }

    handler.postDelayed(this::playDotWithDelay,
            MorseSoundPlayer.getDotSoundDuration() + MorseSoundPlayer.getInterSymbolNOSoundDelay());
}

    private void playDashWithDelay() {
        if (!isPressingDash) return;

        // play dash sound
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDashSoundDuration());

        if (isKeyPracticeActive) {

            appendMorseOutText("-"); // call outtext
            recordUserSymbol('-');  // directly update the input info.
        }

        handler.postDelayed(this::playDashWithDelay,
                MorseSoundPlayer.getDashSoundDuration() + MorseSoundPlayer.getInterSymbolNOSoundDelay());
    }

    // input dot
    private void playDotOnce() {
        if (!isKeyPracticeActive) return;

        // 소리 재생
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDotSoundDuration());
        appendMorseOutTextSmart(".");
        // 사용자 입력 기록
        recordUserSymbol('.');

    }

    // 단발 대시 입력
    private void playDashOnce() {
        if (!isKeyPracticeActive) return;

        // 소리 재생
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDashSoundDuration());
        appendMorseOutTextSmart("-");
        // 사용자 입력 기록
        recordUserSymbol('-');

    }

    private long lastDisplayTime = 0;

    private void appendMorseOutTextSmart(String s) {
        int delay =0;

        if (!isKeyPracticeActive) return;

        if(s.equals(".")) {delay = 500;}
        else if(s.equals("-")) {delay = 900;}
        else delay = 500;

        synchronized (morseLock) {
            String currentText = morseOutput.getText().toString();
            long now = System.currentTimeMillis(); //add current time


            //ONlY space at Morseout textview for readability
            if (!currentText.isEmpty() && !currentText.endsWith(" ")) {
                morseOutput.append(" "); // space between symbol
            }


            // add space for user input to exceed 500sec
            if (lastInputTime > 0 && now - lastInputTime > delay) {
              //  System.out.println(userInputMorse);
              //  System.out.println("!!!!!!!add __spce__");
                morseOutput.append(" ");
                recordUserSymbol(' ');
              //  System.out.println(userInputMorse);
            }

            morseOutput.append(s);
            lastInputTime = now;

            // auto clear after some duration time
            userHandler.removeCallbacks(clearRunnable);
            if (clearRunnable == null) {
                clearRunnable = () -> {
                    synchronized (morseLock) {
                        morseOutput.setText("");
                        lastInputTime = 0;
                        clearRunnable = null;
                    }
                };
            }
            userHandler.postDelayed(clearRunnable, userDelay);
        }
    }

    // Display the entered Morse code on the screen
    private void appendMorseOutText(String s) {
        if (!isKeyPracticeActive) return;

        synchronized (morseLock) {
            String currentText = morseOutput.getText().toString();
            long now = System.currentTimeMillis(); // add: current time


            //MorseOut
            if (!currentText.isEmpty() && !currentText.endsWith(" ")) {
                morseOutput.append(" "); // space for between symbol
            }

            // add space for user input to exceed 500sec
            if (lastInputTime > 0 && now - lastInputTime > 500) {

                morseOutput.append(" ");
                recordUserSymbol(' ');
            }

            morseOutput.append(s);
            lastInputTime = now;

            // Auto clear the text after a certain amount of time
            userHandler.removeCallbacks(clearRunnable);
            if (clearRunnable == null) {
                clearRunnable = () -> {
                    synchronized (morseLock) {
                        morseOutput.setText("");
                        lastInputTime = 0;
                        clearRunnable = null;
                    }
                };
            }
            userHandler.postDelayed(clearRunnable, userDelay);
        }
    }


    @SuppressLint("SetTextI18n")
    //  Check and process the Morse code entered by the user
    private void checkUserInput() {
        System.out.println("-----------------------------------------------");
        if (!word_gameView.getisKeyPracticeActive()) { // when game is over
            String score = word_gameView.getScore();
            String result;
            String[] parts = score.split("/");
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);
            double cal = (double)first/second;

            if (cal == 1) result = "Perfect!";
            else if (cal > 0.6) result = "Good!";
            else result = "No~!";

            qView.setText(result + " Score: " + score);
            morseOutput.setText("");
            return;
        }

        String targetMorse = word_gameView.getTargetMorse();

        System.out.println("user: "+userInputMorse.toString());
        System.out.println("target: "+targetMorse);

        if (userInputMorse.toString().equals(targetMorse)) {
            // play correct answer sound if the answer is correct
            AppSoundPlayer.playSegment(getApplicationContext(), R.raw.yay, 160, 1500);
            userInputMorse.setLength(0);
            morseOutput.setText("");
            word_gameView.removeCurrentLetter(); // move to the next character
        } else {
            // clear the input if incorrect
            userInputMorse.setLength(0);
            morseOutput.setText("");
            word_gameView.setAnswer(false);
        }
    }

    // Called when the Activity is completely destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppSoundPlayer.releaseAll();  // release MediaPlayer memory
    }

    private void togglePlay(int btnIndex, String morseCode) {
        if (isPlaying[btnIndex]) {
            isPlaying[btnIndex] = false;
            handler.removeCallbacks(runnables[btnIndex]);
            playButtons[btnIndex].setText("▶");
        } else {
            isPlaying[btnIndex] = true;
            currentIndex[btnIndex] = 0;
            dotDashViews[btnIndex].setText("");
            playButtons[btnIndex].setText("■");
            playSequence(btnIndex, morseCode);
        }
    }

    private void playSequence(int btnIndex, String morseCode) {
        // Delay between symbols (dots/dashes)
        int interSymbolDelay = MorseSoundPlayer.getInterSymbolNOSoundDelay();
        // Delay between letters
        int interLetterDelay = MorseSoundPlayer.getInterLetterNOSoundDelay();

        // Create a Runnable for each button
        runnables[btnIndex] = new Runnable() {
            // Sound repetition count
            int repeatCountSound = 0;
            final int maxRepeatSound = 3;  // Maximum of 3 sound repetitions
            // Text display repetition count
            int repeatCountText = 0;
            final int maxRepeatText = 1;   // Display text only once

            @Override
            public void run() {
                // Exit if the button is not playing
                if (!isPlaying[btnIndex]) return;

                // Exit if the repetition count reaches the maximum
                if (repeatCountSound >= maxRepeatSound) {
                    isPlaying[btnIndex] = false;          // Set playing state to false
                    handler.removeCallbacks(this);         // Remove Runnable
                    playButtons[btnIndex].setText("▶");   // Reset button text
                    return;
                }

                // Get the Morse code character at the current index
                char c = morseCode.charAt(currentIndex[btnIndex]);
                int duration;

                if (c == '.') {
                    duration = MorseSoundPlayer.getDotSoundDuration();
                    MorseSoundPlayer.playTone(duration);
                } else if (c == '-') {
                    duration = MorseSoundPlayer.getDashSoundDuration();
                    MorseSoundPlayer.playTone(duration);
                } else if (c == ' ') {
                    // No sound for a space, delay between letters
                    duration = 0;
                } else {
                    // Handle unexpected characters
                    duration = MorseSoundPlayer.getDotSoundDuration();
                    MorseSoundPlayer.playTone(duration);
                }
                // Display text on the screen (only once)
                if (repeatCountText < maxRepeatText) {
                    if (dotDashViews[btnIndex].getText().length() > 0) {
                        dotDashViews[btnIndex].append(" "); // Add a space between symbols
                    }
                    dotDashViews[btnIndex].append(String.valueOf(c)); // Add the symbol
                }

                // Move to the next symbol
                currentIndex[btnIndex]++;
                int delay;

                // If the end of the letter is reached
                if (currentIndex[btnIndex] >= morseCode.length()) {
                    currentIndex[btnIndex] = 0; // Reset index
                    repeatCountSound++;          // Increment sound repetition count
                    if (repeatCountText < maxRepeatText) {  // maxRepeatText=3
                        dotDashViews[btnIndex].append("  "); // Add spaces to indicate the end of the letter
                        repeatCountText++;                   // Increment text repetition count
                    }
                    delay = duration + interLetterDelay; // Delay between letters
                } else {
                    // interLetterDelay for a space, otherwise interSymbolDelay
                    delay = (c == ' ') ? interLetterDelay : duration + interSymbolDelay;
                }

                // Execute the Runnable again after a delay (recursive call)
                handler.postDelayed(this, delay);
            }
        };

        // Run the Runnable for the first time
        handler.post(runnables[btnIndex]);
    }
}
