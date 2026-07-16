package com.photography.luxexposimeter;

/**
 * ReciprocityCalculator
 * <p>
 * Matematica pura (senza dipendenze Android) per la correzione del difetto
 * di reciprocità (reciprocity failure / effetto Schwarzschild) delle pellicole.
 * <p>
 * Fondamento: la legge di reciprocità H = E·t vale sulla pellicola solo nel
 * range medio (~1/1000 s – 1 s). Alle lunghe esposizioni la sensibilità
 * effettiva cala (Schwarzschild, 1899: risposta ∝ E·t^p con p < 1) e il tempo
 * misurato dall'esposimetro (Tm) va allungato al tempo corretto (Tc).
 * <p>
 * Modelli implementati (parametri in FilmStock):
 * <p>
 *   1. POWER (Ilford/HARMAN, "Film Reciprocity Failure Compensation", 12/2023):
 *          Tc = Tm^P        per Tm > 1 s   (nessuna correzione per Tm ≤ 1 s)
 *      Esempio ufficiale: HP5+ a Tm = 10 s → Tc = 10^1.31 = 20.4 s.
 * <p>
 *   2. TABLE_TIME (Kodak F-4016/F-4017/F-4043, Foma):
 *      punti (Tm → Tc) del datasheet interpolati LINEARMENTE IN SCALA LOG-LOG
 *      (le curve pubblicate sono quasi rette in log-log: per Tri-X le pendenze
 *      tra i punti sono 1.40 e 1.38). Oltre l'ultimo punto si estrapola con la
 *      pendenza dell'ultimo segmento (vedi isBeyondData per l'avviso).
 * <p>
 *   3. TABLE_STOPS (Fuji Acros II / Velvia 50, Kodak E100):
 *      il datasheet dà una compensazione in stop s(t) riferita al tempo di
 *      scatto EFFETTIVO t. Il tempo corretto risolve quindi il punto fisso
 *          Tc = Tm · 2^s(Tc)
 *      (iterazione; conferma: T-Max 10 s → +1/2 stop = 14.1 s "naive", ma la
 *      colonna tempo del datasheet Kodak dice 15 s: il tempo più lungo subisce
 *      a sua volta più failure). L'inversa è in forma chiusa:
 *          Tm = t / 2^s(t).
 * <p>
 * Il failure ad alta intensità (tempi < 1/10.000 s) è fuori dal range
 * dell'app (minimo 1/8000 s) e non è modellato.
 */
public final class ReciprocityCalculator {

    private ReciprocityCalculator() {
    }

    /**
     * Tempo di scatto corretto per la reciprocità.
     *
     * @param film    Pellicola (o DIGITAL per nessuna correzione)
     * @param metered Tempo misurato dall'esposimetro in secondi (> 0)
     * @return        Tempo corretto in secondi (sempre ≥ metered)
     */
    public static double correctedTime(FilmStock film, double metered) {
        if (metered <= 0) {
            throw new IllegalArgumentException("Il tempo misurato deve essere positivo.");
        }
        if (metered <= film.noCorrectionBelow) {
            return metered;
        }
        return switch (film.model) {
            case NONE -> metered;
            case POWER -> Math.pow(metered, film.power);
            case TABLE_TIME -> logLogInterpolate(film.table, metered);
            case TABLE_STOPS -> fixedPointCorrected(film, metered);
        };
    }

    /**
     * Inversa: dato il tempo di scatto EFFETTIVO scelto dall'utente (modalità
     * a priorità di tempi), restituisce il tempo misurato equivalente Tm che
     * l'esposimetro deve "vedere" soddisfatto. È sempre ≤ actual: la pellicola
     * a tempi lunghi rende meno, quindi il diaframma andrà aperto di
     * log₂(actual/Tm) stop.
     *
     * @param film   Pellicola
     * @param actual Tempo di scatto effettivo in secondi (> 0)
     * @return       Tempo misurato equivalente in secondi
     */
    public static double meteredEquivalentTime(FilmStock film, double actual) {
        if (actual <= 0) {
            throw new IllegalArgumentException("Il tempo effettivo deve essere positivo.");
        }
        if (actual <= film.noCorrectionBelow) {
            return actual;
        }
        return switch (film.model) {
            case NONE -> actual;
            case POWER -> Math.pow(actual, 1.0 / film.power);
            // Tabella invertita: interpolazione log-log su (Tc → Tm)
            case TABLE_TIME -> logLogInterpolate(film.table, actual, 1, 0);
            // s(t) è indicizzata dal tempo effettivo: inversa in forma chiusa
            case TABLE_STOPS -> actual / Math.pow(2.0, stopsAt(film.table, actual));
        };
    }

