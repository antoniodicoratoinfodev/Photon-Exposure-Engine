package com.photography.luxexposimeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ExposureCalculator
 *
 * Converte un valore di illuminamento in lux nella triade esposimetrica
 * fotografica (ISO, diaframma f/N, tempo di esposizione).
 *
 * Formule di riferimento (fonte: Wikipedia "Exposure value", ANSI PH3.49-1971,
 * ISO 2720:1974):
 *
 *   1. Da lux a EV a ISO 100 (sensore piano, costante C = 250):
 *         EV₁₀₀ = log₂(E / 2.5)
 *      dove E è l'illuminamento in lux.
 *
 *   2. Correzione EV per ISO arbitrario:
 *         EV_ISO = EV₁₀₀ + log₂(ISO / 100)
 *
 *   3. Definizione di EV dalla triade:
 *         EV = log₂(N² / t)
 *      dove N = f-number, t = tempo di esposizione in secondi.
 *
 *   4. Dato EV e N, il tempo di esposizione è:
 *         t = N² / 2^EV
 *
 *   5. Dato EV e t, il diaframma è:
 *         N = sqrt(t * 2^EV)
 *
 * La costante C = 250 è il valore standard per sensori piani (flat/cosine)
 * secondo ANSI PH3.49-1971 e ISO 2720:1974.
 */
public class ExposureCalculator {

    // Costante di calibrazione per sensore piano (flat/cosine), ANSI/ISO standard
    public static final double C_FLAT = 250.0;

    // Costante di calibrazione per sensore emisferico (hemispherical/cardioid)
    // Minolta usa 330, Sekonic usa 340; usiamo 330 come default
    public static final double C_HEMI = 330.0;

    // Serie standard di f-number (diaframmi) in scala fotografica
    public static final double[] STANDARD_F_STOPS = {
            1.0, 1.2, 1.4, 1.8, 2.0, 2.5, 2.8, 3.5, 4.0,
            4.5, 5.0, 5.6, 6.3, 7.1, 8.0, 9.0, 10.0, 11.0,
            13.0, 14.0, 16.0, 18.0, 20.0, 22.0, 32.0, 45.0, 64.0
    };

    // Serie standard di tempi di esposizione in secondi (scala fotografica)
    public static final double[] STANDARD_SHUTTER_SPEEDS = {
            30.0, 25.0, 20.0, 15.0, 13.0, 10.0, 8.0, 6.0, 5.0, 4.0,
            3.2, 2.5, 2.0, 1.6, 1.3, 1.0,
            1.0 / 1.3, 1.0 / 1.6, 1.0 / 2.0, 1.0 / 2.5, 1.0 / 3.0,
            1.0 / 4.0, 1.0 / 5.0, 1.0 / 6.0, 1.0 / 8.0, 1.0 / 10.0,
            1.0 / 13.0, 1.0 / 15.0, 1.0 / 20.0, 1.0 / 25.0, 1.0 / 30.0,
            1.0 / 40.0, 1.0 / 50.0, 1.0 / 60.0, 1.0 / 80.0, 1.0 / 100.0,
            1.0 / 125.0, 1.0 / 160.0, 1.0 / 200.0, 1.0 / 250.0, 1.0 / 320.0,
            1.0 / 400.0, 1.0 / 500.0, 1.0 / 640.0, 1.0 / 800.0, 1.0 / 1000.0,
            1.0 / 1250.0, 1.0 / 1600.0, 1.0 / 2000.0, 1.0 / 2500.0, 1.0 / 3200.0,
            1.0 / 4000.0, 1.0 / 5000.0, 1.0 / 6400.0, 1.0 / 8000.0
    };

    // Serie standard di valori ISO
    public static final int[] STANDARD_ISO_VALUES = {
            25, 50, 64, 80, 100, 125, 160, 200, 250, 320,
            400, 500, 640, 800, 1000, 1250, 1600, 2000, 2500, 3200,
            4000, 5000, 6400, 12800, 25600, 51200, 102400
    };

    /**
     * Calcola l'EV a ISO 100 a partire dall'illuminamento in lux.
     * Formula: EV₁₀₀ = log₂(E / 2.5)
     * Equivalente a: EV₁₀₀ = log₂(E / (C/100)) con C = 250
     *
     * @param lux  Illuminamento in lux (E), deve essere > 0
     * @return     EV a ISO 100
     * @throws IllegalArgumentException se lux <= 0
     */
    public static double luxToEV100(double lux) {
        if (lux <= 0) {
            throw new IllegalArgumentException("Il valore lux deve essere positivo.");
        }
        // EV₁₀₀ = log₂(E / 2.5)  dove 2.5 = C/100 = 250/100
        return log2(lux / 2.5);
    }

