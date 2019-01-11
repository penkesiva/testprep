package com.ee.testprep.util;

import android.graphics.Color;

import java.util.Random;

public class RandomColor {
    private static String[] mColors = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#0174DF",
            "#0080FF",
            "#303F9F",
            "#FF4081",
            "#232F34",
            "#344955",
            "#2D2D2D",
            "#1C262A",
            "#FE9A2E",
            "#5FB404",
            "#FF0000",
    };

    public int getRandomColor() {
        Random randomGenerator = new Random(); // Construct a new Random number generator
        int rand = randomGenerator.nextInt(mColors.length);
        return Color.parseColor(mColors[rand]);
    }
}
