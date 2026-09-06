# MorseStriker

Morse Code Practice & Game Android App
**MorseStriker is an Android app that teaches and practices Morse Code through levels and a game-based practice system.**

## Why I Started This Project

I first learned about Morse Code in middle school and started studying it little by little. By 9th grade, I had learned it well enough to use it.

In 10th grade, I took AP Java. During the summer, I started building a Morse Code practice app using the Java I learned in class. Around the same time, I also earned my FCC Amateur Radio Technician Class License.

I used several Morse Code learning apps as part of my market research, but I had a hard time finding one that used game elements in a fun way. So I decided to build my own.

## How It Works

I divided Morse Code practice into **Letter → Word → Sentence**, with a game after each learning stage.

### 1. Letter

Learn and practice the Morse Code for individual letters.

| Letter | Morse Code |
| ------ | ---------- |
| A      | .-         |
| B      | -...       |

After learning, the Letter Game lets you practice the letters.

### 2. Word

Learn short words and basic Morse Code expressions used in Amateur Radio.

| Word | Morse Code |
| ---- | ---------- |
| HI   | .... ..    |
| TNX  | - -. -..-  |

After learning, the Word Game lets you practice the words.

### 3. Sentence

The same idea is used for sentences, followed by the Sentence Game.

## Game

* Letters, words, or sentences fall from the top of the screen.
* Enter Morse Code using the Dot (`.`) and Dash (`-`) buttons.
* **You have to enter the correct answer before the letter, word, or sentence reaches the bottom of the screen. Correct answers increase the score.**
* A "Yay!" cheering sound plays when you get the answer right.
* The final score is shown when the game ends.

## Audio

I created the Morse Code sounds directly in the app.

* Different lengths for Dots and Dashes
* Consistent timing between Morse Code signals
* Morse Code can be played directly on the learning screens
* A sound effect plays for correct answers during the game

## Technologies

* Java
* Android Studio
* Android SDK
* XML
* `AudioTrack`
* `MediaPlayer`

## Source Code

| File                            | Description                          |
| ------------------------------- | ------------------------------------ |
| `LetterPracticeActivity.java`   | Letter learning and Letter Game      |
| `WordPracticeActivity.java`     | Word learning and Word Game          |
| `SentencePracticeActivity.java` | Sentence learning and Sentence Game  |
| `KeyPracticeGame.java`          | Falling game logic                   |
| `PracticeData.java`             | Morse Code learning data             |
| `MorseSoundPlayer.java`         | Generates and plays Morse Code audio |
| `AppSoundPlayer.java`           | Plays game sound effects             |
| `ModeSelectActivity.java`       | Selects the learning stage           |
| `BaseActivity.java`             | Common screen setup                  |

## What I Learned

Through this project, I applied what I learned in AP Java to a real Android app. I used Android Activities to build the screens, handled user input, created Morse Code sounds directly with AudioTrack, and implemented the falling game and scoring system.

I also worked on setting the actual timing of Morse Code sounds and reducing input delays to make the Dot and Dash buttons more responsive. I spent a lot of time on the UI and kept adjusting the layout and colors so that even users who did not know Morse Code could understand and use the app easily.

The app was not technically very complex, but I learned that designing something from the user's perspective and turning a working program into a real product was much harder than I expected.
