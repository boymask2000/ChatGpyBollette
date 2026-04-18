package com.boymask.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.util.ArrayList;
import java.util.List;

public class PdfToImage {

    public static List<Bitmap> render(Context context, Uri uri) throws Exception {

        ParcelFileDescriptor pfd =
                context.getContentResolver().openFileDescriptor(uri, "r");

        PdfRenderer renderer = new PdfRenderer(pfd);

        List<Bitmap> pages = new ArrayList<>();

        for (int i = 0; i < renderer.getPageCount(); i++) {

            PdfRenderer.Page page = renderer.openPage(i);

            Bitmap bitmap = Bitmap.createBitmap(
                    page.getWidth(),
                    page.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            pages.add(bitmap);
            page.close();
        }

        renderer.close();
        pfd.close();

        return pages;
    }
}