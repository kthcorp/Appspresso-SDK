package com.appspresso.waikiki.filesystem.utils;

public class DataConverter {
    public static int convertToInteger(long longValue) {
        return (int) longValue;
    }

    public static byte[] convertToByteArray(Number[] numberArray) {
        int length = numberArray.length;
        byte[] byteArray = new byte[length];
        for (int i = 0; i < length; i++) {
            byteArray[i] = numberArray[i].byteValue();
        }
        return byteArray;
    }

    public static int[] convertToIntegerArray(byte[] byteArray) {
        int length = byteArray.length;
        int[] intArray = new int[length];
        for (int i = 0; i < length; i++) {
            intArray[i] = (int) byteArray[i] & 0xFF;
        }

        return intArray;
    }
}
