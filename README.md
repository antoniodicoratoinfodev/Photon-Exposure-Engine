# Photon-Exposure-Engine (Lux Exposure Calculator)

**Android App (Java) — Professional Photography Exposure Converter**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)

> ⚠️ **IMPORTANT – READ THIS FIRST**  
> This source code is made public **ONLY FOR VIEWING AND EDUCATIONAL PURPOSES**.  
> You are **NOT PERMITTED** to run, execute, deploy, or use this software for its intended purpose (exposure calculation) unless you purchase the official commercial version from the Google Play Store.  
> See the [LICENSE](./LICENSE.md) file for full legal terms.

Converts an illuminance value in lux (measured by a light meter) into the photographic exposure triad: **ISO**, **aperture (f-number)**, and **shutter speed**.

---

## 📱 About this repository

This repository contains the full source code for the **Photon-Exposure-Engine (Lux Exposure Calculator)** app.

It is published **solely** to:

* Showcase development skills
* Serve as a learning resource for photographers and developers (code reading only)
* Document the mathematical foundation behind exposure calculation

**The source code is NOT licensed for any form of use as an application.**  
The official, ready-to-use version is available for purchase on the Google Play Store.

---

## 📸 Screenshots

| | | |
|---|---|---|
| ![Screenshot 1](Samples/1.png) | ![Screenshot 2](Samples/2.png) | ![Screenshot 3](Samples/3.png) |

---

## 📐 Mathematical formulas

### 1. Lux → EV at ISO 100

```
EV100 = log2(E / 2.5)
```

Where:

* `E` = illuminance in lux
* `2.5 = C / 100`, with `C = 250` (ANSI PH3.49-1971 / ISO 2720:1974 calibration constant for flat/cosine sensors)

Equivalent form:

```
E = 2.5 × 2^EV100
```

---

### 2. EV correction for arbitrary ISO

```
EV_ISO = EV100 + log2(ISO / 100)
```

Doubling ISO increases exposure by **+1 EV (one stop)**.

---

### 3. EV definition from the exposure triad

```
EV = log2(N² / t)
```

Where:

* `N` = f-number (aperture)
* `t` = exposure time in seconds

---

### 4. Given EV and aperture → shutter speed

```
t = N² / 2^EV
```

### 5. Given EV and shutter speed → aperture

```
N = √(t × 2^EV)
```

---

### 🔁 Cross-validation

All combinations `(N, t)` satisfying:

```
EV = log2(N² / t)
```

produce the **same exposure**.  
The app includes a table of equivalent combinations to demonstrate this principle.

---

### 6. Reciprocity failure — Analog tab

The reciprocity law `H = E × t` holds on film only in the mid range (~1/1000 s – 1 s).
For long exposures the effective sensitivity of the emulsion drops
(**low-intensity reciprocity failure**, Schwarzschild effect: response ∝ `E × t^p`, `p < 1`),
so the metered time `Tm` must be extended to a corrected time `Tc`.
The **Analog** tab applies the official manufacturer data of the selected film stock;
the **Digital** tab applies no correction (digital sensors are linear).

**Ilford / HARMAN model** (official, "Film Reciprocity Failure Compensation", Dec 2023 — no correction for `Tm ≤ 1 s`):

```
Tc = Tm^P
```

| Film | P | Film | P |
|---|---|---|---|
| Pan F+ | 1.33 | SFX 200 | 1.43 |
| FP4+ | 1.26 | XP2 Super | 1.31 |
| HP5+ | 1.31 | Ortho+ | 1.25 |
| Delta 100 | 1.26 | Kentmere 100 | 1.26 |
| Delta 400 | 1.41 | Kentmere 400 | 1.30 |
| Delta 3200 | 1.33 | Generic B&W | 1.30 |

**Datasheet tables** (log-log linear interpolation between published points):

| Film | Published points (metered → corrected) |
|---|---|
| Kodak Tri-X 320/400 (F-4017) | 1 s→2 s, 10 s→50 s, 100 s→1200 s (dev −10/−20/−30%) |
| Kodak T-Max 100 (F-4016) | 1 s→+1/3 stop, 10 s→15 s, 100 s→200 s |
| Kodak T-Max 400 (F-4043) | ≤1 s none, 10 s→+1/3 stop, 100 s→300 s |
| Fomapan 100 | 1 s→×2, 10 s→×8, 100 s→×16 |
| Fomapan 400 | 1 s→×1.5, 10 s→×6, 100 s→×8 |

