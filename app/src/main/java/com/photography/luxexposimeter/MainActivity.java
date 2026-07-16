package com.photography.luxexposimeter;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Locale;

/**
 * MainActivity — Convertitore Lux → Triade Esposimetrica
 * <p>
 * L'utente inserisce:
 *   - Valore in lux (misurato con l'esposimetro)
 *   - ISO desiderato (selezionabile da spinner o seekbar)
 *   - Modalità di calcolo:
 *       A) Fisso il diaframma → calcola il tempo
 *       B) Fisso il tempo    → calcola il diaframma
 * <p>
 * Due tab in testa alla pagina:
 *   - Digital: matematica esposimetrica pura (legge di reciprocità valida)
 *   - Analog:  stessa app, ma il tempo viene corretto per il difetto di
 *              reciprocità (reciprocity failure) della pellicola selezionata;
 *              in modalità B è il diaframma a essere compensato.
 * <p>
 * L'app mostra:
 *   - EV a ISO 100
 *   - EV corretto per l'ISO scelto
 *   - Diaframma (f-number)
 *   - Tempo di esposizione (e tempo corretto per la pellicola nel tab Analog)
 *   - Tabella delle combinazioni equivalenti
 *   - Descrizione della scena in base all'EV
 */
public class MainActivity extends AppCompatActivity {

    // ─── Widget ───────────────────────────────────────────────────────────────
    private TabLayout tabMode;
    private EditText etLux;
    private Spinner spinnerISO;
    private Spinner spinnerFStop;
    private Spinner spinnerShutter;
    private Spinner spinnerFilm;
    private View cardFilm;
    private TextView tvFilmInfo;
    private Button btnCalcFromFStop;
    private Button btnCalcFromShutter;
    private Button btnFormulas;

    private TextView tvEV100;
    private TextView tvEVISO;
    private TextView tvFNumber;
    private TextView tvShutterSpeed;
    private TextView tvShutterCorrected;
    private TextView tvReciprocityNote;
    private TextView tvSceneDescription;
    private LinearLayout layoutEquivalents;
    private TextView tvEquivalentsHeader;

    // ─── Dati ─────────────────────────────────────────────────────────────────
    private FilmStock[] filmStocks;

    // Stato del tab (false = Digital, true = Analog) e ultima modalità
    // calcolata (0 = nessuna, 1 = diaframma fisso, 2 = tempo fisso) per
    // ricalcolare automaticamente al cambio di tab o pellicola.
    private boolean analogMode = false;
    private int lastCalcMode = 0;

    // Preferenza per il tema chiaro/scuro (default: scuro, il look originale)
    private static final String PREFS_NAME = "settings";
    private static final String KEY_DARK_THEME = "dark_theme";

    // Ultime selezioni dei pannelli A/B, ripristinate al prossimo avvio
    private static final String KEY_LAST_FSTOP_INDEX = "last_fstop_index";
    private static final String KEY_LAST_SHUTTER_INDEX = "last_shutter_index";
    private static final int DEFAULT_FSTOP_INDEX = 11;   // f/5.6
    private static final int DEFAULT_SHUTTER_INDEX = 36; // 1/125

