package com.appspresso.waikiki.deviceinteraction;

import java.util.ArrayList;
import java.util.List;
import com.appspresso.api.AxError;
import android.content.Context;

public class Vibrator {
    public static final int VIBRATE_LENGTH = 10;
    public static final char PATTERN_VIBRATE_ON = '.';
    public static final char PATTERN_VIBRATE_OFF = '_';
    public static final int TIME_VIBRATE = 100;

    private static android.os.Vibrator vibrator;

    public static void init(Context environment) {
        vibrator = (android.os.Vibrator) environment.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void startVibrate(long duration) {
        vibrator.vibrate(duration);
    }

    public static void startVibrate(long[] pattern) {
        vibrator.vibrate(pattern, -1);
    }

    public static void startVibrateRepeat(long[] pattern) {
        vibrator.vibrate(pattern, 0);
    }

    public static void stopVibrate() {
        vibrator.cancel();
    }

    public static class Pattern {
        private long delay;
        private long[] initialPattern;
        private long[] pattern;

        public Pattern(String stringPattern) throws AxError {
            convertPattern(stringPattern);
        }

        public long getDelay() {
            return delay;
        }

        public long[] getInitailPattern() {
            return initialPattern;
        }

        public long[] getPattern() {
            return pattern;
        }

        /**
         * @param pattern
         * @throws AxError
         */
        private void convertPattern(String stringPattern) throws AxError {
            AxError verityError = verifyPattern(stringPattern);
            if (verityError != null) throw verityError;

            List<Long> list = new ArrayList<Long>(10);
            int length = stringPattern.length();

            char checkChar = stringPattern.charAt(0);
            if (checkChar == PATTERN_VIBRATE_ON) list.add(0L);
            long duration = TIME_VIBRATE;

            for (int index = 1; index < length; index++) {
                if (stringPattern.charAt(index) == checkChar) {
                    duration += TIME_VIBRATE;
                }
                else {
                    checkChar =
                            (checkChar == PATTERN_VIBRATE_ON)
                                    ? PATTERN_VIBRATE_OFF
                                    : PATTERN_VIBRATE_ON;
                    list.add(new Long(duration));
                    duration = TIME_VIBRATE;
                }
            }

            list.add(duration);
            length = list.size();

            if ((length % 2) == 1) {
                length = length - 1;
                initialPattern = new long[length];
                pattern = new long[length];
                for (int i = 0; i < length; i++) {
                    initialPattern[i] = list.get(i);
                    pattern[i] = initialPattern[i];
                    delay += initialPattern[i];
                }
                pattern[0] += list.get(length);
            }
            else {
                pattern = new long[length];
                for (int i = 0; i < length; i++) {
                    pattern[i] = list.get(i);
                }
                initialPattern = pattern;
            }
        }

        private AxError verifyPattern(String pattern) {
            if (null == pattern) { return new AxError(AxError.INVALID_VALUES_ERR,
                    "Parameter pattern is mandatory."); }

            if (pattern.length() == 0) { return new AxError(AxError.INVALID_VALUES_ERR,
                    "Pattern length is 0."); }

            int length = pattern.length();
            if (length > VIBRATE_LENGTH) { return new AxError(AxError.INVALID_VALUES_ERR,
                    "Pattern is longer than 10 characters."); }

            for (int i = 0; i < length; i++) {
                if (PATTERN_VIBRATE_ON == pattern.charAt(i)
                        || PATTERN_VIBRATE_OFF == pattern.charAt(i)) continue;
                return new AxError(AxError.INVALID_VALUES_ERR,
                        "Pattern must be composed by '.' and '_'.");
            }

            return null;
        }
    }
}