**Stop-based data** (the correction `s(t)` refers to the *actual* exposure time,
so the corrected time solves the fixed point `Tc = Tm × 2^s(Tc)`):

| Film | Data |
|---|---|
| Fuji Acros II (AF3-0258E) | none <120 s; +1/2 stop for 120–1000 s |
| Fuji Provia 100F | none up to 128 s; beyond: test |
| Fuji Velvia 50 (RVP50) | 4 s→+1/3 (CC 5M), 8 s→+1/2, 16 s→+2/3, 32 s→+1 stop; ≥64 s not recommended |
| Kodak Ektachrome E100 | ~+1/2 stop for 20–40 s (approximate) |

**Shutter-priority mode (B)** — with a fixed *actual* time `t`, the equivalent
metered time is `Tm_eq = t / 2^s(t)` (or `t^(1/P)`), and the aperture becomes
`N = √(Tm_eq × 2^EV)` (i.e. the aperture opens by `log2(t / Tm_eq)` stops).

Beyond the published manufacturer data the value is extrapolated and flagged in the UI.
High-intensity failure (exposures < 1/10,000 s) is outside the app range.

---

## 📚 Sources

| Source                       | Description                                     |
| ---------------------------- | ----------------------------------------------- |
| Wikipedia – "Exposure value" | Formal EV definition and lux/EV reference table |
| ANSI PH3.49-1971             | Calibration constant C = 250                    |
| ISO 2720:1974                | International light meter standard              |
| ANSI PH2.7-1986              | EV reference table for real-world scenes        |
| HARMAN Technology (Dec 2023) | "Film Reciprocity Failure Compensation" — Tc = Tm^P factors |
| Kodak F-4016 / F-4017 / F-4043 | T-Max 100, Tri-X, T-Max 400 long-exposure tables |
| Fujifilm AF3-0258E, RVP50 Data Guide | Acros II, Velvia 50, Provia 100F reciprocity data |
| Foma datasheets              | Fomapan 100/400 "Schwarzschild effect" tables   |

---

## 🧱 Project structure

```
Photon-Exposure-Engine/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/photography/luxexposimeter/
│       │   ├── ExposureCalculator.java
│       │   ├── FilmStock.java
│       │   ├── ReciprocityCalculator.java
│       │   ├── MainActivity.java
│       │   ├── ExposureCalculatorTest.java
│       │   └── ReciprocityCalculatorTest.java
│       └── res/
│           ├── layout/activity_main.xml
│           ├── values/strings.xml
│           ├── values/colors.xml
│           ├── values/themes.xml
│           └── drawable/ic_launcher_foreground.xml
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## ⚙️ App features

### Input

* **Lux value**: manual input from light meter
* **ISO**: selectable (25 → 102400)

### Calculation modes

| Mode                      | Fixed input   | Calculated    |
| ------------------------- | ------------- | ------------- |
| **A — Aperture Priority** | f-number      | Shutter speed |
| **B — Shutter Priority**  | Shutter speed | f-number      |

### Output

* EV at ISO 100 (`EV100`)
* EV corrected for selected ISO (`EV_ISO`)
* Aperture (exact value + nearest standard value)
* Shutter speed (exact value + nearest standard value)
* Scene description based on EV
* Full table of equivalent exposure combinations

---

## 🚀 How to import in Android Studio

1. Open Android Studio
2. **File → Open** → select the `Photon-Exposure-Engine/` folder
3. Wait for Gradle sync
4. Connect a device (API 21+) or start an emulator
5. Press **Run** ▶

> **Reminder:** Importing and building the project is allowed for code inspection only.  
> Running the compiled app on a device is **prohibited** without purchasing the commercial version.

### Requirements

* Android Studio Hedgehog (2023.1.1) or newer
* Android SDK 34
* minSdk 21 (Android 5.0 Lollipop)
* Java 8

---

## 🧪 Unit tests

`ExposureCalculatorTest.java` is a pure Java test suite verifying:

* Lux → EV100 conversion
* EV correction for ISO
* Shutter speed from EV + aperture
* Aperture from EV + shutter speed
* Round-trip calculation (EV → N, t → EV)
* Equivalent exposure combinations
* Formatting of shutter speeds and apertures

`ReciprocityCalculatorTest.java` verifies the Analog-tab math:

* Ilford power model (official HP5+ example: 10 s → 20.4 s)
* Exact datasheet points for Kodak, Foma, Fuji tables
* Fixed-point solution for stop-based data (Velvia 50, Acros II, E100)
* Inverse (shutter-priority) round-trips
* Monotonicity, `Tc ≥ Tm`, beyond-data flags, long-time formatting

Run tests:

```bash
cd app/src/main/java/com/photography/luxexposimeter
javac ExposureCalculator.java ExposureCalculatorTest.java \
      FilmStock.java ReciprocityCalculator.java ReciprocityCalculatorTest.java