    /**
     * Calcola l'EV corretto per un ISO arbitrario.
     * Formula: EV_ISO = EV₁₀₀ + log₂(ISO / 100)
     *
     * @param ev100  EV a ISO 100
     * @param iso    Sensibilità ISO (deve essere > 0)
     * @return       EV corretto per l'ISO specificato
     */
    public static double adjustEVForISO(double ev100, int iso) {
        if (iso <= 0) {
            throw new IllegalArgumentException("Il valore ISO deve essere positivo.");
        }
        // EV_ISO = EV₁₀₀ + log₂(ISO / 100)
        return ev100 + log2(iso / 100.0);
    }

    /**
     * Dato EV e f-number N, calcola il tempo di esposizione in secondi.
     * Formula derivata da EV = log₂(N²/t):
     *   t = N² / 2^EV
     *
     * @param ev  Valore di esposizione
     * @param fNumber  f-number (diaframma)
     * @return    Tempo di esposizione in secondi
     */
    public static double evAndFNumberToShutterSpeed(double ev, double fNumber) {
        // t = N² / 2^EV
        return (fNumber * fNumber) / Math.pow(2.0, ev);
    }

    /**
     * Dato EV e tempo di esposizione t, calcola il f-number.
     * Formula derivata da EV = log₂(N²/t):
     *   N = sqrt(t * 2^EV)
     *
     * @param ev           Valore di esposizione
     * @param shutterSpeed Tempo di esposizione in secondi
     * @return             f-number
     */
    public static double evAndShutterSpeedToFNumber(double ev, double shutterSpeed) {
        // N = sqrt(t * 2^EV)
        return Math.sqrt(shutterSpeed * Math.pow(2.0, ev));
    }

    /**
     * Verifica la formula EV: dato N e t, calcola EV.
     * Formula: EV = log₂(N² / t)
     *
     * @param fNumber      f-number
     * @param shutterSpeed Tempo di esposizione in secondi
     * @return             EV calcolato
     */
    public static double fNumberAndShutterSpeedToEV(double fNumber, double shutterSpeed) {
        return log2((fNumber * fNumber) / shutterSpeed);
    }

    /**
     * Trova il valore standard di f-number più vicino al valore calcolato.
     *
     * @param fNumber  f-number calcolato
     * @return         f-number standard più vicino
     */
    public static double nearestStandardFStop(double fNumber) {
        double nearest = STANDARD_F_STOPS[0];
        // Confronto in scala logaritmica per coerenza con la scala fotografica
        double minDiff = Math.abs(Math.log(fNumber) - Math.log(nearest));
        for (double f : STANDARD_F_STOPS) {
            double diff = Math.abs(Math.log(fNumber) - Math.log(f));
            if (diff < minDiff) {
                minDiff = diff;
                nearest = f;
            }
        }
        return nearest;
    }

    /**
     * Trova il tempo di esposizione standard più vicino al valore calcolato.
     *
     * @param shutterSpeed  Tempo calcolato in secondi
     * @return              Tempo standard più vicino in secondi
     */
    public static double nearestStandardShutterSpeed(double shutterSpeed) {
        double nearest = STANDARD_SHUTTER_SPEEDS[0];
        // Confronto in scala logaritmica per coerenza con la scala fotografica
        double minDiff = Math.abs(Math.log(shutterSpeed) - Math.log(nearest));
        for (double t : STANDARD_SHUTTER_SPEEDS) {
            double diff = Math.abs(Math.log(shutterSpeed) - Math.log(t));
            if (diff < minDiff) {
                minDiff = diff;
                nearest = t;
            }
        }
        return nearest;
    }

    /**
     * Formatta un tempo di esposizione in secondi come stringa leggibile.
     * Esempi: 1/125, 1/60, 2", 1/2, 1/1.3, 30"
     *
     * @param shutterSpeed  Tempo in secondi
     * @return              Stringa formattata
     */
    public static String formatShutterSpeed(double shutterSpeed) {
        if (shutterSpeed >= 1.0) {
            // Tempi >= 1 secondo: mostra come intero o decimale con apice
            if (shutterSpeed == Math.floor(shutterSpeed)) {
                return (int) shutterSpeed + "\"";
            } else {
                return String.format(Locale.US, "%.1f\"", shutterSpeed);
            }
        } else {
            // Tempi < 1 secondo: mostra come frazione 1/N
            double denominator = 1.0 / shutterSpeed;
            long rounded = Math.round(denominator);
            // Se arrotondare a intero altera il denominatore di oltre il 3%
            // (es. 1/1.3, 1/1.6, 1/2.5 della serie standard), mantieni un decimale
            // per non falsare il tempo mostrato né duplicare le etichette
            if (Math.abs(denominator - rounded) / denominator > 0.03) {
                return String.format(Locale.US, "1/%.1f", denominator);
            }
            return "1/" + rounded;
        }
    }

