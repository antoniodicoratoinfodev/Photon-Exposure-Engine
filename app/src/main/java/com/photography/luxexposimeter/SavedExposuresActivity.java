package com.photography.luxexposimeter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * SavedExposuresActivity — Saved exposures (log di scatto)
 * <p>
 * Elenca i salvataggi in ordine di numero progressivo: triade esposimetrica,
 * valori standard/approssimati, data e ora, contesto Digital/Analog e note.
 * Toccando una voce si apre il dettaglio completo con le note modificabili;
 * l'icona cestino elimina la voce (i numeri delle altre non cambiano).
 */
public class SavedExposuresActivity extends AppCompatActivity {

    private LinearLayout layoutEntries;
    private TextView tvEmpty;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy '·' HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_saved);

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

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        layoutEntries = findViewById(R.id.layoutSavedEntries);
        tvEmpty = findViewById(R.id.tvSavedEmpty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rebuildList();
    }

    // ─── Lista dei salvataggi ─────────────────────────────────────────────────
    private void rebuildList() {
        List<SavedExposure> entries = ExposureLogStore.load(this);
        layoutEntries.removeAllViews();
        tvEmpty.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (SavedExposure entry : entries) {
            View card = inflater.inflate(R.layout.item_saved_exposure, layoutEntries, false);
            bindEntryCard(card, entry);
            layoutEntries.addView(card);
        }
    }

    private void bindEntryCard(View card, SavedExposure entry) {
        ((TextView) card.findViewById(R.id.tvItemNumber)).setText(
                String.format(Locale.getDefault(), "#%d", entry.number));
        ((TextView) card.findViewById(R.id.tvItemTriad)).setText(entry.triadLine());
        ((TextView) card.findViewById(R.id.tvItemStd)).setText(entry.stdLine());
        ((TextView) card.findViewById(R.id.tvItemMeta)).setText(buildMetaLine(entry));

        TextView tvNotes = card.findViewById(R.id.tvItemNotes);
        if (entry.notes.isEmpty()) {
            tvNotes.setVisibility(View.GONE);
        } else {
            tvNotes.setText(entry.notes);
            tvNotes.setVisibility(View.VISIBLE);
        }

        card.setOnClickListener(v -> showDetailDialog(entry));
        card.findViewById(R.id.btnItemDelete)
                .setOnClickListener(v -> confirmDelete(entry));
    }

    /** "14 Jul 2026 · 18:22 — Analog · Ilford HP5+" */
    private String buildMetaLine(SavedExposure entry) {
        StringBuilder meta = new StringBuilder(dateFormat.format(new Date(entry.timestamp)));
        meta.append(" — ").append(entry.analog
                ? getString(R.string.tab_analog)
                : getString(R.string.tab_digital));
        if (entry.analog && entry.filmName != null) {
            meta.append(" · ").append(entry.filmName);
        }
        return meta.toString();
    }

    // ─── Dettaglio con note modificabili ──────────────────────────────────────
    private void showDetailDialog(SavedExposure entry) {
        View content = getLayoutInflater().inflate(R.layout.dialog_entry_detail, null);

        ((TextView) content.findViewById(R.id.tvDetailTitle)).setText(
                String.format(Locale.getDefault(), "#%d · %s",
                        entry.number, dateFormat.format(new Date(entry.timestamp))));
        ((TextView) content.findViewById(R.id.tvDetailMeta)).setText(buildModeLine(entry));
        ((TextView) content.findViewById(R.id.tvDetailData)).setText(buildDetailData(entry));

        EditText etNotes = content.findViewById(R.id.etDetailNotes);
        etNotes.setText(entry.notes);

        new MaterialAlertDialogBuilder(this)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bg_dialog))
                .setView(content)
                .setPositiveButton(R.string.dialog_save_notes, (dialog, which) -> {
                    ExposureLogStore.updateNotes(this, entry.number,
                            etNotes.getText().toString().trim());
                    Toast.makeText(this, R.string.toast_notes_saved, Toast.LENGTH_SHORT).show();
                    rebuildList();
                })
                .setNegativeButton(R.string.dialog_close, null)
                .show();
    }

    /** "Analog · Aperture priority (A)" */
    private String buildModeLine(SavedExposure entry) {
        StringBuilder mode = new StringBuilder(entry.analog
                ? getString(R.string.tab_analog)
                : getString(R.string.tab_digital));
        if (entry.calcMode == 1) {
            mode.append(" · ").append(getString(R.string.mode_a_title))
                    .append(" (").append(getString(R.string.mode_a_badge)).append(")");
        } else if (entry.calcMode == 2) {
            mode.append(" · ").append(getString(R.string.mode_b_title))
                    .append(" (").append(getString(R.string.mode_b_badge)).append(")");
        }
        return mode.toString();
    }

    private String buildDetailData(SavedExposure entry) {
        Locale locale = Locale.getDefault();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(locale, "Metering: %s lx", formatLux(entry.lux)));
        sb.append(String.format(locale, "\nEV₁₀₀ = %.2f", entry.ev100));
        sb.append(String.format(locale, "\nEV (ISO %d) = %.2f", entry.iso, entry.evISO));
        sb.append("\nISO: ").append(entry.iso);
        sb.append("\nAperture: ")
                .append(ExposureCalculator.formatFNumber(entry.fNumber))
                .append(" → std ")
                .append(ExposureCalculator.formatFNumber(entry.fNumberStandard));
        sb.append("\nShutter: ")
                .append(ExposureCalculator.formatShutterSpeed(entry.shutterSpeed))
                .append(" → approx ")
                .append(ExposureCalculator.formatShutterSpeed(entry.shutterSpeedStandard));
        if (entry.analog && entry.filmName != null) {
            sb.append("\nFilm: ").append(entry.filmName);
            if (!Double.isNaN(entry.filmTime)) {
                sb.append("\nFilm time: ")
                        .append(ExposureCalculator.formatShutterSpeed(entry.filmTime));
            } else {
                sb.append("\nReciprocity: compensated on aperture");
            }
        }
        return sb.toString();
    }

    private String formatLux(double lux) {
        if (lux == Math.floor(lux) && !Double.isInfinite(lux)) {
            return String.format(Locale.getDefault(), "%.0f", lux);
        }
        return String.format(Locale.getDefault(), "%.2f", lux);
    }

    // ─── Eliminazione ─────────────────────────────────────────────────────────
    private void confirmDelete(SavedExposure entry) {
        new MaterialAlertDialogBuilder(this)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bg_dialog))
                .setTitle(getString(R.string.delete_dialog_title, entry.number))
                .setMessage(R.string.delete_dialog_message)
                .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                    ExposureLogStore.delete(this, entry.number);
                    rebuildList();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}