java ExposureCalculatorTest
java ReciprocityCalculatorTest
```

---

## 🧠 Technical notes

* `C = 250` is valid for **flat (cosine) sensors**
* Hemispherical sensors use:
  * `C = 330` (Minolta)
  * `C = 340` (Sekonic)
* Standard f-numbers follow ISO photographic scale (including 1/3 and 1/2 stops)
* Shutter speeds range from **30s to 1/8000s**
* Comparisons are logarithmic to match exposure stops

---

## 📄 License

Copyright © 2026 Antonio Dicorato. All rights reserved.

This source code is made publicly available on GitHub **SOLELY FOR VIEWING AND EDUCATIONAL INSPECTION**.  
You may browse, fork, and study the code as permitted by GitHub's Terms of Service.

**You are strictly PROHIBITED from:**

- Running, executing, or deploying this software for any purpose.
- Using the application for its intended photographic exposure calculation purpose or any similar real-world functionality.
- Copying, modifying, or creating derivative works of the code.
- Distributing, sublicensing, or making the software available to third parties in any compiled or executable form.

If you wish to use this application for its intended purpose (exposure calculation, photography assistance, or any practical functionality), you **MUST purchase the commercial version** from the Google Play Store. The commercial version is the only authorized way to use the software as an application.

For commercial licensing or permission requests:  
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

### Disclaimer

This software is provided *"as is"*, without warranty of any kind.  
The author is not liable for any damages arising from its use.

---

## 📦 App availability

The full application will be available for purchase on the Google Play Store.

---

# Photon-Exposure-Engine (Lux Exposure Calculator)

**App Android (Java) — Convertitore Professionale per Esposizione Fotografica**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)

> ⚠️ **IMPORTANTE – LEGGI PRIMA**  
> Questo codice sorgente è reso pubblico **SOLO PER VISUALIZZAZIONE E PER FINI EDUCATIVI**.  
> **NON È CONSENTITO** eseguire, avviare, distribuire o usare questo software per il suo scopo principale (calcolo dell'esposizione) a meno che non si acquisti la versione commerciale ufficiale dal Google Play Store.  
> Consulta il file [LICENSE](./LICENSE.md) per i termini legali completi.

Converte un valore di illuminamento in lux (misurato con un luxmetro) nella triade fotografica dell'esposizione: **ISO**, **apertura (f-number)** e **tempo di esposizione**.

---

## 📱 Informazioni su questo repository

Questo repository contiene il codice sorgente completo dell'app **Photon-Exposure-Engine (Lux Exposure Calculator)**.

È pubblicato **esclusivamente** per:

* Mostrare competenze di sviluppo
* Servire come risorsa didattica per fotografi e sviluppatori (sola lettura del codice)
* Documentare le basi matematiche del calcolo dell'esposizione

**Il codice sorgente NON è concesso in licenza per alcuna forma di utilizzo come applicazione.**  
La versione ufficiale e pronta all'uso è disponibile per l'acquisto su Google Play Store.

---

## 📸 Screenshot

| | | |
|---|---|---|
| ![Screenshot 1](Samples/1.png) | ![Screenshot 2](Samples/2.png) | ![Screenshot 3](Samples/3.png) |

---

## 📐 Formule matematiche

### 1. Lux → EV a ISO 100

```
EV100 = log2(E / 2.5)
```

Dove:

* `E` = illuminamento in lux
* `2.5 = C / 100`, con `C = 250` (costante di calibrazione ANSI PH3.49-1971 / ISO 2720:1974 per sensori flat/cosine)

Forma equivalente:

```
E = 2.5 × 2^EV100
```

---

### 2. Correzione EV per ISO arbitrario

```
EV_ISO = EV100 + log2(ISO / 100)
```

Raddoppiare l'ISO aumenta l'esposizione di **+1 EV (uno stop)**.

---

### 3. Definizione di EV dalla triade di esposizione

```
EV = log2(N² / t)
```

Dove:

* `N` = f-number (apertura)
* `t` = tempo di esposizione in secondi

---

### 4. Dato EV e apertura → tempo di esposizione

```
t = N² / 2^EV
```

### 5. Dato EV e tempo di esposizione → apertura

```
N = √(t × 2^EV)
```

---

### 🔁 Validazione incrociata

Tutte le combinazioni `(N, t)` che soddisfano:

```
EV = log2(N² / t)
```

producono la **stessa esposizione**.  
L'app include una tabella di combinazioni equivalenti per dimostrare questo principio.

---

### 6. Difetto di reciprocità — tab Analog

La legge di reciprocità `H = E × t` vale sulla pellicola solo nel range medio
(~1/1000 s – 1 s). Alle lunghe esposizioni la sensibilità effettiva
dell'emulsione cala (**difetto di reciprocità a bassa intensità**, effetto
Schwarzschild: risposta ∝ `E × t^p`, `p < 1`): il tempo misurato `Tm` va
allungato al tempo corretto `Tc`. Il tab **Analog** applica i dati ufficiali
del produttore per la pellicola selezionata; il tab **Digital** non applica
alcuna correzione (i sensori digitali sono lineari).

**Modello Ilford / HARMAN** (ufficiale, "Film Reciprocity Failure Compensation",
dic 2023 — nessuna correzione per `Tm ≤ 1 s`):

```
Tc = Tm^P
```

I fattori P per pellicola, le tabelle Kodak/Foma (interpolate in scala log-log)
e i dati in stop Fuji/E100 (punto fisso `Tc = Tm × 2^s(Tc)`) sono elencati
nella sezione inglese qui sopra. In **modalità B** (tempo reale fisso `t`) il
tempo equivalente misurato è `Tm_eq = t / 2^s(t)` (oppure `t^(1/P)`) e il
diaframma si apre di `log2(t / Tm_eq)` stop: `N = √(Tm_eq × 2^EV)`.
Oltre i dati pubblicati dal produttore il valore è estrapolato e segnalato
nella UI; il failure ad alta intensità (< 1/10.000 s) è fuori dal range dell'app.

---

## 📚 Fonti

| Fonte                        | Descrizione                                               |
| ---------------------------- | --------------------------------------------------------- |
| Wikipedia – "Exposure value" | Definizione formale di EV e tabella di riferimento lux/EV |
| ANSI PH3.49-1971             | Costante di calibrazione C = 250                          |
| ISO 2720:1974                | Standard internazionale per luxmetri                      |
| ANSI PH2.7-1986              | Tabella di riferimento EV per scene reali                 |
| HARMAN Technology (dic 2023) | "Film Reciprocity Failure Compensation" — fattori Tc = Tm^P |
| Kodak F-4016 / F-4017 / F-4043 | Tabelle lunghe esposizioni T-Max 100, Tri-X, T-Max 400  |
| Fujifilm AF3-0258E, RVP50 Data Guide | Dati reciprocità Acros II, Velvia 50, Provia 100F |
| Datasheet Foma               | Tabelle "Schwarzschild effect" Fomapan 100/400            |

---

## 🧱 Struttura del progetto

```
Photon-Exposure-Engine/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/photography/luxexposimeter/
│       │   ├── ExposureCalculator.java
│       │   ├── FilmStock.java
│       │   ├── ReciprocityCalculator.java
│       │   ├── MainActivity.java
│       │   ├── ExposureCalculatorTest.java
│       │   └── ReciprocityCalculatorTest.java
│       └── res/
│           ├── layout/activity_main.xml
│           ├── values/strings.xml
│           ├── values/colors.xml
│           ├── values/themes.xml
│           └── drawable/ic_launcher_foreground.xml
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## ⚙️ Funzionalità dell'app

