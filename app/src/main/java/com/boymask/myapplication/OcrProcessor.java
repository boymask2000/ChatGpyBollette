package com.boymask.myapplication;

import android.graphics.Bitmap;

import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class OcrProcessor {
    private static final TextRecognizer recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    public static void extractText(Bitmap bitmap, OnTextReady callback) {

        com.google.mlkit.vision.common.InputImage image =
                com.google.mlkit.vision.common.InputImage.fromBitmap(bitmap, 0);


        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    callback.onText(result.getText());
                })
                .addOnFailureListener(e -> {
                    callback.onText("");
                });
    }

    public interface OnTextReady {
        void onText(String text);
    }
}