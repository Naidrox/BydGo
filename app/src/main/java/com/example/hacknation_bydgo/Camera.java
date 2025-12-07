package com.example.hacknation_bydgo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Camera {

    public interface CameraCallback {
        void onPhotoTaken(Bitmap bitmap);
    }

    private final AppCompatActivity activity;
    private final CameraCallback callback;
    private Uri photoUri;
    private final ActivityResultLauncher<Uri> takePictureLauncher;

    private int lastPredictedId = -1;
    private float lastPredictedScore = 0;
    private boolean wasUnlocked = false; // <-- nowa flaga

    public int getLastPredictedId() { return lastPredictedId; }
    public float getLastPredictedScore() { return lastPredictedScore; }
    public boolean wasUnlocked() { return wasUnlocked; }

    public Camera(AppCompatActivity activity, CameraCallback callback) {
        this.activity = activity;
        this.callback = callback;

        takePictureLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && photoUri != null) {
                        handleCapturedPhoto();
                    }
                }
        );
    }

    public void takePhoto() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File storageDir = activity.getExternalFilesDir(null);
            File photoFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);

            photoUri = FileProvider.getUriForFile(
                    activity,
                    activity.getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile
            );

            takePictureLauncher.launch(photoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCapturedPhoto() {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(photoUri));

            int targetWidth = 101;
            int targetHeight = 180;
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

            Module module = Module.load(assetFilePath("model.pt"));

            float[] MEAN = {0.0f, 0.0f, 0.0f};
            float[] STD = {1.0f, 1.0f, 1.0f};

            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                    resized,
                    MEAN,
                    STD
            );

            Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
            float[] scores = outputTensor.getDataAsFloatArray();

            int maxIndex = 0;
            for (int i = 1; i < scores.length; i++) {
                if (scores[i] > scores[maxIndex]) {
                    maxIndex = i;
                }
            }

            System.out.println("Predicted class: " + maxIndex + "  Score: " + scores[maxIndex]);

            lastPredictedId = maxIndex;
            lastPredictedScore = scores[maxIndex];
            wasUnlocked = false; // reset flagi

            if (scores[maxIndex] > 3) {
                Point[] allPoints = PointsStorage.loadPoints(activity, PointsRepository.getAllPoints());

                for (Point p : allPoints) {
                    if (p.getId() == maxIndex) {
                        if (!p.isVisited()) {
                            p.setVisited(true);
                            wasUnlocked = true; // <-- punkt został odblokowany teraz
                        }
                        break;
                    }
                }

                PointsStorage.savePoints(activity, allPoints);
            }

            if (callback != null) callback.onPhotoTaken(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String assetFilePath(String assetName) throws IOException {
        File file = new File(activity.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) return file.getAbsolutePath();

        try (InputStream is = activity.getAssets().open(assetName);
             OutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
        return file.getAbsolutePath();
    }
}