### Input

* **Valore in lux**: inserimento manuale dal luxmetro
* **ISO**: selezionabile (25 → 102400)

### Modalità di calcolo

| Modalità                    | Input fisso          | Calcolato            |
| --------------------------- | -------------------- | -------------------- |
| **A — Priorità Apertura**   | f-number             | Tempo di esposizione |
| **B — Priorità Otturatore** | Tempo di esposizione | f-number             |

### Output

* EV a ISO 100 (`EV100`)
* EV corretto per ISO selezionato (`EV_ISO`)
* Apertura (valore esatto + valore standard più vicino)
* Tempo di esposizione (valore esatto + valore standard più vicino)
* Descrizione della scena basata su EV
* Tabella completa delle combinazioni equivalenti di esposizione

---

## 🚀 Come importare in Android Studio

1. Aprire Android Studio
2. **File → Open** → selezionare la cartella `Photon-Exposure-Engine/`
3. Attendere la sincronizzazione di Gradle
4. Collegare un dispositivo (API 21+) o avviare un emulatore
5. Premere **Run** ▶

> **Promemoria:** L'importazione e la compilazione del progetto sono consentite solo per ispezione del codice.  
> L'esecuzione dell'app compilata su un dispositivo è **vietata** senza l'acquisto della versione commerciale.