    /**
     * Compensazione in stop applicata dalla correzione:
     * log₂(Tc / Tm). Vale 0 quando non serve correzione.
     */
    public static double compensationStops(FilmStock film, double metered) {
        return ExposureCalculator.log2(correctedTime(film, metered) / metered);
    }

    /**
     * true se il calcolo cade oltre i dati pubblicati dal produttore
     * (valore estrapolato: mostrare un avviso).
     * TABLE_TIME / NONE: confronto sul tempo misurato;
     * TABLE_STOPS: confronto sul tempo effettivo corretto.
     */
    public static boolean isBeyondData(FilmStock film, double metered) {
        return isBeyondData(film, metered, Double.NaN);
    }

    /**
     * Variante per i chiamanti che hanno già calcolato il tempo corretto.
     * Evita di ripetere il punto fisso dei modelli TABLE_STOPS.
     */
    static boolean isBeyondData(FilmStock film, double metered, double corrected) {
        return switch (film.model) {
            case POWER -> false;
            case TABLE_STOPS -> {
                double actual = Double.isNaN(corrected)
                        ? correctedTime(film, metered)
                        : corrected;
                yield actual > film.dataLimit;
            }
            case NONE, TABLE_TIME -> metered > film.dataLimit;
        };
    }

    // ─── Interni ──────────────────────────────────────────────────────────────

    /**
     * Interpolazione lineare in scala log-log su punti {x, y} ordinati per x
     * crescente. Sotto il primo punto e oltre l'ultimo si prolunga la pendenza
     * del segmento più vicino.
     */
    static double logLogInterpolate(double[][] points, double x) {
        return logLogInterpolate(points, x, 0, 1);
    }

    /** Variante che legge direttamente le colonne indicate, senza copiare la tabella. */
    private static double logLogInterpolate(double[][] points, double x,
                                             int xColumn, int yColumn) {
        int n = points.length;
        int i = 1;
        while (i < n - 1 && x > points[i][xColumn]) {
            i++;
        }
        double lx0 = Math.log(points[i - 1][xColumn]);
        double ly0 = Math.log(points[i - 1][yColumn]);
        double lx1 = Math.log(points[i][xColumn]);
        double ly1 = Math.log(points[i][yColumn]);
        double t = (Math.log(x) - lx0) / (lx1 - lx0);
        return Math.exp(ly0 + t * (ly1 - ly0));
    }

    /**
     * Compensazione in stop s(t) da tabella {t, stop}, lineare in ln(t).
     * Sotto il primo punto: 0 stop (nessuna correzione — per le tabelle a
     * gradino come Acros II il salto al primo punto è voluto, fedele al
     * datasheet). Oltre l'ultimo punto: pendenza dell'ultimo segmento
     * (piatta se i due ultimi valori coincidono).
     */
    static double stopsAt(double[][] points, double t) {
        int n = points.length;
        if (t < points[0][0]) {
            return 0.0;
        }
        double logT = Math.log(t);
        if (t <= points[n - 1][0]) {
            int i = 1;
            while (i < n - 1 && t > points[i][0]) {
                i++;
            }
            double f = (logT - Math.log(points[i - 1][0]))
                    / (Math.log(points[i][0]) - Math.log(points[i - 1][0]));
            return points[i - 1][1] + f * (points[i][1] - points[i - 1][1]);
        }
        // Estrapolazione oltre l'ultimo punto con la pendenza dell'ultimo segmento
        double slope = (points[n - 1][1] - points[n - 2][1])
                / (Math.log(points[n - 1][0]) - Math.log(points[n - 2][0]));
        return points[n - 1][1] + slope * (logT - Math.log(points[n - 1][0]));
    }

    /**
     * Punto fisso Tc = Tm · 2^s(Tc) per i modelli TABLE_STOPS.
     * s cresce lentamente (logaritmicamente): l'iterazione converge in
     * poche decine di passi.
     */
    private static double fixedPointCorrected(FilmStock film, double metered) {
        double t = metered;
        for (int k = 0; k < 100; k++) {
            double next = metered * Math.pow(2.0, stopsAt(film.table, t));
            if (Math.abs(next - t) <= 1e-9 * Math.max(1.0, t)) {
                return next;
            }
            t = next;
        }
        return t;
    }

}
