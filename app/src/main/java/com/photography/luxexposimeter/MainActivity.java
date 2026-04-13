package com.photography.luxexposimeter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * MainActivity — Convertitore Lux → Triade Esposimetrica
 *
 * L'utente inserisce:
 *   - Valore in lux (misurato con l'esposimetro)
 *   - ISO desiderato (selezionabile da spinner o seekbar)
 *   - Modalità di calcolo:
 *       A) Fisso il diaframma → calcola il tempo
 *       B) Fisso il tempo    → calcola il diaframma
 *
 * L'app mostra:
 *   - EV a ISO 100
 *   - EV corretto per l'ISO scelto
 *   - Diaframma (f-number)
 *   - Tempo di esposizione
 *   - Tabella delle combinazioni equivalenti
 *   - Descrizione della scena in base all'EV
 */

public class MainActivity extends AppCompatActivity {

    // ─── Widget ───────────────────────────────────────────────────────────────
    private EditText etLux;
    private Spinner  spinnerISO;
    private Spinner  spinnerFStop;
    private Spinner  spinnerShutter;
    private Button   btnCalcFromFStop;
    private Button   btnCalcFromShutter;

    private TextView tvEV100;
    private TextView tvEVISO;
    private TextView tvFNumber;
    private TextView tvShutterSpeed;
    private TextView tvSceneDescription;
    private LinearLayout layoutEquivalents;
    private TextView tvEquivalentsHeader;