### Requisiti

* Android Studio Hedgehog (2023.1.1) o versione successiva
* Android SDK 34
* minSdk 21 (Android 5.0 Lollipop)
* Java 8

---

## 🧪 Test unitari

`ExposureCalculatorTest.java` è una suite di test in Java puro che verifica:

* Conversione Lux → EV100
* Correzione EV per ISO
* Tempo di esposizione da EV + apertura
* Apertura da EV + tempo di esposizione
* Calcolo round-trip (EV → N, t → EV)
* Combinazioni di esposizione equivalenti
* Formattazione di tempi di esposizione e aperture

`ReciprocityCalculatorTest.java` verifica la matematica del tab Analog:

* Modello Ilford Tc = Tm^P (esempio ufficiale HP5+: 10 s → 20.4 s)
* Punti esatti delle tabelle Kodak, Foma e Fuji
* Punto fisso per i dati in stop (Velvia 50, Acros II, E100)
* Round-trip dell'inversa (priorità di tempi)
* Monotonia, `Tc ≥ Tm`, flag oltre-dati, formattazione tempi lunghi

Esecuzione test:

```bash
cd app/src/main/java/com/photography/luxexposimeter
javac ExposureCalculator.java ExposureCalculatorTest.java \
      FilmStock.java ReciprocityCalculator.java ReciprocityCalculatorTest.java
java ExposureCalculatorTest
java ReciprocityCalculatorTest
```

---

## 🧠 Note tecniche

* `C = 250` è valido per **sensori flat (cosine)**
* Sensori emisferici utilizzano:
  * `C = 330` (Minolta)
  * `C = 340` (Sekonic)
* I f-number standard seguono la scala fotografica ISO (inclusi 1/3 e 1/2 stop)
* I tempi di esposizione vanno da **30s a 1/8000s**
* I confronti sono logaritmici per corrispondere agli stop di esposizione

---

## 📄 Licenza

Copyright © 2026 Antonio Dicorato. Tutti i diritti riservati.

Questo codice sorgente è reso pubblico su GitHub **ESCLUSIVAMENTE PER VISUALIZZAZIONE, PER FINI EDUCATIVI E DIDATTICI**.

**È SEVERAMENTE VIETATO:**

- Eseguire, avviare o distribuire questo software per qualsiasi scopo.
- Utilizzare l'applicazione per il suo scopo previsto (calcolo dell'esposizione fotografica o qualsiasi funzionalità simile nel mondo reale).
- Copiare, modificare o creare opere derivate dal codice.
- Distribuire, concedere in sublicenza o mettere il software a disposizione di terzi in forma compilata o eseguibile.

Se desideri utilizzare questa applicazione per il suo scopo principale (calcolo dell'esposizione, assistenza fotografica o qualsiasi funzionalità pratica), **DEVI ACQUISTARE la versione commerciale** dal Google Play Store. La versione commerciale è l'unico modo autorizzato per usare il software come applicazione.

Per richieste di licenza commerciale o autorizzazioni:  
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

### Disclaimer

Questo software è fornito *"così com'è"*, senza alcuna garanzia.  
L'autore non è responsabile per eventuali danni derivanti dal suo utilizzo.

---

## 📦 Disponibilità dell'app

L'applicazione completa sarà disponibile per l'acquisto sul Google Play Store.
