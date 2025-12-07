package com.example.hacknation_bydgo;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MAP_FILE_NAME = "bydgoszcz.map";

    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;

    private boolean menuOpen = false;

    private ImageButton btnMenu, btnOne, btnTwo;
    private ImageButton btnMap, btnShutter;

    private Point[] points;

    private Camera camera; // <-- Dodane

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Point[] defaultPoints = PointsRepository.getAllPoints();
        points = PointsStorage.loadPoints(this, defaultPoints);
        PointsStorage.savePoints(this, points);

        btnMenu   = findViewById(R.id.btnMenu);
        btnOne    = findViewById(R.id.btnOne);
        btnTwo    = findViewById(R.id.btnTwo);
        btnMap    = findViewById(R.id.btnMap);
        btnShutter= findViewById(R.id.btnShutter);

        List<ImageButton> extraButtons = Arrays.asList(btnOne, btnTwo);

        btnMenu.setOnClickListener(v -> {
            if (menuOpen) collapseMenu(extraButtons);
            else expandMenu(extraButtons);
        });
        btnOne.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Location.class)));
        btnTwo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Home.class)));

        // RESET BUTTON - DEV ONLY

        ImageButton btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> {
            // Reset wszystkich punktów
            getSharedPreferences("points_storage", MODE_PRIVATE).edit().clear().apply();

            // Przywróć domyślne punkty
            points = PointsRepository.getAllPoints();
            PointsStorage.savePoints(MainActivity.this, points);

            // Odśwież markery na mapie
            for (int i = mapView.getLayerManager().getLayers().size() - 1; i >= 0; i--) {
                if (mapView.getLayerManager().getLayers().get(i) instanceof Marker) {
                    mapView.getLayerManager().getLayers().remove(i);
                }
            }

            Drawable drawableUndiscovered = ContextCompat.getDrawable(this, R.drawable.marker_undiscovered);
            Bitmap bitmapUndiscovered = AndroidGraphicFactory.convertToBitmap(drawableUndiscovered);
            bitmapUndiscovered.incrementRefCount();

            Drawable drawableDiscovered = ContextCompat.getDrawable(this, R.drawable.marker_discovered);
            Bitmap bitmapDiscovered = AndroidGraphicFactory.convertToBitmap(drawableDiscovered);
            bitmapDiscovered.incrementRefCount();

            for (Point p : points) {
                LatLong latLong = new LatLong(p.getLat(), p.getLon());
                Bitmap bmp = p.isVisited() ? bitmapDiscovered : bitmapUndiscovered;
                Marker marker = new Marker(latLong, bmp, 0, -bmp.getHeight() / 2);
                mapView.getLayerManager().getLayers().add(marker);
            }

            Toast.makeText(MainActivity.this, "Wszystkie punkty zresetowane", Toast.LENGTH_SHORT).show();
        });

        // RESET BUTTON END

        // --- Kamera ---
        btnShutter.setOnClickListener(v -> camera.takePhoto());

        camera = new Camera(this, bitmap -> {
            points = PointsStorage.loadPoints(MainActivity.this, PointsRepository.getAllPoints());

            // Usuń wszystkie stare markery
            for (int i = mapView.getLayerManager().getLayers().size() - 1; i >= 0; i--) {
                if (mapView.getLayerManager().getLayers().get(i) instanceof Marker) {
                    mapView.getLayerManager().getLayers().remove(i);
                }
            }

            Drawable drawableUndiscovered = ContextCompat.getDrawable(this, R.drawable.marker_undiscovered);
            Bitmap bitmapUndiscovered = AndroidGraphicFactory.convertToBitmap(drawableUndiscovered);
            bitmapUndiscovered.incrementRefCount();

            Drawable drawableDiscovered = ContextCompat.getDrawable(this, R.drawable.marker_discovered);
            Bitmap bitmapDiscovered = AndroidGraphicFactory.convertToBitmap(drawableDiscovered);
            bitmapDiscovered.incrementRefCount();

            for (Point p : points) {
                LatLong latLong = new LatLong(p.getLat(), p.getLon());
                Bitmap bmp = p.isVisited() ? bitmapDiscovered : bitmapUndiscovered;
                Marker marker = new Marker(latLong, bmp, 0, -bmp.getHeight() / 2);
                mapView.getLayerManager().getLayers().add(marker);
            }

            // --- Toast ---
            if (camera.wasUnlocked()) {
                Toast.makeText(MainActivity.this, "Odwiedzono: " +
                        points[camera.getLastPredictedId()].getName(), Toast.LENGTH_LONG).show();
            } else if (camera.getLastPredictedScore() > 30) {
                Toast.makeText(MainActivity.this, points[camera.getLastPredictedId()].getName() + " już odwiedzone",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Spróbuj jeszcze raz", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Map setup ---
        AndroidGraphicFactory.createInstance(getApplication());
        mapView = findViewById(R.id.mapView);

        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        tileCache = AndroidUtil.createTileCache(
                this,
                "mapcache",
                mapView.getModel().displayModel.getTileSize(),
                1.0f,
                mapView.getModel().frameBufferModel.getOverdrawFactor()
        );

        mapView.getModel().mapViewPosition.setCenter(new LatLong(53.1235, 18.0084));
        mapView.getModel().mapViewPosition.setZoomLevel((byte) 15);

        File mapFile;
        try {
            mapFile = getMapFileFromRaw();
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare map file", e);
        }

        MapDataStore mapDataStore = new MapFile(mapFile);
        GraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;

        tileRendererLayer = new TileRendererLayer(
                tileCache,
                mapDataStore,
                mapView.getModel().mapViewPosition,
                false,
                true,
                true,
                graphicFactory
        );

        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        Drawable drawableUndiscovered = ContextCompat.getDrawable(this, R.drawable.marker_undiscovered);
        Bitmap bitmapUndiscovered = AndroidGraphicFactory.convertToBitmap(drawableUndiscovered);
        bitmapUndiscovered.incrementRefCount();

        Drawable drawableDiscovered = ContextCompat.getDrawable(this, R.drawable.marker_discovered);
        Bitmap bitmapDiscovered = AndroidGraphicFactory.convertToBitmap(drawableDiscovered);
        bitmapDiscovered.incrementRefCount();

        for (Point p : points) {
            LatLong latLong = new LatLong(p.getLat(), p.getLon());
            Bitmap bmp = p.isVisited() ? bitmapDiscovered : bitmapUndiscovered;

            Marker marker = new Marker(latLong, bmp, 0, -bmp.getHeight() / 2);
            mapView.getLayerManager().getLayers().add(marker);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tileRendererLayer != null) tileRendererLayer.onDestroy();
        if (tileCache != null) tileCache.destroy();
        if (mapView != null) mapView.destroy();
        AndroidGraphicFactory.clearResourceMemoryCache();
    }

    private File getMapFileFromRaw() throws IOException {
        File mapsDir = new File(getFilesDir(), "maps");
        if (!mapsDir.exists()) mapsDir.mkdirs();
        File outFile = new File(mapsDir, MAP_FILE_NAME);
        if (!outFile.exists()) {
            InputStream in = getResources().openRawResource(R.raw.bydgoszcz);
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            in.close();
            out.close();
        }
        return outFile;
    }

    private void expandMenu(List<ImageButton> buttons) {
        menuOpen = true;
        for (int i = 0; i < buttons.size(); i++) {
            ImageButton b = buttons.get(i);
            b.setVisibility(View.VISIBLE);
            b.setAlpha(0f);
            b.setTranslationY(-40f);
            b.animate().alpha(1f).translationY(0f).setStartDelay(i * 40L).setDuration(160L).start();
        }
    }

    private void collapseMenu(List<ImageButton> buttons) {
        menuOpen = false;
        for (int i = buttons.size() - 1, order = 0; i >= 0; i--, order++) {
            final ImageButton b = buttons.get(i);
            b.animate().alpha(0f).translationY(-40f).setStartDelay(order * 40L).setDuration(120L)
                    .withEndAction(() -> b.setVisibility(View.GONE)).start();
        }
    }
}
