package com.example.hacknation_bydgo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class Location extends AppCompatActivity {

    private LinearLayout locationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_location);

        locationsContainer = findViewById(R.id.locationsContainer);

        // Pobierz wszystkie punkty z repozytorium
        Point[] allPoints = PointsRepository.getAllPoints();
        // Załaduj stan visited z SharedPreferences
        Point[] points = PointsStorage.loadPoints(this, allPoints);

        // Sortowanie: odkryte u góry
        Arrays.sort(points, (a, b) -> Boolean.compare(b.isVisited(), a.isVisited()));

        for (Point p : points) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(24, 24, 24, 24);

            // Tło w zależności od stanu
            if (p.isVisited()) {
                itemLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.item_frame_discovered));
            } else {
                itemLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.item_frame_undiscovered));
            }

            // Nazwa obiektu
            TextView nameView = new TextView(this);
            nameView.setText(p.getName());
            nameView.setTextSize(18);
            nameView.setGravity(Gravity.CENTER);
            nameView.setTextColor(p.isVisited() ? Color.BLACK : Color.LTGRAY);
            nameView.setClickable(p.isVisited());

            // Opis obiektu
            TextView descView = new TextView(this);
            descView.setText(p.getDescription());
            descView.setVisibility(View.GONE);
            descView.setGravity(Gravity.CENTER);
            descView.setPadding(0, 16, 0, 0);

            // Klikanie w nazwę – otwieranie/zamykanie opisu
            nameView.setOnClickListener(v -> {
                if (!p.isVisited()) return; // <-- blokada dla nieodkrytych

                // zamykanie wszystkich innych opisów
                for (int i = 0; i < locationsContainer.getChildCount(); i++) {
                    LinearLayout child = (LinearLayout) locationsContainer.getChildAt(i);
                    TextView d = (TextView) child.getChildAt(1);
                    if (d != descView) d.setVisibility(View.GONE);
                }
                // pokaz/ukryj opis klikniętego punktu
                descView.setVisibility(descView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            });


            itemLayout.addView(nameView);
            itemLayout.addView(descView);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16); // odstęp między obiektami
            locationsContainer.addView(itemLayout, params);
        }

        // Przycisk Powrót
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }
}
