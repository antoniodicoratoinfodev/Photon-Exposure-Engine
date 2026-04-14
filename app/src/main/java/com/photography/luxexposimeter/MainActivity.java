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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

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
    private Spinner spinnerISO;
    private Spinner spinnerFStop;
    private Spinner spinnerShutter;
    private Button btnCalcFromFStop;
    private Button btnCalcFromShutter;

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
        etLux = findViewById(R.id.etLux);
        spinnerISO = findViewById(R.id.spinnerISO);
        spinnerFStop = findViewById(R.id.spinnerFStop);
        spinnerShutter = findViewById(R.id.spinnerShutter);
        btnCalcFromFStop = findViewById(R.id.btnCalcFromFStop);
        btnCalcFromShutter = findViewById(R.id.btnCalcFromShutter);
        tvEV100 = findViewById(R.id.tvEV100);
        tvEVISO = findViewById(R.id.tvEVISO);
        tvFNumber = findViewById(R.id.tvFNumber);
        tvShutterSpeed = findViewById(R.id.tvShutterSpeed);
        tvSceneDescription = findViewById(R.id.tvSceneDescription);
        layoutEquivalents = findViewById(R.id.layoutEquivalents);
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
                    "Aperture: %s  (selected)",
                    ExposureCalculator.formatFNumber(result.fNumber)));
        } else {
            tvFNumber.setText(String.format(Locale.getDefault(),
                    "Aperture: %s  (calculated value) → std: %s",
                    ExposureCalculator.formatFNumber(result.fNumber),
                    ExposureCalculator.formatFNumber(result.fNumberStandard)));
        }

        // Tempo di esposizione
        if (!fixedFStop) {
            tvShutterSpeed.setText(String.format(Locale.getDefault(),
                    "Shutter speed: %s  (selected)",
                    ExposureCalculator.formatShutterSpeed(result.shutterSpeed)));
        } else {
            tvShutterSpeed.setText(String.format(Locale.getDefault(),
                    "Shutter speed: %s  (calculated value) → std: %s",
                    ExposureCalculator.formatShutterSpeed(result.shutterSpeed),
                    ExposureCalculator.formatShutterSpeed(result.shutterSpeedStandard)));
        }

        // Descrizione della scena usando l'enum (sostituisce il vecchio metodo describeScene)
        tvSceneDescription.setText(SceneEV.fromEV(result.ev100));

        // Combinazioni equivalenti
        buildEquivalentsTable(result.evISO, result.iso);
    }

    // ─── Tabella combinazioni equivalenti ─────────────────────────────────────
    private void buildEquivalentsTable(double evISO, int iso) {
        layoutEquivalents.removeAllViews();

        List<double[]> combos = ExposureCalculator.getEquivalentCombinations(evISO);

        if (combos.isEmpty()) {
            tvEquivalentsHeader.setText("No standard combinations within the photographic range.");
            return;
        }

        tvEquivalentsHeader.setText(String.format(Locale.getDefault(),
                "Equivalent combinations (EV %.1f, ISO %d):", evISO, iso));

        // Intestazione tabella
        LinearLayout header = makeRow(
                "Aperture", "Shutter speed", "EV validation", true);
        layoutEquivalents.addView(header);

        for (double[] combo : combos) {
            double fNum = combo[0];
            double t = combo[1];
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

    // ─── ENUM per la descrizione della scena (sostituisce il vecchio metodo if/else) ───
    private enum SceneEV {
        EXTREME_DARK(-1.0, "Total darkness, candlelight only"),
        CANDLELIGHT(1.0, "Candlelight, flame, very low light"),
        DIM_INTERIOR(3.0, "Dim interior lighting, low light"),
        HOME_INTERIOR(5.0, "Indoor lighting, typical home illumination"),
        BRIGHT_INTERIOR(7.0, "Well-lit interiors, offices, retail spaces"),
        NIGHT_STREET(9.0, "Night street lighting, neon signs, urban night scene"),
        OVERCAST_SHADE(11.0, "Heavy overcast, deep open shade"),
        CLOUDY(12.0, "Overcast sky, soft diffused daylight"),
        LIGHT_CLOUD(13.0, "Thin cloud cover, soft daylight, veiled sun"),
        DIRECT_SUN(14.0, "Direct sunlight, slight shadows"),
        FULL_SUN(15.0, "Full sunlight, clear day"),
        BRIGHT_SUN_REFLECTED(16.0, "Very bright sunlight, sand/snow reflections"),
        EXTREME_LIGHT(Double.MAX_VALUE, "Extreme brightness, strong reflections (snow, desert, arc lamps)");

        private final double upperBound; // limite superiore (escluso per l'ultimo)
        private final String description;

        SceneEV(double upperBound, String description) {
            this.upperBound = upperBound;
            this.description = description;
        }

        /**
         * Restituisce la descrizione della scena in base all'EV a ISO 100.
         * I range sono identici a quelli del vecchio metodo describeScene().
         *
         * @param ev100 Valore EV (riferito a ISO 100)
         * @return descrizione testuale
         */
        public static String fromEV(double ev100) {
            for (SceneEV scene : values()) {
                if (ev100 < scene.upperBound) {
                    return scene.description;
                }
            }
            // fallback (non dovrebbe accadere mai)
            return EXTREME_LIGHT.description;
        }
    }

    // ─── Utility ──────────────────────────────────────────────────────────────
    private double parseLux() {
        String text = etLux.getText().toString().trim();
        if (text.isEmpty()) {
            showError("Enter a value in lux.");
            return -1;
        }
        try {
            double lux = Double.parseDouble(text);
            if (lux <= 0) {
                showError("The lux value must be greater than zero.");
                return -1;
            }
            return lux;
        } catch (NumberFormatException e) {
            showError("Invalid lux value.");
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