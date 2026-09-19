package com.example.morsestriker;

public class PracticeData {
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

    public static final String[][][] level_words = {
            { {"HI", ".... .."}, {"73", "--... ...--"}, {"TNX", "- -. -..-"} },           // Level 1
            { {"DE", "-.. ."}, {"K", "-.-"} },                                             // Level 2
            { {"R", ".-."}, {"PSE", ".--. ... ."} },                                       // Level 3
            { {"QTH", "--.- - ...."}, {"QSL", "--.- ... .-.."}, {"QSO", "--.- ... ---"} }, // Level 4
            { {"SK", "... -.-"}, {"RST", ".-. ... -"} },                                   // Level 5
            { {"BK", "-... -.-"}, {"AR", ".- .-."}, {"CQ", "-.-. --.-"} },                 // Level 6
            { {"QRM", "--.- .-. --"}, {"QRN", "--.- .-. -."}, {"QRP", "--.- .-. .--."} }, // Level 7
            { {"QRT", "--.- .-. -"}, {"QSY", "--.- ... -.--"}, {"QRZ", "--.- .-. --.."} } // Level 8
    };


    public static final String[][][] level_sentences = {
            { {"HI", ".... .."}, {"73", "--... ...--"}, {"TNX", "- -. -..-"} },
            { {"DE", "-.. ."}, {"K", "-.-"}, {"R", ".-."} },
            { {"QTH", "--.- - ...."}, {"QSL", "--.- ... .-.."}, {"QSO", "--.- ... ---"} },
            { {"RST", ".-. ... -"}, {"SK", "... -.-"}, {"PSE", ".--. ... ."} },
            { {"CQ", "-.-. --.-"}, {"AR", ".- .-."}, {"BK", "-... -.-"} },
            { {"QRM", "--.- .-. --"}, {"QRN", "--.- .-. -."}, {"QRP", "--.- .-. .--."} },
            { {"QSY", "--.- ... -.--"}, {"QRT", "--.- .-. -"}, {"QRZ", "--.- .-. --.."} },
            { {"OM", "--- --"}, {"DE", "-.. ."}, {"K", "-.-"} },
            { {"ALL TEST", ".- .-.. .-.. / - . ... -"} }
    };

    interface LevelFunction {
        String apply(int level, String letter);
    }

    public static String getMorseForLetter(int level, String letter) {
        for (String[] pair : level_characters[level]) {
            if (pair[0].equals(letter)) return pair[1];
        }
        return "";
    }

    public static String level_words(int level, String letter) {
        for (String[] pair : level_words[level]) {
            if (pair[0].equals(letter)) return pair[1];
        }
        return "";
    }

    public static String level_sentences(int level, String letter) {
        for (String[] pair : level_sentences[level]) {
            if (pair[0].equals(letter)) return pair[1];
        }
        return "";
    }

    // 함수 배열
    protected static LevelFunction[] targetdata = {
            PracticeData::getMorseForLetter,
            PracticeData::level_words,
            PracticeData::level_sentences
    };
}
