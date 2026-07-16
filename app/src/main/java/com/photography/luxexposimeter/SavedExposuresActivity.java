package com.photography.luxexposimeter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SavedExposuresActivity — Saved exposures (log di scatto)
 * <p>
 * Elenca i salvataggi in ordine di numero progressivo: triade esposimetrica,
 * valori standard/approssimati, data e ora, contesto Digital/Analog e note.
 * Toccando una voce si apre il dettaglio completo con le note modificabili;
 * l'icona cestino elimina la voce (i numeri delle altre non cambiano).
 */
public class SavedExposuresActivity extends AppCompatActivity {

    private ExposureAdapter adapter;
    private LayoutInflater inflater;
    private final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private final AtomicInteger loadGeneration = new AtomicInteger();
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy '·' HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_saved);

        View root = findViewById(R.id.rootSaved);
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
        ListView listEntries = findViewById(R.id.listSavedEntries);
        TextView tvEmpty = findViewById(R.id.tvSavedEmpty);
        inflater = LayoutInflater.from(this);
        adapter = new ExposureAdapter();
        listEntries.setEmptyView(tvEmpty);
        listEntries.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadEntriesAsync();
    }

    @Override
    protected void onDestroy() {
        loadGeneration.incrementAndGet();
        // Le scritture già accodate devono concludersi anche se l'activity si chiude.
        logExecutor.shutdown();
        super.onDestroy();
    }

    // ─── Lista virtualizzata e caricamento fuori dal thread UI ───────────────
    private void reloadEntriesAsync() {
        int request = loadGeneration.incrementAndGet();
        logExecutor.execute(() -> {
            List<SavedExposure> entries = ExposureLogStore.load(getApplicationContext());
            runOnUiThread(() -> {
                if (request == loadGeneration.get() && !isFinishing() && !isDestroyed()) {
                    adapter.replaceWith(entries);
                }
            });
        });
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
                    String notes = etNotes.getText().toString().trim();
                    logExecutor.execute(() -> {
                        ExposureLogStore.updateNotes(getApplicationContext(), entry.number, notes);
                        runOnUiThread(() -> {
                            if (!isFinishing() && !isDestroyed()) {
                                entry.notes = notes;
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, R.string.toast_notes_saved,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
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
                .setPositiveButton(R.string.dialog_delete, (dialog, which) ->
                    logExecutor.execute(() -> {
                        ExposureLogStore.delete(getApplicationContext(), entry.number);
                        runOnUiThread(() -> {
                            if (!isFinishing() && !isDestroyed()) {
                                adapter.remove(entry.number);
                            }
                        });
                    }))
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private final class ExposureAdapter extends BaseAdapter {

        private final List<SavedExposure> entries = new ArrayList<>();

        void replaceWith(List<SavedExposure> newEntries) {
            entries.clear();
            entries.addAll(newEntries);
            notifyDataSetChanged();
        }

        void remove(int number) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).number == number) {
                    entries.remove(i);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public SavedExposure getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).number;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EntryViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_saved_exposure, parent, false);
                holder = new EntryViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (EntryViewHolder) convertView.getTag();
            }
            holder.bind(getItem(position));
            return convertView;
        }
    }

    private final class EntryViewHolder {

        private final TextView number;
        private final TextView triad;
        private final TextView standard;
        private final TextView meta;
        private final TextView notes;
        private SavedExposure entry;

        EntryViewHolder(View card) {
            number = card.findViewById(R.id.tvItemNumber);
            triad = card.findViewById(R.id.tvItemTriad);
            standard = card.findViewById(R.id.tvItemStd);
            meta = card.findViewById(R.id.tvItemMeta);
            notes = card.findViewById(R.id.tvItemNotes);
            ImageButton delete = card.findViewById(R.id.btnItemDelete);

            // I listener vengono creati una sola volta per view riciclata.
            card.setOnClickListener(v -> showDetailDialog(entry));
            delete.setOnClickListener(v -> confirmDelete(entry));
        }

        void bind(SavedExposure newEntry) {
            entry = newEntry;
            number.setText(String.format(Locale.getDefault(), "#%d", entry.number));
            triad.setText(entry.triadLine());
            standard.setText(entry.stdLine());
            meta.setText(buildMetaLine(entry));

            if (entry.notes.isEmpty()) {
                notes.setText(null);
                notes.setVisibility(View.GONE);
            } else {
                notes.setText(entry.notes);
                notes.setVisibility(View.VISIBLE);
            }
        }
    }
}
