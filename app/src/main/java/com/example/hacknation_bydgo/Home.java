package com.example.hacknation_bydgo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // korzystamy z XML

        // Przycisk Powrót
        TextView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Tutaj można opcjonalnie ustawić dynamiczną treść,
        // jeśli np. lista autorów lub drużyny ma pochodzić z repozytorium.
        TextView authorsList = findViewById(R.id.homeAuthorsList);
        authorsList.setText(
                "- Jan Kowalski\n" +
                        "- Anna Nowak\n" +
                        "- Piotr Wiśniewski"
        );

        TextView teamInfo = findViewById(R.id.homeTeamInfo);
        teamInfo.setText(
                "Specjaliści od map i GIS\n" +
                        "Programiści Android\n" +
                        "Projektanci UX/UI\n\n" +
                        "Projekt ma na celu edukację oraz promocję zabytków i miejsc w Bydgoszczy."
        );
    }
}
