package com.photography.luxexposimeter;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * ExposureLogStore — persistenza del log esposizioni su SharedPreferences.
 * <p>
 * Le voci sono serializzate come array JSON; il contatore progressivo è
 * separato dalle voci, così i numeri restano stabili come in un log di
 * scatto: eliminare una voce non riassegna mai i numeri delle altre.
 */
final class ExposureLogStore {

    private static final String PREFS_NAME = "exposure_log";
    private static final String KEY_ENTRIES = "entries";
    private static final String KEY_NEXT_NUMBER = "next_number";

    private ExposureLogStore() {
    }

    /** Numero che verrà assegnato al prossimo salvataggio. */
    static int peekNextNumber(Context context) {
        return prefs(context).getInt(KEY_NEXT_NUMBER, 1);
    }

    /** Tutte le voci salvate, in ordine di salvataggio (numero crescente). */
    static List<SavedExposure> load(Context context) {
        List<SavedExposure> entries = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs(context).getString(KEY_ENTRIES, "[]"));
            for (int i = 0; i < array.length(); i++) {
                entries.add(SavedExposure.fromJson(array.getJSONObject(i)));
            }
        } catch (JSONException ignored) {
            // Log corrotto: si riparte da lista vuota, il contatore resta valido.
        }
        return entries;
    }

    /** Aggiunge la voce e fa avanzare il contatore progressivo. */
    static void add(Context context, SavedExposure entry) {
        List<SavedExposure> entries = load(context);
        entries.add(entry);
        persist(context, entries);
        prefs(context).edit().putInt(KEY_NEXT_NUMBER, entry.number + 1).apply();
    }

    /** Aggiorna le note della voce con il numero indicato. */
    static void updateNotes(Context context, int number, String notes) {
        List<SavedExposure> entries = load(context);
        for (SavedExposure entry : entries) {
            if (entry.number == number) {
                entry.notes = notes;
                break;
            }
        }
        persist(context, entries);
    }

    /** Elimina la voce con il numero indicato (il numero non viene riusato). */
    static void delete(Context context, int number) {
        List<SavedExposure> entries = load(context);
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).number == number) {
                entries.remove(i);
                break;
            }
        }
        persist(context, entries);
    }

    private static void persist(Context context, List<SavedExposure> entries) {
        JSONArray array = new JSONArray();
        try {
            for (SavedExposure entry : entries) {
                array.put(entry.toJson());
            }
        } catch (JSONException ignored) {
            return; // non sovrascrivere il log con dati incompleti
        }
        prefs(context).edit().putString(KEY_ENTRIES, array.toString()).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
