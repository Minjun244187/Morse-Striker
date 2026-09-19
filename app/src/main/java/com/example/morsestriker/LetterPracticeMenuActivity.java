
/*

package com.example.morsestriker;

import android.view.View;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class LetterPractice extends AppCompatActivity {

    public static final String[][][] level_characters = {
            { {"E", "."}, {"T", "-"}, {"A", ".-"} },
            { {"N", "-."}, {"M", "--"}, {"I", ".."} },
            { {"O", "---"}, {"S", "..."}, {"H", "...."} },
            { {"R", ".-."}, {"D", "-.."}, {"U", "..-"} },
            { {"K", "-.-"}, {"C", "-.-."}, {"W", ".--"} },
            { {"G", "--."}, {"L", ".-.."}, {"F", "..-." }},
            { {"B", "-..."}, {"V", "...-"}, {"Y", "-.--"}},
            { {"Z", "--.."}, {"Q", "--.-"}, {"J", ".---"}},
            { {"0", "-----"}, {"1", ".----"}, {"2", "..---"}},
            { {"3", "...--"}, {"4", "....-"}, {"5", "....."}},
            { {"6", "-...."}, {"7", "--..."}, {"8", "---.."}},
            { {"9", "----."} },
            { {"A", ".-"}, {"B", "-..."} }
    };

    private static final int catagory = 1;

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
    private TextView[] letterViews = new TextView[3];
    private Button[] playButtons = new Button[3];

    // Level characters


    private TextView morseOutput;
    private TextView qView;
    private Button keyPracticeButton;

    private ArrayList<String> keyPracticeList = new ArrayList<>();
    private int keyPracticeIndex = 0;
    private int userDelay = 1500;
    private Handler userHandler = new Handler();
    private final Object morseLock = new Object();
    private Runnable clearRunnable = null;
    private ToneGenerator ding = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    // Key Practice input
    private StringBuilder userInputMorse = new StringBuilder();
    private int currentLevel;
    // checkUserInput Runnable 정의
    private final Runnable checkUserInputRunnable = this::checkUserInput;

    private KeyPracticeGame letter_gameView;
    private Button keyPracticeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_practice);

        // Initialize views
        dotDashViews = new TextView[] {
                findViewById(R.id.dot_dash1),
                findViewById(R.id.dot_dash2),
                findViewById(R.id.dot_dash3)
        };

        letterViews = new TextView[] {
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
        qView = findViewById(R.id.qView);
        //text game
       // keyPracticeButton = findViewById(R.id.keyPracticeButton);
        // 기존 UI 초기화
        //text falling game
        letter_gameView = findViewById(R.id.gameArea);
        keyPracticeBtn = findViewById(R.id.keyPracticeButton);

        // Key Practice 버튼 클릭 시 게임 시작
        keyPracticeBtn.setOnClickListener(v -> {
            makeKeyPracticelist(currentLevel);
            letter_gameView.setCharList(keyPracticeList, currentLevel);
            letter_gameView.startGame();
            //startKeyPractice(currentLevel);
            isKeyPracticeActive = true;
        });


        // Dot key
        dot_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressingDot = true;
                    playDotWithDelay(); // 첫 음도 여기서 처리
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPressingDot = false;
                    // checkUserInput만 제거
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, 1500);
                    return true;
            }
            return false;
        });

// Dash key
        dash_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressingDash = true;
                    playDashWithDelay(); // 첫 음도 여기서 처리
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPressingDash = false;
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, 1500);
                    return true;
            }
            return false;
        });



        // Setup letters and buttons based on level
        for (int i = 0; i < dotDashViews.length; i++) {
            if (i < level_characters[level].length) {
                dotDashViews[i].setVisibility(View.VISIBLE);
                dotDashViews[i].setText(""); // clear dot/dash
                letterViews[i].setVisibility(View.VISIBLE);
                letterViews[i].setText(level_characters[level][i][0]); // show letter immediately
                playButtons[i].setVisibility(View.VISIBLE);

                int index = i;
                String letter = level_characters[level][i][0];
                String morse = level_characters[level][i][1];
                playButtons[i].setOnClickListener(v -> togglePlay(index, letter, morse));
            } else {
                dotDashViews[i].setVisibility(View.GONE);
                letterViews[i].setVisibility(View.GONE);
                playButtons[i].setVisibility(View.GONE);
            }
        }

        // Key Practice button

        keyPracticeButton.setOnClickListener(v -> {
            startKeyPractice(currentLevel);
            isKeyPracticeActive = true;
        });

    }
    private void recordUserSymbol(char c) {
        if (isKeyPracticeActive) {
            userInputMorse.append(c); // 정답 비교용 버퍼만 갱신 (공백 없음)
        }
    }
    // Dot/Dash sound with repeated delay
    private void playDotWithDelay() {
        if (!isPressingDot) return;
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDotSoundDuration());

        if (isKeyPracticeActive) {
            appendMorseOutText(".");
            recordUserSymbol('.');
        }
        handler.postDelayed(this::playDotWithDelay, MorseSoundPlayer.getDotSoundDuration()+MorseSoundPlayer.getInterSymbolNOSoundDelay());
    }

    private void playDashWithDelay() {
        if (!isPressingDash) return;
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDashSoundDuration());

        // appendMorseOutText("-");
        if (isKeyPracticeActive) {
            appendMorseOutText("-");
            recordUserSymbol('-');
        }
        handler.postDelayed(this::playDashWithDelay, MorseSoundPlayer.getDashSoundDuration()+MorseSoundPlayer.getInterSymbolNOSoundDelay());
    }

    private void appendMorseOutText(String s) {
        if (!isKeyPracticeActive) return;

        synchronized (morseLock) {
            String currentText = morseOutput.getText().toString();

            // 마지막 문자가 이미 공백이면 추가하지 않음
            if (!currentText.isEmpty() && !currentText.endsWith(" ")) {
                morseOutput.append(" ");
                //System.out.println("empty");
            }
            morseOutput.append(s);

            // 1.5초 뒤 자동 클리어 예약
            userHandler.removeCallbacks(clearRunnable);
            if (clearRunnable == null) {
                clearRunnable = () -> {
                    synchronized (morseLock) {
                        morseOutput.setText("");
                        clearRunnable = null;
                    }
                };
            }
            userHandler.postDelayed(clearRunnable, userDelay);
        }
    }

    // key game
    private void makeKeyPracticelist(int level) {
        keyPracticeList.clear();
        int len = level_characters[level].length;
        for (int i = 0; i < len * 2; i++) {
            int idx = (int) (Math.random() * len);
            keyPracticeList.add(level_characters[level][idx][0]);
        }
        keyPracticeIndex = 0;
        userInputMorse.setLength(0);
        showNextKeyPracticeLetter();
    }


    // Key Practice
    private void startKeyPractice(int level) {
        keyPracticeList.clear();
        int len = level_characters[level].length;
        for (int i = 0; i < len * 2; i++) {
            int idx = (int) (Math.random() * len);
            keyPracticeList.add(level_characters[level][idx][0]);
        }
        keyPracticeIndex = 0;
        userInputMorse.setLength(0);
        showNextKeyPracticeLetter();
    }

    private void showNextKeyPracticeLetter() {
        if (keyPracticeIndex >= keyPracticeList.size()) {
            qView.setText("Done!");
            keyPracticeButton.setText("Practice Again");
            morseOutput.setText("");
            isKeyPracticeActive = false;
            return;
        }
        qView.setText(keyPracticeList.get(keyPracticeIndex));
    }

    private void appendUserMorse(String s) {
        userInputMorse.append(s);
        morseOutput.setText(userInputMorse.toString());
    }

    private void checkUserInput() {
        if (keyPracticeIndex >= keyPracticeList.size()) {
            qView.setText("Done!");
            morseOutput.setText("");
            return;
        }

        String targetLetter = keyPracticeList.get(keyPracticeIndex);
        String targetMorse = getMorseForLetter(currentLevel, targetLetter);

        if (userInputMorse.toString().equals(targetMorse)) {
            ding.startTone(ToneGenerator.TONE_PROP_BEEP);
            keyPracticeIndex++;
            userInputMorse.setLength(0);
            morseOutput.setText("");
            showNextKeyPracticeLetter();
        } else {
            userInputMorse.setLength(0);
            morseOutput.setText("");
        }
    }

    private String getMorseForLetter(int level, String letter) {
        for (String[] pair : level_characters[level]) {
            if (pair[0].equals(letter)) return pair[1];
        }
        return "";
    }

    private void togglePlay(int btnIndex, String letter, String morseCode) {
        if (isPlaying[btnIndex]) {
            isPlaying[btnIndex] = false;
            handler.removeCallbacks(runnables[btnIndex]);
            playButtons[btnIndex].setText("▶");
        } else {
            isPlaying[btnIndex] = true;
            currentIndex[btnIndex] = 0;
            dotDashViews[btnIndex].setText("");
            playButtons[btnIndex].setText("||");
            playSequence(btnIndex, morseCode);
        }
    }

    private void playSequence(int btnIndex, String morseCode) {
        int interSymbolDelay =  MorseSoundPlayer.getInterSymbolNOSoundDelay();//140
        int interLetterDelay =  MorseSoundPlayer.getInterLetterNOSoundDelay();//420

        runnables[btnIndex] = new Runnable() {
            int repeatCountSound = 0;
            final int maxRepeatSound = 7;
            int repeatCountText = 0;
            final int maxRepeatText = 3;

            @Override
            public void run() {
                if (!isPlaying[btnIndex]) return;
                if (repeatCountSound >= maxRepeatSound) {
                    isPlaying[btnIndex] = false;
                    handler.removeCallbacks(this);
                    playButtons[btnIndex].setText("▶");
                    return;
                }

                char c = morseCode.charAt(currentIndex[btnIndex]);
                int duration = (c == '.') ? MorseSoundPlayer.getDotSoundDuration() : MorseSoundPlayer.getDashSoundDuration();
                MorseSoundPlayer.playTone(duration);

                if (repeatCountText < maxRepeatText) { //3
                    if (dotDashViews[btnIndex].getText().length() > 0) {
                        dotDashViews[btnIndex].append(" ");
                    }
                    dotDashViews[btnIndex].append(String.valueOf(c));
                }

                currentIndex[btnIndex]++;
                int delay;
                if (currentIndex[btnIndex] >= morseCode.length()) {
                    currentIndex[btnIndex] = 0;
                    repeatCountSound++;
                    if (repeatCountText < maxRepeatText) {
                        dotDashViews[btnIndex].append("  ");
                        repeatCountText++;
                    }
                    delay = duration + interLetterDelay;
                } else {
                    delay = duration + interSymbolDelay;
                }

                handler.postDelayed(this, delay);
            }
        };
        handler.post(runnables[btnIndex]);
    }
}
*/

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

