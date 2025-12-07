package com.example.hacknation_bydgo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;


public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/k9fuzq6y_expires_30_days.png").into((ImageView) findViewById(R.id.r076nmpbm39kx));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/44583817_expires_30_days.png").into((ImageView) findViewById(R.id.rantd2h2po38));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/otydbff8_expires_30_days.png").into((ImageView) findViewById(R.id.rszvuxjkmw7m));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/ti3dmwrn_expires_30_days.png").into((ImageView) findViewById(R.id.rifzu5rs6oll));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/nvmceqtm_expires_30_days.png").into((ImageView) findViewById(R.id.r4067jcf1uzx));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/4o0pjl61_expires_30_days.png").into((ImageView) findViewById(R.id.r9jyjsn1l9kk));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/dlwe0c6s_expires_30_days.png").into((ImageView) findViewById(R.id.rxgve41w2pyd));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/3teks4qr_expires_30_days.png").into((ImageView) findViewById(R.id.reeyywwbhbc));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/ahi24hwi_expires_30_days.png").into((ImageView) findViewById(R.id.rmvjtii9pphs));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/jmbg2vqp_expires_30_days.png").into((ImageView) findViewById(R.id.rhgkgml40bom));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/keh4hdq9_expires_30_days.png").into((ImageView) findViewById(R.id.rmhvpxkkkr9));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/mlifvwdf_expires_30_days.png").into((ImageView) findViewById(R.id.rat2obyifxfr));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/sm9n4p6f_expires_30_days.png").into((ImageView) findViewById(R.id.r6egorixfhcu));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/wis8mpwl_expires_30_days.png").into((ImageView) findViewById(R.id.r2le0bhoujne));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/pr09vehg_expires_30_days.png").into((ImageView) findViewById(R.id.rsxn8lloshmd));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/g3fqswci_expires_30_days.png").into((ImageView) findViewById(R.id.regir514bi1a));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/n2ofh3rd_expires_30_days.png").into((ImageView) findViewById(R.id.rbnbe9erom4e));
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/sPZbCRHwCa/lhx1bvth_expires_30_days.png").into((ImageView) findViewById(R.id.rbk3pufu7agu));

        View button1 = findViewById(R.id.rj5addjb16de);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
            }
        });

        // --- OBSŁUGA RESETU ---
        Button btnReset = findViewById(R.id.btnResetProfile);
        btnReset.setOnClickListener(v -> showResetConfirmationDialog());

    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset postępów")
                .setMessage("Czy na pewno chcesz usunąć wszystkie odwiedzone miejsca? Tej operacji nie można cofnąć.")
                .setPositiveButton("Resetuj", (dialog, which) -> {
                    performReset();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void performReset() {
        // 1. Wyczyść SharedPreferences
        getSharedPreferences("points_storage", MODE_PRIVATE).edit().clear().apply();

        // 2. Przywróć domyślne punkty (wszystkie jako nieodwiedzone)
        Point[] defaultPoints = PointsRepository.getAllPoints();
        PointsStorage.savePoints(this, defaultPoints);

        // 3. Informacja dla użytkownika
        Toast.makeText(this, "Postępy zostały zresetowane", Toast.LENGTH_SHORT).show();

        // 4. Odśwież ekran profilu (przeładowanie aktywności)
        recreate();
    }

}
