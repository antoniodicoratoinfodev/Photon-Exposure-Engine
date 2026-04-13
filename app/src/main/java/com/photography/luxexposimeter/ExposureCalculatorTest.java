package com.photography.luxexposimeter;

/**
 * Test unitario puro (senza dipendenze Android) per verificare la correttezza
 * matematica di ExposureCalculator.
 *
 * Eseguibile con: javac ExposureCalculator.java ExposureCalculatorTest.java && java ExposureCalculatorTest
 */
public class ExposureCalculatorTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Test ExposureCalculator ===\n");

        testLuxToEV100();
        testAdjustEVForISO();
        testEvAndFNumberToShutterSpeed();
        testEvAndShutterSpeedToFNumber();
        testRoundTrip();
        testEquivalentCombinations();
        testFormatShutterSpeed();
        testFormatFNumber();
        testTableValues();

        System.out.println("\n=== Risultato: " + passed + " passati, " + failed + " falliti ===");
        if (failed > 0) System.exit(1);
    }

    // ─── Test 1: lux → EV₁₀₀ ─────────────────────────────────────────────────
    static void testLuxToEV100() {
        System.out.println("--- Test 1: luxToEV100 ---");

        // Dalla tabella Wikipedia: EV₁₀₀=0 → lux=2.5 (perché 2.5 × 2^0 = 2.5)
        assertApprox("EV₁₀₀(2.5 lux) = 0",
                ExposureCalculator.luxToEV100(2.5), 0.0, 0.001);

        // EV₁₀₀=10 → lux=2.5 × 2^10 = 2560
        assertApprox("EV₁₀₀(2560 lux) = 10",
                ExposureCalculator.luxToEV100(2560.0), 10.0, 0.001);

        // EV₁₀₀=15 → lux=2.5 × 2^15 = 81920
        assertApprox("EV₁₀₀(81920 lux) = 15",
                ExposureCalculator.luxToEV100(81920.0), 15.0, 0.001);

        // EV₁₀₀=-1 → lux=2.5 × 2^(-1) = 1.25
        assertApprox("EV₁₀₀(1.25 lux) = -1",
                ExposureCalculator.luxToEV100(1.25), -1.0, 0.001);

        // Verifica eccezione per lux <= 0
        try {
            ExposureCalculator.luxToEV100(0);
            fail("luxToEV100(0) dovrebbe lanciare eccezione");
        } catch (IllegalArgumentException e) {
            pass("luxToEV100(0) lancia IllegalArgumentException correttamente");
        }
    }

    // ─── Test 2: correzione ISO ────────────────────────────────────────────────
    static void testAdjustEVForISO() {
        System.out.println("\n--- Test 2: adjustEVForISO ---");

        // ISO 100: nessuna correzione
        assertApprox("EV_ISO100 = EV100",
                ExposureCalculator.adjustEVForISO(10.0, 100), 10.0, 0.001);

        // ISO 200: +1 stop
        assertApprox("EV_ISO200 = EV100 + 1",
                ExposureCalculator.adjustEVForISO(10.0, 200), 11.0, 0.001);

        // ISO 400: +2 stop
        assertApprox("EV_ISO400 = EV100 + 2",
                ExposureCalculator.adjustEVForISO(10.0, 400), 12.0, 0.001);

        // ISO 50: -1 stop
        assertApprox("EV_ISO50 = EV100 - 1",
                ExposureCalculator.adjustEVForISO(10.0, 50), 9.0, 0.001);

        // ISO 800: +3 stop
        assertApprox("EV_ISO800 = EV100 + 3",
                ExposureCalculator.adjustEVForISO(10.0, 800), 13.0, 0.001);
    }

    // ─── Test 3: EV + f-number → tempo ────────────────────────────────────────
    static void testEvAndFNumberToShutterSpeed() {
        System.out.println("\n--- Test 3: evAndFNumberToShutterSpeed ---");

        // EV=0, N=1 → t = 1²/2^0 = 1 secondo
        assertApprox("EV=0, f/1 → t=1s",
                ExposureCalculator.evAndFNumberToShutterSpeed(0, 1.0), 1.0, 0.0001);

        // EV=10, N=8 → t = 64/1024 = 1/16 s
        assertApprox("EV=10, f/8 → t=1/16",
                ExposureCalculator.evAndFNumberToShutterSpeed(10, 8.0), 1.0 / 16.0, 0.0001);

        // EV=15, N=16 → t = 256/32768 = 1/128 s
        assertApprox("EV=15, f/16 → t=1/128",
                ExposureCalculator.evAndFNumberToShutterSpeed(15, 16.0), 1.0 / 128.0, 0.0001);

        // EV=6, N=2.8 → t = 7.84/64 ≈ 0.1225 s ≈ 1/8.16 s
        double t = ExposureCalculator.evAndFNumberToShutterSpeed(6, 2.8);
        assertApprox("EV=6, f/2.8 → t≈0.1225s",
                t, 2.8 * 2.8 / Math.pow(2, 6), 0.0001);
    }

    // ─── Test 4: EV + tempo → f-number ────────────────────────────────────────
    static void testEvAndShutterSpeedToFNumber() {
        System.out.println("\n--- Test 4: evAndShutterSpeedToFNumber ---");

        // EV=0, t=1 → N = sqrt(1 × 1) = 1
        assertApprox("EV=0, t=1s → f/1",
                ExposureCalculator.evAndShutterSpeedToFNumber(0, 1.0), 1.0, 0.0001);

        // EV=10, t=1/16 → N = sqrt(1/16 × 1024) = sqrt(64) = 8
        assertApprox("EV=10, t=1/16 → f/8",
                ExposureCalculator.evAndShutterSpeedToFNumber(10, 1.0 / 16.0), 8.0, 0.0001);

        // EV=12, t=1/125 → N = sqrt(1/125 × 4096) = sqrt(32.768) ≈ 5.724
        double n = ExposureCalculator.evAndShutterSpeedToFNumber(12, 1.0 / 125.0);
        assertApprox("EV=12, t=1/125 → f≈5.72",
                n, Math.sqrt((1.0 / 125.0) * Math.pow(2, 12)), 0.001);
    }

    // ─── Test 5: Round-trip (EV → N,t → EV) ──────────────────────────────────
    static void testRoundTrip() {
        System.out.println("\n--- Test 5: Round-trip EV → (N,t) → EV ---");

        double[] evValues = {-2, 0, 5, 8, 10, 12, 15};
        double[] fNumbers = {1.4, 2.8, 5.6, 8.0, 11.0};

        for (double ev : evValues) {
            for (double f : fNumbers) {
                double t = ExposureCalculator.evAndFNumberToShutterSpeed(ev, f);
                double evBack = ExposureCalculator.fNumberAndShutterSpeedToEV(f, t);
                assertApprox(
                        String.format("Round-trip EV=%.0f, f/%.1f", ev, f),
                        evBack, ev, 0.0001);
            }
        }
    }

    // ─── Test 6: Combinazioni equivalenti ─────────────────────────────────────
    static void testEquivalentCombinations() {
        System.out.println("\n--- Test 6: getEquivalentCombinations ---");

        double ev = 12.0;
        java.util.List<double[]> combos = ExposureCalculator.getEquivalentCombinations(ev);

        if (combos.isEmpty()) {
            fail("getEquivalentCombinations(12) non deve essere vuota");
            return;
        }
        pass("getEquivalentCombinations(12) ha " + combos.size() + " combinazioni");

        // Tutte le combinazioni devono produrre lo stesso EV
        for (double[] c : combos) {
            double evCheck = ExposureCalculator.fNumberAndShutterSpeedToEV(c[0], c[1]);
            assertApprox(
                    String.format("Combo f/%.1f, t=%.6f → EV=%.2f", c[0], c[1], evCheck),
                    evCheck, ev, 0.001);
        }
    }

    // ─── Test 7: Formattazione tempi ──────────────────────────────────────────
    static void testFormatShutterSpeed() {
        System.out.println("\n--- Test 7: formatShutterSpeed ---");

        assertEqual("1 secondo", ExposureCalculator.formatShutterSpeed(1.0), "1\"");
        assertEqual("30 secondi", ExposureCalculator.formatShutterSpeed(30.0), "30\"");
        assertEqual("1/125", ExposureCalculator.formatShutterSpeed(1.0 / 125.0), "1/125");
        assertEqual("1/60", ExposureCalculator.formatShutterSpeed(1.0 / 60.0), "1/60");
        assertEqual("1/1000", ExposureCalculator.formatShutterSpeed(1.0 / 1000.0), "1/1000");
    }

    // ─── Test 8: Formattazione f-number ───────────────────────────────────────
    static void testFormatFNumber() {
        System.out.println("\n--- Test 8: formatFNumber ---");

        assertEqual("f/8", ExposureCalculator.formatFNumber(8.0), "f/8");
        assertEqual("f/1.4", ExposureCalculator.formatFNumber(1.4), "f/1.4");
        assertEqual("f/2.8", ExposureCalculator.formatFNumber(2.8), "f/2.8");
        assertEqual("f/11", ExposureCalculator.formatFNumber(11.0), "f/11");
    }

    // ─── Test 9: Verifica tabella Wikipedia (EV₁₀₀ → lux) ────────────────────
    static void testTableValues() {
        System.out.println("\n--- Test 9: Verifica tabella Wikipedia EV₁₀₀ → lux ---");

        // Tabella da Wikipedia: lux = 2.5 × 2^EV
        int[] evTable   = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        double[] luxTable = {1.25, 2.5, 5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560,
                5120, 10240, 20480, 40960, 81920, 163840};

        for (int i = 0; i < evTable.length; i++) {
            double evCalc = ExposureCalculator.luxToEV100(luxTable[i]);
            assertApprox(
                    String.format("luxToEV100(%.0f lux) = %d", luxTable[i], evTable[i]),
                    evCalc, evTable[i], 0.001);
        }
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

    static void pass(String msg) {
        System.out.println("  [PASS] " + msg);
        passed++;
    }

    static void fail(String msg) {
        System.err.println("  [FAIL] " + msg);
        failed++;
    }
}