public class LetterPracticeActivity extends BaseActivity {

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
    private TextView[] letterViews = new TextView[3];
    private Button[] playButtons = new Button[3];

    private TextView morseOutput;
    private TextView qView;

    private Button keyPracticeBtn;


    private int userDelay = 1500;
    private boolean Correct = true;
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

    private KeyPracticeGame letter_gameView;
    private final int letter = 0;


    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_practice);

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

        letterViews = new TextView[] {
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
        letter_gameView = findViewById(R.id.gameArea);
        keyPracticeBtn = findViewById(R.id.keyPracticeButton);

        // Game Start when clicking the Key Practice button
        keyPracticeBtn.setOnClickListener(v -> {
            keyPracticeBtn.setText("Key Practice");
            qView.setText("");

            morseOutput.setText("");       // Initialize screen output
            userInputMorse.setLength(0);   // Initialize setting

            letter_gameView.setCharList(letter,currentLevel);
            letter_gameView.startGame();
            isKeyPracticeActive = true;
        });
        // register callback
        letter_gameView.setGameEndCallback(() -> {
            // Automatically called when all characters are used up.
            runOnUiThread(() -> {
                checkUserInput(); // Calculate score and display message
                isKeyPracticeActive = false;
                keyPracticeBtn.setText("Try Again ?");
            });
        });
        // Dot key
        dot_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressingDot = true;
                    playDotWithDelay(); // Process the first sound here as well
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPressingDot = false;
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, 1500);
                    return true;
            }
            return false;
        });

        // Dash key
        dash_key.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPressingDash = true;
                    playDashWithDelay(); // Process the first sound here as well
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isPressingDash = false;
                    userHandler.removeCallbacks(checkUserInputRunnable);
                    if (isKeyPracticeActive) userHandler.postDelayed(checkUserInputRunnable, 800);
                    return true;
            }
            return false;
        });

        // Setup letters and buttons based on level
        for (int i = 0; i < dotDashViews.length; i++) {
            if (i < PracticeData.level_characters[level].length) {
                dotDashViews[i].setVisibility(View.VISIBLE);
                dotDashViews[i].setText(""); // clear dot/dash
                letterViews[i].setVisibility(View.VISIBLE);
                letterViews[i].setText(PracticeData.level_characters[level][i][0]); // show letter immediately
                playButtons[i].setVisibility(View.VISIBLE);

                int index = i;
                String letter = PracticeData.level_characters[level][i][0];
                String morse = PracticeData.level_characters[level][i][1];
                playButtons[i].setOnClickListener(v -> togglePlay(index, letter, morse));

            } else {
                dotDashViews[i].setVisibility(View.GONE);
                letterViews[i].setVisibility(View.GONE);
                playButtons[i].setVisibility(View.GONE);
            }
        }
    }

    private void recordUserSymbol(char c) {
        if (isKeyPracticeActive) {
            userInputMorse.append(c); // Update only the answer-comparison buffer (no spaces)
        }
    }

    private void playDotWithDelay() {
        if (!isPressingDot) return;
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDotSoundDuration());

        if (isKeyPracticeActive) {
            appendMorseOutText(".");
            recordUserSymbol('.');
        }
        handler.postDelayed(this::playDotWithDelay, MorseSoundPlayer.getDotSoundDuration()+MorseSoundPlayer.getInterSymbolNOSoundDelay());
    }

    private void playDashWithDelay() {
        if (!isPressingDash) return;
        MorseSoundPlayer.playTone(MorseSoundPlayer.getDashSoundDuration());

        if (isKeyPracticeActive) {
            appendMorseOutText("-");
            recordUserSymbol('-');
        }
        handler.postDelayed(this::playDashWithDelay, MorseSoundPlayer.getDashSoundDuration()+MorseSoundPlayer.getInterSymbolNOSoundDelay());
    }

    private void appendMorseOutText(String s) {
        if (!isKeyPracticeActive) return;

        synchronized (morseLock) {
            String currentText = morseOutput.getText().toString();
            if (!currentText.isEmpty() && !currentText.endsWith(" ")) {
                morseOutput.append(" ");
            }
            morseOutput.append(s);

            userHandler.removeCallbacks(clearRunnable);
            if (clearRunnable == null) {
                clearRunnable = () -> {
                    synchronized (morseLock) {
                        morseOutput.setText("");
                        clearRunnable = null;
                    }
                };
            }
            userHandler.postDelayed(clearRunnable, userDelay);
        }
    }


    @SuppressLint("SetTextI18n")
    private void checkUserInput() {
        if (!letter_gameView.getisKeyPracticeActive()) {
            String score = letter_gameView.getScore();
            String result;
            String[] parts = score.split("/");
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);
            double cal = (double)first/second;

            if (cal == 1) {
                result = "Perfect!";
            } else if (cal > 0.6 && cal < 1) {
                result = "Good!";
            } else if (cal >= 0 && cal <= 0.6) {
                result = "No~!";
            } else {
                result = "Invalid";
            }

            qView.setText(result + " Score: " + score);
            morseOutput.setText("");

            return;
        }

        String targetMorse = letter_gameView.getTargetMorse();

        if (userInputMorse.toString().equals(targetMorse)) {
            //ding.startTone(ToneGenerator.TONE_PROP_BEEP);
            AppSoundPlayer.playSegment(getApplicationContext(), R.raw.yay, 160, 1500);
            userInputMorse.setLength(0);
            morseOutput.setText("");
            letter_gameView.removeCurrentLetter();

        } else {
            userInputMorse.setLength(0);
            morseOutput.setText("");
            letter_gameView.setAnswer(Incorrect);
        }
    }

    //Called when the Activity is completely destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppSoundPlayer.releaseAll();  // release MediaPlayer memory
    }

    private void togglePlay(int btnIndex, String letter, String morseCode) {
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
        int interSymbolDelay =  MorseSoundPlayer.getInterSymbolNOSoundDelay();
        int interLetterDelay =  MorseSoundPlayer.getInterLetterNOSoundDelay();

        runnables[btnIndex] = new Runnable() {
            int repeatCountSound = 0;
            final int maxRepeatSound = 7;
            int repeatCountText = 0;
            final int maxRepeatText = 3;

            @Override
            public void run() {
                if (!isPlaying[btnIndex]) return;
                if (repeatCountSound >= maxRepeatSound) {
                    isPlaying[btnIndex] = false;
                    handler.removeCallbacks(this);
                    playButtons[btnIndex].setText("▶");
                    return;
                }

                char c = morseCode.charAt(currentIndex[btnIndex]);
                int duration = (c == '.') ? MorseSoundPlayer.getDotSoundDuration() : MorseSoundPlayer.getDashSoundDuration();
                MorseSoundPlayer.playTone(duration);

                if (repeatCountText < maxRepeatText) {
                    if (dotDashViews[btnIndex].getText().length() > 0) {
                        dotDashViews[btnIndex].append(" ");
                    }
                    dotDashViews[btnIndex].append(String.valueOf(c));
                }

                currentIndex[btnIndex]++;
                int delay;
                if (currentIndex[btnIndex] >= morseCode.length()) {
                    currentIndex[btnIndex] = 0;
                    repeatCountSound++;
                    if (repeatCountText < maxRepeatText) {
                        dotDashViews[btnIndex].append("  ");
                        repeatCountText++;
                    }
                    delay = duration + interLetterDelay;
                } else {
                    delay = duration + interSymbolDelay;
                }

                handler.postDelayed(this, delay);
            }
        };
        handler.post(runnables[btnIndex]);
    }
}

