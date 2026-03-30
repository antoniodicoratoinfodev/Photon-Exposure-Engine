# Lux → Triade Esposimetrica

**App Android (Java) — Convertitore fotografico professionale**

Converte un valore di illuminamento in lux (misurato con un esposimetro) nella triade esposimetrica fotografica: **ISO**, **diaframma (f-number)** e **tempo di esposizione**.

---

## Formule matematiche

### 1. Lux → EV a ISO 100

```
EV₁₀₀ = log₂(E / 2.5)
```

dove:
- `E` = illuminamento in lux (misurato dall'esposimetro)
- `2.5 = C / 100` con `C = 250` (costante di calibrazione ANSI PH3.49-1971 / ISO 2720:1974 per sensore piano/flat)

Equivalente a: `E = 2.5 × 2^EV₁₀₀`

### 2. Correzione EV per ISO arbitrario

```
EV_ISO = EV₁₀₀ + log₂(ISO / 100)
```

Ogni raddoppio di ISO aggiunge +1 EV (1 stop).

### 3. Definizione di EV dalla triade

```
EV = log₂(N² / t)
```

dove:
- `N` = f-number (diaframma)
- `t` = tempo di esposizione in secondi

### 4. Dato EV e diaframma → calcola il tempo

```
t = N² / 2^EV
```

### 5. Dato EV e tempo → calcola il diaframma

```
N = √(t × 2^EV)
```

### Verifica incrociata

Tutte le combinazioni `(N, t)` che soddisfano `EV = log₂(N²/t)` producono la stessa esposizione. La tabella delle combinazioni equivalenti nell'app mostra questo principio.

---

## Fonti

| Fonte | Dettaglio |
|---|---|
| Wikipedia "Exposure value" | Definizione formale EV, tabella lux/EV |
| ANSI PH3.49-1971 | Costante C = 250 per sensori piani |
| ISO 2720:1974 | Standard internazionale esposimetri |
| ANSI PH2.7-1986 | Tabella scene fotografiche per EV |

---

## Struttura del progetto

```
LuxExposimeter/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/photography/luxexposimeter/
│       │   ├── ExposureCalculator.java   ← Logica matematica pura
│       │   ├── MainActivity.java         ← UI Android
│       │   └── ExposureCalculatorTest.java ← Test unitari (eseguibili con javac)
│       └── res/
│           ├── layout/activity_main.xml  ← Layout UI dark
│           ├── values/strings.xml        ← Stringhe (italiano)
│           ├── values/colors.xml         ← Palette dark fotografica
│           ├── values/themes.xml         ← Tema Material dark
│           └── drawable/ic_launcher_foreground.xml ← Icona vettoriale
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## Funzionalità dell'app

### Input
- **Valore lux**: inserito manualmente (da esposimetro)
- **ISO**: selezionabile da spinner (25 → 102400)

### Modalità di calcolo

| Modalità | Input fisso | Calcolato |
|---|---|---|
| **A — Priorità diaframma** | f-number | Tempo di esposizione |
| **B — Priorità otturatore** | Tempo | f-number |

### Output
- EV a ISO 100 (`EV₁₀₀`)
- EV corretto per l'ISO scelto (`EV_ISO`)
- Diaframma (valore esatto + standard fotografico più vicino)
- Tempo di esposizione (valore esatto + standard fotografico più vicino)
- Descrizione della scena in base all'EV
- Tabella completa di tutte le combinazioni equivalenti (con verifica EV)

---

## Come importare in Android Studio

1. Aprire Android Studio
2. **File → Open** → selezionare la cartella `LuxExposimeter/`
3. Attendere la sincronizzazione Gradle
4. Collegare un dispositivo Android (API 21+) o avviare un emulatore
5. Premere **Run** (▶)

### Requisiti
- Android Studio Hedgehog (2023.1.1) o superiore
- Android SDK 34
- minSdk 21 (Android 5.0 Lollipop)
- Java 8

---

## Test unitari

Il file `ExposureCalculatorTest.java` è un test puro Java (senza dipendenze Android) che verifica:

- Conversione lux → EV₁₀₀ (con tabella di riferimento Wikipedia)
- Correzione EV per ISO
- Calcolo tempo da EV + f-number
- Calcolo f-number da EV + tempo
- Round-trip matematico (EV → N,t → EV) su 35 combinazioni
- Combinazioni equivalenti
- Formattazione tempi e diaframmi

**Risultato**: 67 test, 0 fallimenti.

Per eseguire i test (richiede JDK):
```bash
cd app/src/main/java/com/photography/luxexposimeter
javac ExposureCalculator.java ExposureCalculatorTest.java
java ExposureCalculatorTest
```

---

## Note tecniche

- La costante `C = 250` è valida per sensori **piani (flat/cosine)**, standard per la misurazione dell'illuminamento in lux. Per sensori emisferici (hemispherical) si usa `C = 320` (Minolta) o `C = 340` (Sekonic).
- I valori standard di f-number seguono la scala fotografica ISO (1/3 stop e 1/2 stop inclusi).
- I tempi di esposizione standard coprono da 30" a 1/8000 s.
- Il confronto tra tempi standard avviene in scala logaritmica per coerenza con la scala fotografica.
