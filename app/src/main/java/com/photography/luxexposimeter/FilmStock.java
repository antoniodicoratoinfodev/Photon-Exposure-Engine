package com.photography.luxexposimeter;

/**
 * FilmStock — dati di reciprocità (reciprocity failure) per pellicola.
 *
 * Ogni voce descrive come correggere il tempo misurato dall'esposimetro (Tm)
 * per ottenere il tempo di scatto effettivo (Tc) che compensa il difetto di
 * reciprocità (effetto Schwarzschild) alle lunghe esposizioni.
 *
 * Modelli supportati (vedi ReciprocityCalculator per la matematica):
 *
 *   NONE        — nessuna correzione (sensore digitale, o pellicola senza
 *                 failure documentato nel range utile, es. Provia 100F ≤ 128 s).
 *   POWER       — modello Ilford/HARMAN ufficiale ("Film Reciprocity Failure
 *                 Compensation", dic 2023):  Tc = Tm^P  per Tm > 1 s.
 *   TABLE_TIME  — tabella (Tm → Tc) dal datasheet, interpolata in scala
 *                 log-log (Kodak F-4016/F-4017/F-4043, Foma).
 *   TABLE_STOPS — tabella (t → +stop) dal datasheet, dove t è il tempo di
 *                 scatto effettivo; il tempo corretto risolve il punto fisso
 *                 t = Tm · 2^stops(t) (Fuji Acros II / Velvia 50, Kodak E100).
 *
 * Semantica di dataLimit (soglia oltre cui i dati del produttore finiscono e
 * il valore mostrato è estrapolato):
 *   TABLE_TIME / NONE  → riferito al tempo MISURATO (Tm)
 *   TABLE_STOPS        → riferito al tempo di scatto EFFETTIVO (Tc)
 *   POWER              → nessun limite pubblicato (infinito)
 */
public enum FilmStock {

