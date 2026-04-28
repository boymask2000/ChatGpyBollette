package com.boymask.myapplication;

import android.os.Bundle;

import com.boymask.ImageAdapter;

import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class FotoTakerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private List<File> photoList = new ArrayList<>();
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_taker);

        previewView = findViewById(R.id.previewView);
        Button btnCapture = findViewById(R.id.btnCapture);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(photoList);
        recyclerView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            startCamera();
        }

        btnCapture.setOnClickListener(v -> takePhoto());


        Button esci = findViewById(R.id.esci);
        esci.setOnClickListener(v -> {

            FotoTakerActivity.this.finish();

        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(
                getExternalFilesDir(null),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        .format(new Date()) + ".jpg"
        );

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                options,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults output) {

                        photoList.add(photoFile);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(
                            @NonNull ImageCaptureException exception) {
                        Log.e("CAMERA", "Errore: " + exception.getMessage());
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }
}