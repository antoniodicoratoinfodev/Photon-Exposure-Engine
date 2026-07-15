package com.photography.luxexposimeter;

/**
 * ReciprocityCalculator
 *
 * Matematica pura (senza dipendenze Android) per la correzione del difetto
 * di reciprocità (reciprocity failure / effetto Schwarzschild) delle pellicole.
 *
 * Fondamento: la legge di reciprocità H = E·t vale sulla pellicola solo nel
 * range medio (~1/1000 s – 1 s). Alle lunghe esposizioni la sensibilità
 * effettiva cala (Schwarzschild, 1899: risposta ∝ E·t^p con p < 1) e il tempo
 * misurato dall'esposimetro (Tm) va allungato al tempo corretto (Tc).
 *
 * Modelli implementati (parametri in FilmStock):
 *
 *   1. POWER (Ilford/HARMAN, "Film Reciprocity Failure Compensation", 12/2023):
 *          Tc = Tm^P        per Tm > 1 s   (nessuna correzione per Tm ≤ 1 s)
 *      Esempio ufficiale: HP5+ a Tm = 10 s → Tc = 10^1.31 = 20.4 s.
 *
 *   2. TABLE_TIME (Kodak F-4016/F-4017/F-4043, Foma):
 *      punti (Tm → Tc) del datasheet interpolati LINEARMENTE IN SCALA LOG-LOG
 *      (le curve pubblicate sono quasi rette in log-log: per Tri-X le pendenze
 *      tra i punti sono 1.40 e 1.38). Oltre l'ultimo punto si estrapola con la
 *      pendenza dell'ultimo segmento (vedi isBeyondData per l'avviso).
 *
 *   3. TABLE_STOPS (Fuji Acros II / Velvia 50, Kodak E100):
 *      il datasheet dà una compensazione in stop s(t) riferita al tempo di
 *      scatto EFFETTIVO t. Il tempo corretto risolve quindi il punto fisso
 *          Tc = Tm · 2^s(Tc)
 *      (iterazione; conferma: T-Max 10 s → +1/2 stop = 14.1 s "naive", ma la
 *      colonna tempo del datasheet Kodak dice 15 s: il tempo più lungo subisce
 *      a sua volta più failure). L'inversa è in forma chiusa:
 *          Tm = t / 2^s(t).
 *
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
        switch (film.model) {
            case NONE:
                return metered;
            case POWER:
                return Math.pow(metered, film.power);
            case TABLE_TIME:
                return logLogInterpolate(film.table, metered);
            case TABLE_STOPS:
                return fixedPointCorrected(film, metered);
            default:
                return metered;
        }
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
        switch (film.model) {
            case NONE:
                return actual;
            case POWER:
                return Math.pow(actual, 1.0 / film.power);
            case TABLE_TIME:
                // Tabella invertita: interpolazione log-log su (Tc → Tm)
                return logLogInterpolate(swapColumns(film.table), actual);
            case TABLE_STOPS:
                // s(t) è indicizzata dal tempo effettivo: inversa in forma chiusa
                return actual / Math.pow(2.0, stopsAt(film.table, actual));
            default:
                return actual;
        }
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
        switch (film.model) {
            case POWER:
                return false;
            case TABLE_STOPS:
                return correctedTime(film, metered) > film.dataLimit;
            case NONE:
            case TABLE_TIME:
            default:
                return metered > film.dataLimit;
        }
    }

    // ─── Interni ──────────────────────────────────────────────────────────────

    /**
     * Interpolazione lineare in scala log-log su punti {x, y} ordinati per x
     * crescente. Sotto il primo punto e oltre l'ultimo si prolunga la pendenza
     * del segmento più vicino.
     */
    static double logLogInterpolate(double[][] points, double x) {
        int n = points.length;
        int i = 1;
        while (i < n - 1 && x > points[i][0]) {
            i++;
        }
        double lx0 = Math.log(points[i - 1][0]);
        double ly0 = Math.log(points[i - 1][1]);
        double lx1 = Math.log(points[i][0]);
        double ly1 = Math.log(points[i][1]);
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
        if (t <= points[n - 1][0]) {
            int i = 1;
            while (i < n - 1 && t > points[i][0]) {
                i++;
            }
            double f = (Math.log(t) - Math.log(points[i - 1][0]))
                    / (Math.log(points[i][0]) - Math.log(points[i - 1][0]));
            return points[i - 1][1] + f * (points[i][1] - points[i - 1][1]);
        }
        // Estrapolazione oltre l'ultimo punto con la pendenza dell'ultimo segmento
        double slope = (points[n - 1][1] - points[n - 2][1])
                / (Math.log(points[n - 1][0]) - Math.log(points[n - 2][0]));
        return points[n - 1][1] + slope * (Math.log(t) - Math.log(points[n - 1][0]));
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

    /** Scambia le colonne di una tabella {x, y} → {y, x} (per l'inversa). */
    private static double[][] swapColumns(double[][] points) {
        double[][] out = new double[points.length][2];
        for (int i = 0; i < points.length; i++) {
            out[i][0] = points[i][1];
            out[i][1] = points[i][0];
        }
        return out;
    }
}
