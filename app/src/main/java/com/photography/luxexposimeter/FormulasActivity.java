package com.photography.luxexposimeter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * FormulasActivity — Math & Formulas Reference
 *
 * Pagina statica che documenta le formule matematiche usate dall'app:
 * conversione lux → EV, correzione ISO, triade di esposizione,
 * quantità derivate, note di calibrazione e fonti.
 */
public class FormulasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulas);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