    /**
     * Formatta un f-number come stringa fotografica standard.
     * Esempi: f/1.4, f/2.8, f/8, f/11
     *
     * @param fNumber  f-number
     * @return         Stringa formattata
     */
    public static String formatFNumber(double fNumber) {
        if (fNumber == Math.floor(fNumber)) {
            return "f/" + (int) fNumber;
        } else {
            return String.format(Locale.US, "f/%.1f", fNumber);
        }
    }

    /**
     * Genera una lista di combinazioni equivalenti (f-number, tempo) per un dato EV.
     * Tutte le combinazioni producono la stessa esposizione.
     *
     * @param ev  Valore di esposizione
     * @return    Lista di array double[2] dove [0]=f-number, [1]=tempo in secondi
     */
    public static List<double[]> getEquivalentCombinations(double ev) {
        List<double[]> combinations = new ArrayList<>();
        for (double fStop : STANDARD_F_STOPS) {
            double t = evAndFNumberToShutterSpeed(ev, fStop);
            // Filtra tempi fuori range fotografico utile (da 1/8000 a 30 secondi)
            if (t >= 1.0 / 8000.0 && t <= 30.0) {
                combinations.add(new double[]{fStop, t});
            }
        }
        return combinations;
    }

    /**
     * Calcola il logaritmo in base 2.
     *
     * @param x  Valore (deve essere > 0)
     * @return   log₂(x)
     */
    public static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    /**
     * Classe che rappresenta il risultato completo di una conversione lux → triade.
     */
    public static class ExposureResult {
        public final double lux;
        public final int iso;
        public final double ev100;
        public final double evISO;
        public final double fNumber;
        public final double shutterSpeed;
        public final double fNumberStandard;
        public final double shutterSpeedStandard;

        public ExposureResult(double lux, int iso, double ev100, double evISO,
                              double fNumber, double shutterSpeed,
                              double fNumberStandard, double shutterSpeedStandard) {
            this.lux = lux;
            this.iso = iso;
            this.ev100 = ev100;
            this.evISO = evISO;
            this.fNumber = fNumber;
            this.shutterSpeed = shutterSpeed;
            this.fNumberStandard = fNumberStandard;
            this.shutterSpeedStandard = shutterSpeedStandard;
        }
    }

    /**
     * Calcola la triade esposimetrica completa a partire da lux, ISO e f-number.
     * Il tempo di esposizione viene derivato da EV e f-number.
     *
     * @param lux      Illuminamento in lux
     * @param iso      Sensibilità ISO
     * @param fNumber  f-number scelto
     * @return         ExposureResult con tutti i valori calcolati
     */
    public static ExposureResult calculate(double lux, int iso, double fNumber) {
        double ev100 = luxToEV100(lux);
        double evISO = adjustEVForISO(ev100, iso);
        double shutterSpeed = evAndFNumberToShutterSpeed(evISO, fNumber);
        double fNumberStd = nearestStandardFStop(fNumber);
        double shutterSpeedStd = nearestStandardShutterSpeed(shutterSpeed);
        return new ExposureResult(lux, iso, ev100, evISO,
                fNumber, shutterSpeed, fNumberStd, shutterSpeedStd);
    }

    /**
     * Calcola la triade esposimetrica completa a partire da lux, ISO e tempo di esposizione.
     * Il f-number viene derivato da EV e tempo.
     *
     * @param lux          Illuminamento in lux
     * @param iso          Sensibilità ISO
     * @param shutterSpeed Tempo di esposizione in secondi
     * @return             ExposureResult con tutti i valori calcolati
     */
    public static ExposureResult calculateFromShutter(double lux, int iso, double shutterSpeed) {
        double ev100 = luxToEV100(lux);
        double evISO = adjustEVForISO(ev100, iso);
        double fNumber = evAndShutterSpeedToFNumber(evISO, shutterSpeed);
        double fNumberStd = nearestStandardFStop(fNumber);
        double shutterSpeedStd = nearestStandardShutterSpeed(shutterSpeed);
        return new ExposureResult(lux, iso, ev100, evISO,
                fNumber, shutterSpeed, fNumberStd, shutterSpeedStd);
    }
}