    // ─── Digitale ─────────────────────────────────────────────────────────────
    DIGITAL("Digital sensor", Model.NONE, 0,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Linear response: no reciprocity correction."),

    // ─── Ilford / HARMAN — Tc = Tm^P (Technical Information, Dec 2023) ───────
    ILFORD_PAN_F_PLUS("Ilford Pan F+ 50", Model.POWER, 1.33,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.33 (no correction ≤ 1 s)."),
    ILFORD_FP4_PLUS("Ilford FP4+ 125", Model.POWER, 1.26,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.26 (no correction ≤ 1 s)."),
    ILFORD_HP5_PLUS("Ilford HP5+ 400", Model.POWER, 1.31,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.31 (no correction ≤ 1 s)."),
    ILFORD_DELTA_100("Ilford Delta 100", Model.POWER, 1.26,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.26 (no correction ≤ 1 s)."),
    ILFORD_DELTA_400("Ilford Delta 400", Model.POWER, 1.41,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.41 (no correction ≤ 1 s)."),
    ILFORD_DELTA_3200("Ilford Delta 3200", Model.POWER, 1.33,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.33 (no correction ≤ 1 s)."),
    ILFORD_SFX_200("Ilford SFX 200", Model.POWER, 1.43,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.43 (no correction ≤ 1 s)."),
    ILFORD_XP2_SUPER("Ilford XP2 Super 400", Model.POWER, 1.31,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.31 (no correction ≤ 1 s)."),
    ILFORD_ORTHO_PLUS("Ilford Ortho+ 80", Model.POWER, 1.25,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.25 (no correction ≤ 1 s)."),
    KENTMERE_100("Kentmere Pan 100", Model.POWER, 1.26,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.26 (no correction ≤ 1 s)."),
    KENTMERE_400("Kentmere Pan 400", Model.POWER, 1.30,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Ilford/HARMAN: Tc = Tm^1.30 (no correction ≤ 1 s)."),

    // ─── Kodak — tabelle datasheet (interpolazione log-log) ──────────────────
    // F-4017: 1 s→2 s (dev −10%), 10 s→50 s (dev −20%), 100 s→1200 s (dev −30%)
    KODAK_TRI_X("Kodak Tri-X 320/400", Model.TABLE_TIME, 0,
            new double[][]{{0.1, 0.1}, {1, 2}, {10, 50}, {100, 1200}},
            0.1, 100.0,
            "Kodak F-4017. Also reduce development −10% / −20% / −30% "
                    + "at 2 s / 50 s / 1200 s to control contrast."),
    // F-4016: 1 s→+1/3 stop (≈1.26 s), 10 s→15 s, 100 s→200 s
    KODAK_TMAX_100("Kodak T-Max 100", Model.TABLE_TIME, 0,
            new double[][]{{0.1, 0.1}, {1, 1.26}, {10, 15}, {100, 200}},
            0.1, 100.0,
            "Kodak F-4016 datasheet table."),
    // F-4043: ≤1 s nessuna, 10 s→+1/3 stop (≈12.6 s), 100 s→300 s
    KODAK_TMAX_400("Kodak T-Max 400", Model.TABLE_TIME, 0,
            new double[][]{{1, 1}, {10, 12.6}, {100, 300}},
            1.0, 100.0,
            "Kodak F-4043 datasheet table."),
    // E100: nessuna ≤10 s; ~+1/2 stop a 20–40 s (indicazione Kodak, non a tabella)
    KODAK_E100("Kodak Ektachrome E100", Model.TABLE_STOPS, 0,
            new double[][]{{10, 0}, {20, 0.5}, {40, 0.5}},
            10.0, 40.0,
            "Approximate — Kodak guidance: ~+1/2 stop for 20–40 s; "
                    + "test beyond 40 s."),

    // ─── Fujifilm ─────────────────────────────────────────────────────────────
    // AF3-0258E: nessuna < 120 s; 120–1000 s: +1/2 stop; > 1000 s: fuori dati
    FUJI_ACROS_II("Fujifilm Neopan 100 Acros II", Model.TABLE_STOPS, 0,
            new double[][]{{120, 0.5}, {1000, 0.5}},
            120.0, 1000.0,
            "Datasheet AF3-0258E: +1/2 stop for 120–1000 s; no data beyond."),
    // Datasheet: nessuna correzione da 1/4000 a 128 s; oltre: fuori dati
    FUJI_PROVIA_100F("Fujifilm Provia 100F", Model.NONE, 0,
            null, 128.0, 128.0,
            "No correction up to 128 s (datasheet); beyond: run test exposures."),
    // RVP50 Data Guide: 4 s→+1/3 (CC 5M), 8 s→+1/2 (7.5M), 16 s→+2/3 (10M),
    // 32 s→+1 stop (12.5M); ≥64 s "not recommended"
    FUJI_VELVIA_50("Fujifilm Velvia 50", Model.TABLE_STOPS, 0,
            new double[][]{{1, 0}, {4, 1.0 / 3.0}, {8, 0.5}, {16, 2.0 / 3.0}, {32, 1}},
            1.0, 32.0,
            "Datasheet: add CC magenta filter (5M @4 s → 12.5M @32 s); "
                    + "≥64 s not recommended."),

    // ─── Foma — tabella "Schwarzschild effect" dei datasheet ─────────────────
    // Fomapan 100: 1 s→×2, 10 s→×8, 100 s→×16
    FOMAPAN_100("Fomapan 100 Classic", Model.TABLE_TIME, 0,
            new double[][]{{0.5, 0.5}, {1, 2}, {10, 80}, {100, 1600}},
            0.5, 100.0,
            "Foma datasheet (Schwarzschild effect table)."),
    // Fomapan 400: 1 s→×1.5, 10 s→×6, 100 s→×8
    FOMAPAN_400("Fomapan 400 Action", Model.TABLE_TIME, 0,
            new double[][]{{0.5, 0.5}, {1, 1.5}, {10, 60}, {100, 800}},
            0.5, 100.0,
            "Foma datasheet (Schwarzschild effect table)."),

    // ─── Generica ─────────────────────────────────────────────────────────────
    GENERIC_BW("Generic B&W film (P = 1.30)", Model.POWER, 1.30,
            null, 1.0, Double.POSITIVE_INFINITY,
            "Estimate for films without published data — test your film.");

    /** Tipo di modello matematico di correzione. */
    public enum Model {NONE, POWER, TABLE_TIME, TABLE_STOPS}

    public final String displayName;
    public final Model model;
    /** Esponente P del modello POWER (Tc = Tm^P). */
    public final double power;
    /**
     * Punti della tabella, ordinati per tempo crescente.
     * TABLE_TIME:  righe {Tm, Tc} in secondi.
     * TABLE_STOPS: righe {t, stop} con t = tempo di scatto effettivo.
     */
    public final double[][] table;
    /** Sotto questa soglia (tempo misurato, s) non serve alcuna correzione. */
    public final double noCorrectionBelow;
    /** Limite dei dati pubblicati (vedi semantica in testa alla classe). */
    public final double dataLimit;
    /** Nota informativa mostrata nella UI (fonte, filtri CC, sviluppo…). */
    public final String note;

    FilmStock(String displayName, Model model, double power, double[][] table,
              double noCorrectionBelow, double dataLimit, String note) {
        this.displayName = displayName;
        this.model = model;
        this.power = power;
        this.table = table;
        this.noCorrectionBelow = noCorrectionBelow;
        this.dataLimit = dataLimit;
        this.note = note;
    }

    /** Pellicole selezionabili nel tab Analog (tutte tranne DIGITAL). */
    public static FilmStock[] analogValues() {
        FilmStock[] all = values();
        FilmStock[] out = new FilmStock[all.length - 1];
        int i = 0;
        for (FilmStock f : all) {
            if (f != DIGITAL) out[i++] = f;
        }
        return out;
    }
}