    // ─── Dati ─────────────────────────────────────────────────────────────────
    private String[] isoLabels;
    private String[] fStopLabels;
    private String[] shutterLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        populateSpinners();
        setupListeners();
    }

    // ─── Binding ──────────────────────────────────────────────────────────────
    private void bindViews() {
        etLux               = findViewById(R.id.etLux);
        spinnerISO          = findViewById(R.id.spinnerISO);
        spinnerFStop        = findViewById(R.id.spinnerFStop);
        spinnerShutter      = findViewById(R.id.spinnerShutter);
        btnCalcFromFStop    = findViewById(R.id.btnCalcFromFStop);
        btnCalcFromShutter  = findViewById(R.id.btnCalcFromShutter);
        tvEV100             = findViewById(R.id.tvEV100);
        tvEVISO             = findViewById(R.id.tvEVISO);
        tvFNumber           = findViewById(R.id.tvFNumber);
        tvShutterSpeed      = findViewById(R.id.tvShutterSpeed);
        tvSceneDescription  = findViewById(R.id.tvSceneDescription);
        layoutEquivalents   = findViewById(R.id.layoutEquivalents);
        tvEquivalentsHeader = findViewById(R.id.tvEquivalentsHeader);
    }

    // ─── Popolamento spinner ──────────────────────────────────────────────────
    private void populateSpinners() {
        // ISO
        int[] isoValues = ExposureCalculator.STANDARD_ISO_VALUES;
        isoLabels = new String[isoValues.length];
        for (int i = 0; i < isoValues.length; i++) {
            isoLabels[i] = "ISO " + isoValues[i];
        }
        ArrayAdapter<String> isoAdapter = createWhiteTextAdapter(isoLabels);
        isoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerISO.setAdapter(isoAdapter);
        spinnerISO.setSelection(4); // ISO 100

        // F-stop
        double[] fStops = ExposureCalculator.STANDARD_F_STOPS;
        fStopLabels = new String[fStops.length];
        for (int i = 0; i < fStops.length; i++) {
            fStopLabels[i] = ExposureCalculator.formatFNumber(fStops[i]);
        }
        ArrayAdapter<String> fStopAdapter = createWhiteTextAdapter(fStopLabels);
        fStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFStop.setAdapter(fStopAdapter);
        spinnerFStop.setSelection(11); // f/5.6

        // Shutter speeds
        double[] shutters = ExposureCalculator.STANDARD_SHUTTER_SPEEDS;
        shutterLabels = new String[shutters.length];
        for (int i = 0; i < shutters.length; i++) {
            shutterLabels[i] = ExposureCalculator.formatShutterSpeed(shutters[i]);
        }
        ArrayAdapter<String> shutterAdapter = createWhiteTextAdapter(shutterLabels);
        shutterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShutter.setAdapter(shutterAdapter);
        spinnerShutter.setSelection(36); // 1/125
    }

    // ─── Listener ─────────────────────────────────────────────────────────────
    private void setupListeners() {
        btnCalcFromFStop.setOnClickListener(v -> calculateFromFStop());
        btnCalcFromShutter.setOnClickListener(v -> calculateFromShutter());
    }

    private ArrayAdapter<String> createWhiteTextAdapter(String[] items) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.text_primary));
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.text_primary));
                // Imposta lo sfondo del menu a tendina (dropdown) per renderlo coerente con il tema scuro
                text.setBackgroundColor(getResources().getColor(R.color.card_bg));
                return view;
            }
        };
    }

    // ─── Calcolo: fisso il diaframma, calcola il tempo ────────────────────────
    private void calculateFromFStop() {
        double lux = parseLux();
        if (lux < 0) return;

        int iso = getSelectedISO();
        double fNumber = ExposureCalculator.STANDARD_F_STOPS[spinnerFStop.getSelectedItemPosition()];

        try {
            ExposureCalculator.ExposureResult result =
                    ExposureCalculator.calculate(lux, iso, fNumber);
            displayResult(result, true);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ─── Calcolo: fisso il tempo, calcola il diaframma ────────────────────────
    private void calculateFromShutter() {
        double lux = parseLux();
        if (lux < 0) return;

        int iso = getSelectedISO();
        double shutterSpeed =
                ExposureCalculator.STANDARD_SHUTTER_SPEEDS[spinnerShutter.getSelectedItemPosition()];

        try {
            ExposureCalculator.ExposureResult result =
                    ExposureCalculator.calculateFromShutter(lux, iso, shutterSpeed);
            displayResult(result, false);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    // ─── Visualizzazione risultati ────────────────────────────────────────────
    private void displayResult(ExposureCalculator.ExposureResult result, boolean fixedFStop) {
        // EV a ISO 100
        tvEV100.setText(String.format(Locale.getDefault(),
                "EV₁₀₀ = %.2f", result.ev100));

        // EV corretto per ISO
        tvEVISO.setText(String.format(Locale.getDefault(),
                "EV (ISO %d) = %.2f", result.iso, result.evISO));

        // Diaframma
        if (fixedFStop) {
            tvFNumber.setText(String.format(Locale.getDefault(),
                    "Diaframma: %s  (scelto)",
                    ExposureCalculator.formatFNumber(result.fNumber)));
        } else {
            tvFNumber.setText(String.format(Locale.getDefault(),
                    "Diaframma: %s  (calcolato) → std: %s",
                    ExposureCalculator.formatFNumber(result.fNumber),
                    ExposureCalculator.formatFNumber(result.fNumberStandard)));
        }

        // Tempo di esposizione
        if (!fixedFStop) {
            tvShutterSpeed.setText(String.format(Locale.getDefault(),
                    "Tempo: %s  (scelto)",
                    ExposureCalculator.formatShutterSpeed(result.shutterSpeed)));
        } else {
            tvShutterSpeed.setText(String.format(Locale.getDefault(),
                    "Tempo: %s  (calcolato) → std: %s",
                    ExposureCalculator.formatShutterSpeed(result.shutterSpeed),
                    ExposureCalculator.formatShutterSpeed(result.shutterSpeedStandard)));
        }

        // Descrizione della scena
        tvSceneDescription.setText(describeScene(result.ev100));

        // Combinazioni equivalenti
        buildEquivalentsTable(result.evISO, result.iso);
    }

    // ─── Tabella combinazioni equivalenti ─────────────────────────────────────
    private void buildEquivalentsTable(double evISO, int iso) {
        layoutEquivalents.removeAllViews();

        List<double[]> combos = ExposureCalculator.getEquivalentCombinations(evISO);

        if (combos.isEmpty()) {
            tvEquivalentsHeader.setText("Nessuna combinazione standard nel range fotografico.");
            return;
        }

        tvEquivalentsHeader.setText(String.format(Locale.getDefault(),
                "Combinazioni equivalenti (EV %.1f, ISO %d):", evISO, iso));

        // Intestazione tabella
        LinearLayout header = makeRow(
                "Diaframma", "Tempo", "EV verifica", true);
        layoutEquivalents.addView(header);

        for (double[] combo : combos) {
            double fNum = combo[0];
            double t    = combo[1];
            // Verifica: ricalcola EV dalla coppia N,t per confermare la formula
            double evCheck = ExposureCalculator.fNumberAndShutterSpeedToEV(fNum, t);

            LinearLayout row = makeRow(
                    ExposureCalculator.formatFNumber(fNum),
                    ExposureCalculator.formatShutterSpeed(t),
                    String.format(Locale.getDefault(), "%.2f", evCheck),
                    false);
            layoutEquivalents.addView(row);
        }
    }

    private LinearLayout makeRow(String col1, String col2, String col3, boolean isHeader) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 4, 0, 4);

        int weight1 = 3, weight2 = 3, weight3 = 2;

        TextView tv1 = makeCell(col1, weight1, isHeader);
        TextView tv2 = makeCell(col2, weight2, isHeader);
        TextView tv3 = makeCell(col3, weight3, isHeader);

        row.addView(tv1);
        row.addView(tv2);
        row.addView(tv3);

        if (isHeader) {
            row.setBackgroundColor(0xFF1565C0); // blu scuro
        } else {
            row.setBackgroundColor(0xFF1E1E2E); // sfondo scuro
        }

        return row;
    }

    private TextView makeCell(String text, int weight, boolean isHeader) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        params.setMargins(4, 2, 4, 2);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextSize(13f);
        tv.setTextColor(isHeader ? 0xFFFFFFFF : 0xFFE0E0E0);
        if (isHeader) tv.setTypeface(null, android.graphics.Typeface.BOLD);
        return tv;
    }

    // ─── Descrizione della scena in base all'EV₁₀₀ ───────────────────────────
    private String describeScene(double ev100) {
        // Tabella di riferimento da Wikipedia "Exposure value" e ANSI PH2.7-1986
        if (ev100 < -1)  return "Scena: buio totale / soggetti illuminati da candele";
        if (ev100 < 1)   return "Scena: luce di candele, fiamma, natale";
        if (ev100 < 3)   return "Scena: interni poco illuminati, luce artificiale tenue";
        if (ev100 < 5)   return "Scena: interni con illuminazione domestica";
        if (ev100 < 7)   return "Scena: interni ben illuminati, uffici, negozi";
        if (ev100 < 9)   return "Scena: illuminazione stradale notturna, neon";
        if (ev100 < 11)  return "Scena: cielo coperto, ombra profonda all'aperto";
        if (ev100 < 12)  return "Scena: cielo nuvoloso, luce diffusa";
        if (ev100 < 13)  return "Scena: sole velato, luce morbida";
        if (ev100 < 14)  return "Scena: luce solare diretta, ombra leggera";
        if (ev100 < 15)  return "Scena: sole pieno, giornata luminosa";
        if (ev100 < 16)  return "Scena: sole molto intenso, spiaggia / neve";
        return               "Scena: luce estremamente intensa (riflesso solare, arco voltaico)";
    }

    // ─── Utility ──────────────────────────────────────────────────────────────
    private double parseLux() {
        String text = etLux.getText().toString().trim();
        if (text.isEmpty()) {
            showError("Inserisci un valore in lux.");
            return -1;
        }
        try {
            double lux = Double.parseDouble(text);
            if (lux <= 0) {
                showError("Il valore lux deve essere maggiore di zero.");
                return -1;
            }
            return lux;
        } catch (NumberFormatException e) {
            showError("Valore lux non valido.");
            return -1;
        }
    }

    private int getSelectedISO() {
        return ExposureCalculator.STANDARD_ISO_VALUES[spinnerISO.getSelectedItemPosition()];
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
