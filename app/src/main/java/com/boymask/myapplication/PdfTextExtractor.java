package com.boymask.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PdfTextExtractor {

    public static void extract(Context context, Uri uri, Callback callback) {

        new Thread(() -> {
            try {

                List<Bitmap> pages = PdfToImage.render(context, uri);
                System.out.println("PAGES: " + pages.size());
                StringBuilder fullText = new StringBuilder();

                for (Bitmap page : pages) {

                    CountDownLatch latch = new CountDownLatch(1);

                    OcrProcessor.extractText(page, text -> {
                        fullText.append(text).append("\n");
                        latch.countDown();
                    });

                    latch.await();
                }

                callback.onResult(fullText.toString());

            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult("");
            }
        }).start();
    }

    public interface Callback {
        void onResult(String text);
    }
}