    // Stato da preservare quando l'activity viene ricreata (es. cambio tema)
    private static final String STATE_LAST_CALC_MODE = "state_last_calc_mode";
    private static final String STATE_ANALOG_MODE = "state_analog_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        configureSystemBars();
        bindViews();
        populateSpinners();
        setupListeners();
    }

    // ─── Stato dell'activity (sopravvive al cambio tema) ─────────────────────
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_LAST_CALC_MODE, lastCalcMode);
        outState.putBoolean(STATE_ANALOG_MODE, analogMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Il super ripristina lo stato delle view (testo lux, spinner);
        // dopo possiamo ricalcolare con gli stessi input di prima.
        super.onRestoreInstanceState(savedInstanceState);
        lastCalcMode = savedInstanceState.getInt(STATE_LAST_CALC_MODE, 0);
        boolean analog = savedInstanceState.getBoolean(STATE_ANALOG_MODE, false);
        if (analog && !analogMode) {
            TabLayout.Tab tab = tabMode.getTabAt(1);
            if (tab != null) {
                tab.select(); // scatena setAnalogMode → recalculateIfPossible
                return;
            }
        }
        recalculateIfPossible();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                .putInt(KEY_LAST_FSTOP_INDEX, spinnerFStop.getSelectedItemPosition())
                .putInt(KEY_LAST_SHUTTER_INDEX, spinnerShutter.getSelectedItemPosition())
                .apply();
    }

    /** Legge un indice salvato, ricadendo sul default se fuori dai limiti. */
    private int savedIndexOrDefault(String key, int fallback, int size) {
        int index = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(key, fallback);
        return (index >= 0 && index < size) ? index : fallback;
    }

    // ─── Tema chiaro/scuro ────────────────────────────────────────────────────
    private void applySavedTheme() {
        AppCompatDelegate.setDefaultNightMode(isDarkThemeSaved()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private boolean isDarkThemeSaved() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(KEY_DARK_THEME, true);
    }

    private void toggleTheme() {
        boolean dark = !isDarkThemeSaved();
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit().putBoolean(KEY_DARK_THEME, dark).apply();
        AppCompatDelegate.setDefaultNightMode(dark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void configureSystemBars() {
        View root = findViewById(R.id.rootScroll);
        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(getWindow(), root);
        boolean night = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        controller.setAppearanceLightStatusBars(!night);
        controller.setAppearanceLightNavigationBars(!night);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(0, bars.top, 0, bars.bottom);
            return windowInsets;
        });
    }

    // ─── Binding ──────────────────────────────────────────────────────────────
    private void bindViews() {
        tabMode = findViewById(R.id.tabMode);
        etLux = findViewById(R.id.etLux);
        spinnerISO = findViewById(R.id.spinnerISO);
        spinnerFStop = findViewById(R.id.spinnerFStop);
        spinnerShutter = findViewById(R.id.spinnerShutter);
        spinnerFilm = findViewById(R.id.spinnerFilm);
        cardFilm = findViewById(R.id.cardFilm);
        tvFilmInfo = findViewById(R.id.tvFilmInfo);
        btnCalcFromFStop = findViewById(R.id.btnCalcFromFStop);
        btnCalcFromShutter = findViewById(R.id.btnCalcFromShutter);
        btnFormulas = findViewById(R.id.btnFormulas);
        tvEV100 = findViewById(R.id.tvEV100);
        tvEVISO = findViewById(R.id.tvEVISO);
        tvFNumber = findViewById(R.id.tvFNumber);
        tvShutterSpeed = findViewById(R.id.tvShutterSpeed);
        tvShutterCorrected = findViewById(R.id.tvShutterCorrected);
        tvReciprocityNote = findViewById(R.id.tvReciprocityNote);
        tvSceneDescription = findViewById(R.id.tvSceneDescription);
        layoutEquivalents = findViewById(R.id.layoutEquivalents);
        tvEquivalentsHeader = findViewById(R.id.tvEquivalentsHeader);
    }

    // ─── Popolamento spinner ──────────────────────────────────────────────────
    private void populateSpinners() {
        // ISO
        int[] isoValues = ExposureCalculator.STANDARD_ISO_VALUES;
        String[] isoLabels = new String[isoValues.length];
        for (int i = 0; i < isoValues.length; i++) {
            isoLabels[i] = "ISO " + isoValues[i];
        }
        ArrayAdapter<String> isoAdapter = createWhiteTextAdapter(isoLabels);
        isoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerISO.setAdapter(isoAdapter);
        spinnerISO.setSelection(4); // ISO 100

        // F-stop
        double[] fStops = ExposureCalculator.STANDARD_F_STOPS;
        String[] fStopLabels = new String[fStops.length];
        for (int i = 0; i < fStops.length; i++) {
            fStopLabels[i] = ExposureCalculator.formatFNumber(fStops[i]);
        }
        ArrayAdapter<String> fStopAdapter = createWhiteTextAdapter(fStopLabels);
        fStopAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFStop.setAdapter(fStopAdapter);
        spinnerFStop.setSelection(savedIndexOrDefault(
                KEY_LAST_FSTOP_INDEX, DEFAULT_FSTOP_INDEX, fStops.length));

        // Shutter speeds
        double[] shutters = ExposureCalculator.STANDARD_SHUTTER_SPEEDS;
        String[] shutterLabels = new String[shutters.length];
        for (int i = 0; i < shutters.length; i++) {
            shutterLabels[i] = ExposureCalculator.formatShutterSpeed(shutters[i]);
        }
        ArrayAdapter<String> shutterAdapter = createWhiteTextAdapter(shutterLabels);
        shutterAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerShutter.setAdapter(shutterAdapter);
        spinnerShutter.setSelection(savedIndexOrDefault(
                KEY_LAST_SHUTTER_INDEX, DEFAULT_SHUTTER_INDEX, shutters.length));

        // Pellicole per il tab Analog
        filmStocks = FilmStock.analogValues();
        String[] filmLabels = new String[filmStocks.length];
        int hp5Index = 0;
        for (int i = 0; i < filmStocks.length; i++) {
            filmLabels[i] = filmStocks[i].displayName;
            if (filmStocks[i] == FilmStock.ILFORD_HP5_PLUS) hp5Index = i;
        }
        ArrayAdapter<String> filmAdapter = createWhiteTextAdapter(filmLabels);
        filmAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFilm.setAdapter(filmAdapter);
        spinnerFilm.setSelection(hp5Index); // Ilford HP5+ come default
    }

    // ─── Listener ─────────────────────────────────────────────────────────────
    private void setupListeners() {
        btnCalcFromFStop.setOnClickListener(v -> calculateFromFStop());
        btnCalcFromShutter.setOnClickListener(v -> calculateFromShutter());
        btnFormulas.setOnClickListener(v ->
                startActivity(new Intent(this, FormulasActivity.class)));
        ImageButton btnTheme = findViewById(R.id.btnTheme);
        btnTheme.setOnClickListener(v -> toggleTheme());

        tabMode.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setAnalogMode(tab.getPosition() == 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        spinnerFilm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFilmInfo();
                recalculateIfPossible();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Persisti subito le selezioni dei pannelli A/B: così sopravvivono
        // anche se il processo viene terminato senza passare da onPause.
        spinnerFStop.setOnItemSelectedListener(
                persistSelectionListener(KEY_LAST_FSTOP_INDEX));
        spinnerShutter.setOnItemSelectedListener(
                persistSelectionListener(KEY_LAST_SHUTTER_INDEX));
    }

    private AdapterView.OnItemSelectedListener persistSelectionListener(String prefKey) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                        .putInt(prefKey, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    // ─── Cambio tab Digital / Analog ──────────────────────────────────────────
    private void setAnalogMode(boolean analog) {
        analogMode = analog;
        cardFilm.setVisibility(analog ? View.VISIBLE : View.GONE);
        if (!analog) {
            tvShutterCorrected.setVisibility(View.GONE);
            tvReciprocityNote.setVisibility(View.GONE);
        } else {
            updateFilmInfo();
        }
        recalculateIfPossible();
    }

    private void updateFilmInfo() {
        tvFilmInfo.setText(getSelectedFilm().note);
    }

    private FilmStock getSelectedFilm() {
        return filmStocks[spinnerFilm.getSelectedItemPosition()];
    }

    /** Ricalcola con l'ultima modalità usata (se un risultato era già a schermo). */
    private void recalculateIfPossible() {
        if (lastCalcMode == 1) {
            calculateFromFStop();
        } else if (lastCalcMode == 2) {
            calculateFromShutter();
        }
    }

    private ArrayAdapter<String> createWhiteTextAdapter(String[] items) {
        return new ArrayAdapter<>(this, R.layout.spinner_item, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary));
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_primary));
                text.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.card_bg));
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
            // Il tempo misurato (esposimetro) è identico nei due tab: nel tab
            // Analog la correzione di reciprocità viene applicata in displayResult.
            ExposureCalculator.ExposureResult result =
                    ExposureCalculator.calculate(lux, iso, fNumber);
            lastCalcMode = 1;
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
            ExposureCalculator.ExposureResult result;
            if (analogMode) {
                // Il tempo scelto è quello REALE di scatto: la pellicola a
                // tempi lunghi rende meno, quindi l'esposimetro va soddisfatto
                // con il tempo equivalente t_eq = correct⁻¹(t) e il diaframma
                // si apre di log₂(t/t_eq) stop: N = √(t_eq · 2^EV).
                FilmStock film = getSelectedFilm();
                double ev100 = ExposureCalculator.luxToEV100(lux);
                double evISO = ExposureCalculator.adjustEVForISO(ev100, iso);
                double tEq = ReciprocityCalculator.meteredEquivalentTime(film, shutterSpeed);
                double fNumber = ExposureCalculator.evAndShutterSpeedToFNumber(evISO, tEq);
                result = new ExposureCalculator.ExposureResult(lux, iso, ev100, evISO,
                        fNumber, shutterSpeed,
                        ExposureCalculator.nearestStandardFStop(fNumber),
                        ExposureCalculator.nearestStandardShutterSpeed(shutterSpeed));
            } else {
                result = ExposureCalculator.calculateFromShutter(lux, iso, shutterSpeed);
            }
            lastCalcMode = 2;
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

        // Tempo di esposizione (+ correzione di reciprocità nel tab Analog)
        if (analogMode) {
            displayAnalogShutter(result, fixedFStop);
        } else if (!fixedFStop) {
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

    // ─── Righe tempo/reciprocità del tab Analog ───────────────────────────────
    private void displayAnalogShutter(ExposureCalculator.ExposureResult result,
                                      boolean fixedFStop) {
        FilmStock film = getSelectedFilm();
        StringBuilder note = new StringBuilder();
        boolean beyondData;

        if (fixedFStop) {
            // Modalità A: il tempo misurato va allungato al tempo corretto
            double metered = result.shutterSpeed;
            double corrected = ReciprocityCalculator.correctedTime(film, metered);
            double stops = ReciprocityCalculator.compensationStops(film, metered);
            beyondData = ReciprocityCalculator.isBeyondData(film, metered);

            tvShutterSpeed.setText(String.format(Locale.getDefault(),
                    "Meter time: %s  (uncorrected)",
                    ExposureCalculator.formatShutterSpeed(metered)));
            if (stops > 0.005) {
                tvShutterCorrected.setText(String.format(Locale.getDefault(),
                        "Film time: %s  (+%.1f stop)",
                        ExposureCalculator.formatShutterSpeed(corrected), stops));
            } else {
                tvShutterCorrected.setText(String.format(Locale.getDefault(),
                        "Film time: %s  (no correction needed)",
                        ExposureCalculator.formatShutterSpeed(corrected)));
            }
        } else {
            // Modalità B: il tempo è quello reale, la compensazione è sul diaframma
            double actual = result.shutterSpeed;
            double tEq = ReciprocityCalculator.meteredEquivalentTime(film, actual);
            double stops = ExposureCalculator.log2(actual / tEq);
            beyondData = ReciprocityCalculator.isBeyondData(film, tEq);

            tvFNumber.setText(String.format(Locale.getDefault(),
                    "Aperture: %s  (film-compensated) → std: %s",
                    ExposureCalculator.formatFNumber(result.fNumber),
                    ExposureCalculator.formatFNumber(result.fNumberStandard)));
            tvShutterSpeed.setText(String.format(Locale.getDefault(),
                    "Shutter speed: %s  (selected, actual)",
                    ExposureCalculator.formatShutterSpeed(actual)));
            if (stops > 0.005) {
                tvShutterCorrected.setText(String.format(Locale.getDefault(),
                        "Reciprocity: aperture opened +%.1f stop (meter-equivalent time %s)",
                        stops, ExposureCalculator.formatShutterSpeed(tEq)));
            } else {
                tvShutterCorrected.setText(R.string.reciprocity_no_correction);
            }
        }

        note.append(film.note);
        if (beyondData) {
            note.append("\n⚠ Beyond published manufacturer data: value extrapolated.");
        }
        tvShutterCorrected.setVisibility(View.VISIBLE);
        tvReciprocityNote.setText(note.toString());
        tvReciprocityNote.setVisibility(View.VISIBLE);
    }

    // ─── Tabella combinazioni equivalenti ─────────────────────────────────────
    private void buildEquivalentsTable(double evISO, int iso) {
        layoutEquivalents.removeAllViews();

        List<double[]> combos = ExposureCalculator.getEquivalentCombinations(evISO);

        if (combos.isEmpty()) {
            tvEquivalentsHeader.setText(R.string.equivalents_empty);
            return;
        }

        tvEquivalentsHeader.setText(getString(R.string.equivalents_result_title, evISO, iso));

        FilmStock film = analogMode ? getSelectedFilm() : FilmStock.DIGITAL;

        // Intestazione tabella: nel tab Analog la terza colonna mostra il
        // tempo corretto per la reciprocità invece della verifica EV.
        LinearLayout header = analogMode
                ? makeRow("Aperture", "Meter time", "Approx.", "Film time", true, false)
                : makeRow("Aperture", "Shutter speed", "Approx.", "EV validation", true, false);
        layoutEquivalents.addView(header);

        for (int i = 0; i < combos.size(); i++) {
            double fNum = combos.get(i)[0];
            double t = combos.get(i)[1];

            String col4;
            if (analogMode) {
                double corrected = ReciprocityCalculator.correctedTime(film, t);
                col4 = ExposureCalculator.formatShutterSpeed(corrected);
            } else {
                double evCheck = ExposureCalculator.fNumberAndShutterSpeedToEV(fNum, t);
                col4 = String.format(Locale.getDefault(), "%.2f", evCheck);
            }

            LinearLayout row = makeRow(
                    ExposureCalculator.formatFNumber(fNum),
                    ExposureCalculator.formatShutterSpeed(t),
                    ExposureCalculator.formatShutterSpeed(
                            ExposureCalculator.nearestStandardShutterSpeed(t)),
                    col4,
                    false, i % 2 == 1);
            layoutEquivalents.addView(row);
        }
    }

    private LinearLayout makeRow(String col1, String col2, String col3, String col4,
                                 boolean isHeader, boolean altRow) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        int spacing = dp10();
        row.setPadding(spacing, spacing, spacing, spacing);

        // Nel tab Analog l'ultima colonna ospita tempi lunghi ("1h 31' 0\""):
        // le diamo lo stesso peso delle altre.
        int weight1 = 3, weight2 = 3, weight3 = 3, weight4 = analogMode ? 3 : 2;

        TextView tv1 = makeCell(col1, weight1, isHeader);
        TextView tv2 = makeCell(col2, weight2, isHeader);
        TextView tv3 = makeCell(col3, weight3, isHeader);
        TextView tv4 = makeCell(col4, weight4, isHeader);

        row.addView(tv1);
        row.addView(tv2);
        row.addView(tv3);
        row.addView(tv4);

        if (isHeader) {
            GradientDrawable headerBg = new GradientDrawable();
            headerBg.setColor(ContextCompat.getColor(this, R.color.header_row_bg));
            headerBg.setCornerRadius(spacing);
            row.setBackground(headerBg);
        } else if (altRow) {
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.row_alt));
        }

        return row;
    }

    private TextView makeCell(String text, int weight, boolean isHeader) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        tv.setText(text);
        if (isHeader) {
            tv.setTextSize(12f);
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_on_accent));
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tv.setTextSize(13f);
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tv.setTypeface(android.graphics.Typeface.MONOSPACE);
        }
        return tv;
    }

    private int dp10() {
        return Math.round(getResources().getDisplayMetrics().density * 10);
    }

    // ─── ENUM per la descrizione della scena (sostituisce il vecchio metodo if/else) ───
    private enum SceneEV {
        EXTREME_DARK(0.0, "Total darkness, candlelight only"),
        CANDLELIGHT(2.0, "Candlelight, flame, very low light"),
        DIM_INTERIOR(4.0, "Dim interior lighting, low light"),
        HOME_INTERIOR(6.0, "Indoor lighting, typical home illumination"),
        BRIGHT_INTERIOR(8.0, "Well-lit interiors, offices, retail spaces"),
        NIGHT_STREET(10.0, "Night street lighting, neon signs, urban night scene"),
        OVERCAST_SHADE(12.0, "Heavy overcast, deep open shade"),
        CLOUDY(13.0, "Overcast sky, soft diffused daylight"),
        LIGHT_CLOUD(14.0, "Thin cloud cover, soft daylight, veiled sun"),
        DIRECT_SUN(15.0, "Direct sunlight, slight shadows"),
        FULL_SUN(16.0, "Full sunlight, clear day"),
        BRIGHT_SUN_REFLECTED(17.0, "Very bright sunlight, sand/snow reflections"),
        EXTREME_LIGHT(Double.MAX_VALUE, "Extreme brightness, strong reflections (snow, desert, arc lamps)");

        private final double upperBound; // limite superiore (escluso per l'ultimo)
        private final String description;

        SceneEV(double upperBound, String description) {
            this.upperBound = upperBound;
            this.description = description;
        }

        /**
         * Restituisce la descrizione della scena in base all'EV a ISO 100.
         * Ogni fascia copre [bound precedente, upperBound): l'EV canonico della
         * tabella Wikipedia/ANSI PH2.7 (es. EV 15 = pieno sole, EV 16 = sabbia/neve)
         * cade all'inizio della propria fascia.
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
