package com.example.morsestriker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.media.AudioManager;
import android.media.ToneGenerator;

import java.util.ArrayList;
import java.util.Random;

public class KeyPracticeGame extends View {

    private Paint paint;
    private ArrayList<FallingLetter> fallingLetters = new ArrayList<>();
    private int currentIndex = 0;
    private ArrayList<String> practiceList = new ArrayList<>();

    //  Morse code-related
    private StringBuilder userInputMorse = new StringBuilder();
    private boolean isKeyPracticeActive = false;
    private Handler handler = new Handler();
    private Runnable gameLoop;
    private Handler userHandler = new Handler();
    private final Object morseLock = new Object();
    private Runnable clearRunnable = null;

    private ToneGenerator ding = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    private int viewWidth;
    private int viewHeight;
    private int currentLevel;
    private int category;

    private String targetMorse;
    private boolean checkAnswer = false;

    private int incorrectanswer=0;
    private final int letter = 0;
    private final int word = 1;
    private final int sentence = 2;

    public KeyPracticeGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100f);

        gameLoop = new Runnable() {
            @Override
            public void run() {
                if (!isKeyPracticeActive) return;

                // 글자가 하나도 없으면 새로 생성
                if (fallingLetters.isEmpty()) {
                    resetLetter();
                }

                if (!fallingLetters.isEmpty()) {
                    FallingLetter current = fallingLetters.get(fallingLetters.size() - 1);

                    targetMorse = PracticeData.targetdata[category].apply(currentLevel, current.letter);

                    current.update(viewHeight+100, 4f);

                    // 바닥에 닿으면 새로운 글자 추가
                    if (current.stopped) {
                        incorrectanswer++;
                        resetLetter();   // 멈춘 글자는 제거하지 않고 그대로 두고 새 글자 추가
                    }
                }

                invalidate();
                handler.postDelayed(this, 30);
            }
        };
    }

    // 레벨별 연습 문자 세팅
    public void setCharList(int category, int level) {
        this.currentLevel = level;
        this.category = category;
        makeKeyPracticelist(level);
    }

    public void startGame() {
        System.out.println("GameStart!");
        if (practiceList.isEmpty()) {
            makeKeyPracticelist(currentLevel);
        }
        isKeyPracticeActive = true;
        currentIndex = 0;
        fallingLetters.clear();
        userInputMorse.setLength(0);
        resetLetter();              // Generate the first character
        handler.removeCallbacks(gameLoop);
        handler.post(gameLoop);
    }

    public void stopGame() {
        isKeyPracticeActive = false;
        handler.removeCallbacks(gameLoop);
        userHandler.removeCallbacksAndMessages(null);
    }

    // KeyPracticeGame.java
   // 1. Define the callback interface
    public interface GameEndCallback {
        void onGameEnd();
    }

    // 2. Callback object variable
    private GameEndCallback gameEndCallback;

    // 3. Callback registration method
    public void setGameEndCallback(GameEndCallback callback) {
        this.gameEndCallback = callback;
    }


    private void resetLetter() {
        if (practiceList != null && currentIndex < practiceList.size()) {
            String letter = practiceList.get(currentIndex);
            int maxX = Math.max(viewWidth - 110, 1);
            float xPos = new Random().nextInt(maxX);
            FallingLetter fl = new FallingLetter(letter, xPos);
            fallingLetters.add(fl);
            currentIndex++;
            userInputMorse.setLength(0);
        } else {
            // End the game when all characters have fallen
            isKeyPracticeActive = false;
            handler.removeCallbacks(gameLoop);
          //  System.out.println("isKeyPracticeActive = " + isKeyPracticeActive);
          //  System.out.println("gameEndCallback = " + gameEndCallback);
            if (gameEndCallback != null) {
                gameEndCallback.onGameEnd();
          //      System.out.println("Callback!");
            }
        }
        System.out.println("GameEnd!");

        // Invoke the callback

    }

    public void removeCurrentLetter() {
        if (!fallingLetters.isEmpty()) {
            fallingLetters.remove(fallingLetters.size() - 1); // remove last one
            invalidate(); // refresh screen
        }
        createNextLetter(); // create next letter
        invalidate();       // refresh screen
    }

    // Method for creating the next letter
    private void createNextLetter() {
        if (currentIndex < practiceList.size()) {
            String letter = practiceList.get(currentIndex);
            float xPos = new Random().nextInt(Math.max(viewWidth - 100, 1));
            FallingLetter fl = new FallingLetter(letter, xPos);
            fallingLetters.add(fl);
            currentIndex++;
            userInputMorse.setLength(0);
        } else {
            isKeyPracticeActive = false; // terminate when the characters are used up
            handler.removeCallbacks(gameLoop);
            if (gameEndCallback != null) {
                gameEndCallback.onGameEnd();
            }
        }
    }

    protected void setAnswer(boolean ans){
        checkAnswer = ans;
    }
    protected boolean getisKeyPracticeActive(){
        return isKeyPracticeActive;
    }

    public void recordInput(char c) {
        if (!isKeyPracticeActive) return;
        userInputMorse.append(c);
    }

    public void appendMorseOutText(String s) {
        if (!isKeyPracticeActive) return;

        synchronized (morseLock) {
            userHandler.removeCallbacks(clearRunnable);
            if (clearRunnable == null) {
                clearRunnable = () -> {
                    synchronized (morseLock) {
                        userInputMorse.setLength(0);
                        clearRunnable = null;
                    }
                };
            }
            userHandler.postDelayed(clearRunnable, 1300);
        }
    }

    private void makeKeyPracticelist(int level) {
        practiceList.clear();
        incorrectanswer = 0;
        int len=0;

        if (category == letter){
            len = PracticeData.level_characters[level].length;
            for (int i = 0; i < len * 2; i++) {
                int idx = (int) (Math.random() * len);
                practiceList.add(PracticeData.level_characters[level][idx][0]);
            }
        }
        else if (category == word){
            len = PracticeData.level_words[level].length;
            for (int i = 0; i < len * 2; i++) {
                int idx = (int) (Math.random() * len);
                practiceList.add(PracticeData.level_words[level][idx][0]);
            }
        }
        else if((category == sentence)){
            len = PracticeData.level_sentences[level].length;
            for (int i = 0; i < len * 2; i++) {
                int idx = (int) (Math.random() * len);
                practiceList.add(PracticeData.level_sentences[level][idx][0]);
            }
        }

        currentIndex = 0;
        userInputMorse.setLength(0);
    }

    protected String getTargetMorse(){
        return targetMorse;
    }

    protected String getScore(){
        int totalq = practiceList.size();
        return (totalq-incorrectanswer +"/"+ totalq);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (FallingLetter fl : fallingLetters) {
            canvas.drawText(fl.letter, fl.x, fl.y, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
        userHandler.removeCallbacksAndMessages(null);
        ding.release();
    }

    // FallingLetter class
    private class FallingLetter {
        String letter;
        float x, y;
        boolean stopped = false;
        boolean paused = false;               // suspend
        private Handler pauseHandler = new Handler();
        private Runnable resumeRunnable;

        FallingLetter(String letter, float x) {
            this.letter = letter;
            this.x = x;
            this.y = 0;
        }

        void update(float viewHeight, float speed) {

            int pausedelay = 4000; // suspend 4 sec if the letter has 2 charaters
            // if suspend, No move
            if (stopped || paused) return;

            // suspend in the middle of screen
            if (category == word) {
                if (letter.length() > 2) pausedelay = 5000; // suspend 5 sec if the letter has 3 charaters.

                float midPoint = viewHeight / 3f;
                if (y < midPoint && y + speed >= midPoint && !paused) {
                    paused = true;
                    resumeRunnable = () -> paused = false;
                    pauseHandler.postDelayed(resumeRunnable, pausedelay);
                }
            }

            y += speed;

            if (y + 100 >= viewHeight) {
                y = viewHeight - 100;
                stopped = true;
            }
        }
    }
    }