package com.example.hacknation_bydgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Klasa obsługująca aparat. Umożliwia robienie zdjęć i otrzymywanie callbacka z Bitmapą.
 */
public class Camera {

    public interface CameraCallback {
        void onPhotoTaken(Bitmap bitmap);
    }

    private final AppCompatActivity activity;
    private final CameraCallback callback;
    private Uri photoUri;
    private final ActivityResultLauncher<Uri> takePictureLauncher;

    public Camera(AppCompatActivity activity, CameraCallback callback) {
        this.activity = activity;
        this.callback = callback;

        // Rejestracja launcher'a
        takePictureLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && photoUri != null) {
                        handleCapturedPhoto();
                    }
                }
        );
    }

    /**
     * Wywołanie aparatu
     */
    public void takePhoto() {
        try {
            // Tworzymy unikalną nazwę pliku
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File storageDir = activity.getExternalFilesDir(null);
            File photoFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);

            // Pobranie URI za pomocą FileProvider
            photoUri = FileProvider.getUriForFile(
                    activity,
                    activity.getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile
            );

            // Uruchomienie aparatu
            takePictureLauncher.launch(photoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obsługa zdjęcia po wykonaniu
     */
    private void handleCapturedPhoto() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(photoUri));
            if (bitmap != null && callback != null) {
                callback.onPhotoTaken(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
