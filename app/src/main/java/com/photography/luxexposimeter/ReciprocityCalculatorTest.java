package com.photography.luxexposimeter;

/**
 * Test unitario puro (senza dipendenze Android) per la matematica di
 * ReciprocityCalculator / FilmStock.
 *
 * Eseguibile con:
 *   javac ExposureCalculator.java FilmStock.java ReciprocityCalculator.java ReciprocityCalculatorTest.java
 *   java ReciprocityCalculatorTest
 */
public class ReciprocityCalculatorTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Test ReciprocityCalculator ===\n");

        testDigitalIdentity();
        testIlfordPower();
        testKodakTables();
        testFomaTables();
        testAcrosII();
        testVelvia50();
        testE100AndProvia();
        testInverseRoundTrip();
        testMonotonicityAndBounds();
        testBeyondData();
        testLongTimeFormatting();

        System.out.println("\n=== Risultato: " + passed + " passati, " + failed + " falliti ===");
        if (failed > 0) System.exit(1);
    }

    // ─── Test 1: digitale = identità ──────────────────────────────────────────
    static void testDigitalIdentity() {
        System.out.println("--- Test 1: DIGITAL (nessuna correzione) ---");
        double[] times = {0.001, 0.5, 1, 10, 100, 1000};
        for (double t : times) {
            assertApprox("DIGITAL " + t + "s invariato",
                    ReciprocityCalculator.correctedTime(FilmStock.DIGITAL, t), t, 1e-12);
        }
        assertTrue("DIGITAL mai oltre i dati",
                !ReciprocityCalculator.isBeyondData(FilmStock.DIGITAL, 1e6));
    }

    // ─── Test 2: modello POWER Ilford (Tc = Tm^P) ────────────────────────────
    static void testIlfordPower() {
        System.out.println("\n--- Test 2: Ilford Tc = Tm^P ---");

        // Esempio ufficiale HARMAN (dic 2023): HP5+ 10 s → 10^1.31 = 20.4 s
        assertApprox("HP5+ 10s → 20.4s (esempio ufficiale)",
                ReciprocityCalculator.correctedTime(FilmStock.ILFORD_HP5_PLUS, 10.0),
                20.417, 0.05);

        // Nessuna correzione ≤ 1 s
        assertApprox("HP5+ 1s → 1s (soglia)",
                ReciprocityCalculator.correctedTime(FilmStock.ILFORD_HP5_PLUS, 1.0), 1.0, 1e-12);
        assertApprox("HP5+ 0.5s → 0.5s (sotto soglia)",
                ReciprocityCalculator.correctedTime(FilmStock.ILFORD_HP5_PLUS, 0.5), 0.5, 1e-12);

        // Delta 400 (P più alto della gamma): 30 s → 30^1.41
        assertApprox("Delta 400 30s → 30^1.41",
                ReciprocityCalculator.correctedTime(FilmStock.ILFORD_DELTA_400, 30.0),
                Math.pow(30.0, 1.41), 1e-9);

        // FP4+ e Delta 100 condividono P = 1.26
        assertApprox("FP4+ = Delta 100 a 20s",
                ReciprocityCalculator.correctedTime(FilmStock.ILFORD_FP4_PLUS, 20.0),
                ReciprocityCalculator.correctedTime(FilmStock.ILFORD_DELTA_100, 20.0), 1e-12);
    }

    // ─── Test 3: tabelle Kodak (log-log) ─────────────────────────────────────
    static void testKodakTables() {
        System.out.println("\n--- Test 3: tabelle Kodak F-4017 / F-4016 / F-4043 ---");

        // Tri-X: punti esatti del datasheet
        assertApprox("Tri-X 1s → 2s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TRI_X, 1.0), 2.0, 1e-9);
        assertApprox("Tri-X 10s → 50s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TRI_X, 10.0), 50.0, 1e-9);
        assertApprox("Tri-X 100s → 1200s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TRI_X, 100.0), 1200.0, 1e-6);

        // Interpolazione log-log tra 10 e 100 s (verifica indipendente)
        double f = (Math.log(30) - Math.log(10)) / (Math.log(100) - Math.log(10));
        double expected30 = Math.exp(Math.log(50) + f * (Math.log(1200) - Math.log(50)));
        assertApprox("Tri-X 30s → interpolato log-log (≈227.7s)",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TRI_X, 30.0), expected30, 0.001);

        // Compensazione in stop: Tri-X 10 s → log2(50/10) = 2.32 stop
        assertApprox("Tri-X 10s → +2.32 stop",
                ReciprocityCalculator.compensationStops(FilmStock.KODAK_TRI_X, 10.0),
                ExposureCalculator.log2(5.0), 1e-9);

        // T-Max 100
        assertApprox("T-Max 100 10s → 15s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TMAX_100, 10.0), 15.0, 1e-9);
        assertApprox("T-Max 100 100s → 200s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TMAX_100, 100.0), 200.0, 1e-6);

        // T-Max 400: nessuna correzione a 1 s, poi tabella
        assertApprox("T-Max 400 1s → 1s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TMAX_400, 1.0), 1.0, 1e-12);
        assertApprox("T-Max 400 10s → 12.6s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TMAX_400, 10.0), 12.6, 1e-9);
        assertApprox("T-Max 400 100s → 300s",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_TMAX_400, 100.0), 300.0, 1e-6);
    }

    // ─── Test 4: tabelle Foma (effetto Schwarzschild) ────────────────────────
    static void testFomaTables() {
        System.out.println("\n--- Test 4: tabelle Foma ---");

        assertApprox("Fomapan 100 1s → 2s",
                ReciprocityCalculator.correctedTime(FilmStock.FOMAPAN_100, 1.0), 2.0, 1e-9);
        assertApprox("Fomapan 100 10s → 80s",
                ReciprocityCalculator.correctedTime(FilmStock.FOMAPAN_100, 10.0), 80.0, 1e-6);
        assertApprox("Fomapan 100 100s → 1600s",
                ReciprocityCalculator.correctedTime(FilmStock.FOMAPAN_100, 100.0), 1600.0, 1e-6);

        assertApprox("Fomapan 400 1s → 1.5s",
                ReciprocityCalculator.correctedTime(FilmStock.FOMAPAN_400, 1.0), 1.5, 1e-9);
        assertApprox("Fomapan 400 10s → 60s",
                ReciprocityCalculator.correctedTime(FilmStock.FOMAPAN_400, 10.0), 60.0, 1e-6);
        assertApprox("Fomapan 400 100s → 800s",
                ReciprocityCalculator.correctedTime(FilmStock.FOMAPAN_400, 100.0), 800.0, 1e-6);
    }

    // ─── Test 5: Acros II (gradino +1/2 stop a 120–1000 s) ───────────────────
    static void testAcrosII() {
        System.out.println("\n--- Test 5: Fujifilm Acros II ---");

        assertApprox("Acros II 60s → 60s (nessuna correzione <120s)",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_ACROS_II, 60.0), 60.0, 1e-12);
        assertApprox("Acros II 119s → 119s",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_ACROS_II, 119.0), 119.0, 1e-12);
        assertApprox("Acros II 130s → 130·√2 ≈ 183.8s (+1/2 stop)",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_ACROS_II, 130.0),
                130.0 * Math.sqrt(2.0), 1e-6);
        assertApprox("Acros II 500s → 500·√2 ≈ 707.1s",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_ACROS_II, 500.0),
                500.0 * Math.sqrt(2.0), 1e-6);
    }

    // ─── Test 6: Velvia 50 (stop → punto fisso) ──────────────────────────────
    static void testVelvia50() {
        System.out.println("\n--- Test 6: Fujifilm Velvia 50 ---");

        assertApprox("Velvia 50 1s → 1s",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_VELVIA_50, 1.0), 1.0, 1e-12);

        // Punto fisso: il tempo corretto deve soddisfare Tc = Tm · 2^s(Tc)
        double[] metered = {2, 4, 8, 16, 32};
        for (double tm : metered) {
            double tc = ReciprocityCalculator.correctedTime(FilmStock.FUJI_VELVIA_50, tm);
            double residual = Math.abs(tc - tm * Math.pow(2.0,
                    ReciprocityCalculator.stopsAt(FilmStock.FUJI_VELVIA_50.table, tc)));
            assertTrue(String.format("Velvia punto fisso a Tm=%.0fs (Tc=%.2fs, residuo=%.2e)",
                    tm, tc, residual), residual < 1e-6);
        }

        // Coerenza con i valori noti in letteratura: 4s → ~5s, 8s → ~12s
        double tc4 = ReciprocityCalculator.correctedTime(FilmStock.FUJI_VELVIA_50, 4.0);
        assertTrue(String.format("Velvia 4s → ~5s (ottenuto %.2fs)", tc4),
                tc4 > 4.9 && tc4 < 5.7);
        double tc8 = ReciprocityCalculator.correctedTime(FilmStock.FUJI_VELVIA_50, 8.0);
        assertTrue(String.format("Velvia 8s → ~12s (ottenuto %.2fs)", tc8),
                tc8 > 11.4 && tc8 < 12.6);
    }

    // ─── Test 7: E100 e Provia 100F ───────────────────────────────────────────
    static void testE100AndProvia() {
        System.out.println("\n--- Test 7: Ektachrome E100 / Provia 100F ---");

        assertApprox("E100 10s → 10s (nessuna correzione ≤10s)",
                ReciprocityCalculator.correctedTime(FilmStock.KODAK_E100, 10.0), 10.0, 1e-12);
        double tc30 = ReciprocityCalculator.correctedTime(FilmStock.KODAK_E100, 30.0);
        assertApprox("E100 30s → 30·√2 ≈ 42.4s (+1/2 stop)",
                tc30, 30.0 * Math.sqrt(2.0), 1e-6);

        assertApprox("Provia 100F 100s → 100s (nessuna correzione ≤128s)",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_PROVIA_100F, 100.0), 100.0, 1e-12);
        assertApprox("Provia 100F 128s → 128s",
                ReciprocityCalculator.correctedTime(FilmStock.FUJI_PROVIA_100F, 128.0), 128.0, 1e-12);
    }

    // ─── Test 8: inversa (modalità a priorità di tempi) ──────────────────────
    static void testInverseRoundTrip() {
        System.out.println("\n--- Test 8: inversa meteredEquivalentTime ---");

        double[] meteredTimes = {0.5, 2, 5, 10, 50};
        for (FilmStock film : FilmStock.values()) {
            for (double tm : meteredTimes) {
                double tc = ReciprocityCalculator.correctedTime(film, tm);
                double back = ReciprocityCalculator.meteredEquivalentTime(film, tc);
                assertApprox(String.format("Round-trip %s Tm=%.1fs", film.name(), tm),
                        back, tm, Math.max(1e-6, tm * 1e-6));
            }
        }

        // Caso concreto modalità B: Acros II esposto per 300s reali →
        // tempo equivalente misurato = 300/√2 ≈ 212.1s (apri +1/2 stop)
        assertApprox("Acros II attuale 300s → equivalente 212.1s",
                ReciprocityCalculator.meteredEquivalentTime(FilmStock.FUJI_ACROS_II, 300.0),
                300.0 / Math.sqrt(2.0), 1e-6);

        // HP5+ esposto 30s reali → equivalente 30^(1/1.31) ≈ 13.4s
        assertApprox("HP5+ attuale 30s → equivalente 13.4s",
                ReciprocityCalculator.meteredEquivalentTime(FilmStock.ILFORD_HP5_PLUS, 30.0),
                Math.pow(30.0, 1.0 / 1.31), 1e-9);
    }

    // ─── Test 9: monotonia e Tc ≥ Tm su griglia ──────────────────────────────
    static void testMonotonicityAndBounds() {
        System.out.println("\n--- Test 9: monotonia e Tc ≥ Tm ---");

        for (FilmStock film : FilmStock.values()) {
            boolean monotone = true;
            boolean bounded = true;
            double prev = -1;
            for (double t = 0.01; t <= 2000; t *= 1.07) {
                double tc = ReciprocityCalculator.correctedTime(film, t);
                if (tc < prev - 1e-9) monotone = false;
                if (tc < t - 1e-9) bounded = false;
                prev = tc;
            }
            assertTrue(film.name() + ": Tc monotono crescente", monotone);
            assertTrue(film.name() + ": Tc ≥ Tm ovunque", bounded);
        }
    }

    // ─── Test 10: avvisi oltre i dati del produttore ─────────────────────────
    static void testBeyondData() {
        System.out.println("\n--- Test 10: isBeyondData ---");

        assertTrue("Tri-X 100s entro i dati",
                !ReciprocityCalculator.isBeyondData(FilmStock.KODAK_TRI_X, 100.0));
        assertTrue("Tri-X 300s oltre i dati (estrapolato)",
                ReciprocityCalculator.isBeyondData(FilmStock.KODAK_TRI_X, 300.0));

        assertTrue("Acros II Tm=500s (Tc=707s ≤ 1000s) entro i dati",
                !ReciprocityCalculator.isBeyondData(FilmStock.FUJI_ACROS_II, 500.0));
        assertTrue("Acros II Tm=800s (Tc=1131s > 1000s) oltre i dati",
                ReciprocityCalculator.isBeyondData(FilmStock.FUJI_ACROS_II, 800.0));

        assertTrue("Velvia Tm=32s → Tc>32s oltre i dati",
                ReciprocityCalculator.isBeyondData(FilmStock.FUJI_VELVIA_50, 32.0));
        assertTrue("Velvia Tm=10s entro i dati",
                !ReciprocityCalculator.isBeyondData(FilmStock.FUJI_VELVIA_50, 10.0));

        assertTrue("Provia 100s entro i dati",
                !ReciprocityCalculator.isBeyondData(FilmStock.FUJI_PROVIA_100F, 100.0));
        assertTrue("Provia 200s oltre i dati",
                ReciprocityCalculator.isBeyondData(FilmStock.FUJI_PROVIA_100F, 200.0));

        assertTrue("Ilford (POWER) mai flag estrapolazione",
                !ReciprocityCalculator.isBeyondData(FilmStock.ILFORD_HP5_PLUS, 5000.0));
    }

    // ─── Test 11: formattazione tempi lunghi ─────────────────────────────────
    static void testLongTimeFormatting() {
        System.out.println("\n--- Test 11: formatShutterSpeed per tempi lunghi ---");

        assertEqual("90s → 1' 30\"",
                ExposureCalculator.formatShutterSpeed(90.0), "1' 30\"");
        assertEqual("1200s → 20' 0\"",
                ExposureCalculator.formatShutterSpeed(1200.0), "20' 0\"");
        assertEqual("5460s → 1h 31' 0\"",
                ExposureCalculator.formatShutterSpeed(5460.0), "1h 31' 0\"");
        assertEqual("30s resta 30\"",
                ExposureCalculator.formatShutterSpeed(30.0), "30\"");
        assertEqual("20.4s resta 20.4\"",
                ExposureCalculator.formatShutterSpeed(20.4), "20.4\"");
    }

    // ─── Utility di asserzione ────────────────────────────────────────────────
    static void assertApprox(String label, double actual, double expected, double tolerance) {
        if (Math.abs(actual - expected) <= tolerance) {
            pass(label + " → OK (" + actual + ")");
        } else {
            fail(label + " → FAIL (atteso=" + expected + ", ottenuto=" + actual + ")");
        }
    }

    static void assertEqual(String label, String actual, String expected) {
        if (actual.equals(expected)) {
            pass(label + " → OK");
        } else {
            fail(label + " → FAIL (atteso=\"" + expected + "\", ottenuto=\"" + actual + "\")");
        }
    }

    static void assertTrue(String label, boolean condition) {
        if (condition) {
            pass(label + " → OK");
        } else {
            fail(label + " → FAIL");
        }
    }

    static void pass(String msg) {
        System.out.println("  [PASS] " + msg);
        passed++;
    }

    static void fail(String msg) {
        System.err.println("  [FAIL] " + msg);
        failed++;
    }
}
