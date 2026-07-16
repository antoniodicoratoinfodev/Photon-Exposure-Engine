package com.photography.luxexposimeter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SavedExposure — una voce del log delle esposizioni salvate.
 * <p>
 * Contiene la triade esposimetrica mostrata in Results (ISO, diaframma,
 * tempo) con apertura standard e tempo approssimato, più il contesto del
 * calcolo: lux misurati, EV, tab Digital/Analog e — nel tab Analog —
 * pellicola e tempo corretto per la reciprocità (solo in modalità A;
 * in modalità B la compensazione è già inclusa nel diaframma).
 * <p>
 * Ogni voce ha un numero progressivo stabile (mai riassegnato) e data/ora
 * del salvataggio, così da ricostruire l'ordine degli scatti.
 */
public class SavedExposure {

    public final int number;
    public long timestamp;              // assegnato alla conferma del salvataggio
    public final double lux;
    public final int iso;
    public final double ev100;
    public final double evISO;
    public final double fNumber;
    public final double shutterSpeed;
    public final double fNumberStandard;
    public final double shutterSpeedStandard;
    public final boolean analog;
    public final int calcMode;          // 1 = aperture priority (A), 2 = shutter priority (B)
    public final String filmName;       // null nel tab Digital
    public final double filmTime;       // NaN se non applicabile (Digital o modalità B)
    public String notes;

    public SavedExposure(int number, long timestamp, double lux, int iso,
                         double ev100, double evISO,
                         double fNumber, double shutterSpeed,
                         double fNumberStandard, double shutterSpeedStandard,
                         boolean analog, int calcMode,
                         String filmName, double filmTime, String notes) {
        this.number = number;
        this.timestamp = timestamp;
        this.lux = lux;
        this.iso = iso;
        this.ev100 = ev100;
        this.evISO = evISO;
        this.fNumber = fNumber;
        this.shutterSpeed = shutterSpeed;
        this.fNumberStandard = fNumberStandard;
        this.shutterSpeedStandard = shutterSpeedStandard;
        this.analog = analog;
        this.calcMode = calcMode;
        this.filmName = filmName;
        this.filmTime = filmTime;
        this.notes = notes == null ? "" : notes;
    }

    // ─── Righe di presentazione condivise tra dialog e lista ─────────────────
    /** Triade esposimetrica: "ISO 400 · f/5.6 · 1/125". */
    public String triadLine() {
        return "ISO " + iso
                + " · " + ExposureCalculator.formatFNumber(fNumber)
                + " · " + ExposureCalculator.formatShutterSpeed(shutterSpeed);
    }

    /** Valori normalizzati: "std f/5.6 · approx 1/125". */
    public String stdLine() {
        return "std " + ExposureCalculator.formatFNumber(fNumberStandard)
                + " · approx " + ExposureCalculator.formatShutterSpeed(shutterSpeedStandard);
    }

    /** Riga pellicola per il tab Analog (null nel tab Digital). */
    public String filmLine() {
        if (!analog || filmName == null) return null;
        if (!Double.isNaN(filmTime)) {
            return filmName + " · film time "
                    + ExposureCalculator.formatShutterSpeed(filmTime);
        }
        return filmName + " · reciprocity on aperture";
    }

    // ─── Serializzazione JSON (SharedPreferences) ─────────────────────────────
    JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("n", number);
        o.put("ts", timestamp);
        o.put("lux", lux);
        o.put("iso", iso);
        o.put("ev100", ev100);
        o.put("evISO", evISO);
        o.put("f", fNumber);
        o.put("t", shutterSpeed);
        o.put("fStd", fNumberStandard);
        o.put("tStd", shutterSpeedStandard);
        o.put("analog", analog);
        o.put("mode", calcMode);
        if (filmName != null) o.put("film", filmName);
        if (!Double.isNaN(filmTime)) o.put("tFilm", filmTime);
        o.put("notes", notes);
        return o;
    }

    static SavedExposure fromJson(JSONObject o) throws JSONException {
        return new SavedExposure(
                o.getInt("n"),
                o.getLong("ts"),
                o.getDouble("lux"),
                o.getInt("iso"),
                o.getDouble("ev100"),
                o.getDouble("evISO"),
                o.getDouble("f"),
                o.getDouble("t"),
                o.getDouble("fStd"),
                o.getDouble("tStd"),
                o.getBoolean("analog"),
                o.optInt("mode", 0),
                o.has("film") ? o.getString("film") : null,
                o.has("tFilm") ? o.getDouble("tFilm") : Double.NaN,
                o.optString("notes", ""));
    }
}
