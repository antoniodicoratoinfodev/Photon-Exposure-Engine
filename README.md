# Photon-Exposure-Engine (Lux Exposure Calculator)

**Android App (Java) — Professional Photography Exposure Converter**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)

Converts an illuminance value in lux (measured by a light meter) into the photographic exposure triad: **ISO**, **aperture (f-number)**, and **shutter speed**.

> **Note:** This source code is publicly available **for educational and portfolio purposes only**.
> The full application will be available for purchase on the Google Play Store.
> No commercial use, redistribution, or derivative works are permitted without explicit written permission.

---

## 📱 About this repository

This repository contains the full source code for the **Photon-Exposure-Engine (Lux Exposure Calculator)** app.

It is published to:

* Showcase development skills
* Serve as a learning resource for photographers and developers
* Document the mathematical foundation behind exposure calculation

The **official, ready-to-use version** will be released on the Google Play Store.
The source code is **not licensed for commercial use**.

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

## 📚 Sources

| Source                       | Description                                     |
| ---------------------------- | ----------------------------------------------- |
| Wikipedia – “Exposure value” | Formal EV definition and lux/EV reference table |
| ANSI PH3.49-1971             | Calibration constant C = 250                    |
| ISO 2720:1974                | International light meter standard              |
| ANSI PH2.7-1986              | EV reference table for real-world scenes        |

---

## 🧱 Project structure

```
Photon-Exposure-Engine/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/photography/luxexposimeter/
│       │   ├── ExposureCalculator.java
│       │   ├── MainActivity.java
│       │   └── ExposureCalculatorTest.java
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

---

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

**Result:** 67 tests — 0 failures ✅

Run tests:

```bash
cd app/src/main/java/com/photography/luxexposimeter
javac ExposureCalculator.java ExposureCalculatorTest.java
java ExposureCalculatorTest
```

---

## 🧠 Technical notes

* `C = 250` is valid for **flat (cosine) sensors**
* Hemispherical sensors use:

  * `C = 320` (Minolta)
  * `C = 340` (Sekonic)
* Standard f-numbers follow ISO photographic scale (including 1/3 and 1/2 stops)
* Shutter speeds range from **30s to 1/8000s**
* Comparisons are logarithmic to match exposure stops

---

## 📄 License

Copyright © 2025 Antonio Dicorato. All rights reserved.

This source code is provided **exclusively for educational and portfolio purposes**.

You are **not permitted** to:

* Use it commercially
* Modify or redistribute
* Create derivative works

without explicit written permission.

For licensing inquiries or collaborations:
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

---

# Photon-Exposure-Engine (Lux Exposure Calculator)

**App Android (Java) — Convertitore Professionale di Esposizione Fotografica**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)

Converte un valore di illuminamento in lux (misurato con un luxmetro) nella triade fotografica dell’esposizione: **ISO**, **apertura (f-number)** e **tempo di esposizione**.

> **Nota:** Questo codice sorgente è reso pubblico **solo a scopo educativo e per portfolio**.
> L’applicazione completa sarà disponibile per l’acquisto su Google Play Store.
> Non è consentito alcun uso commerciale, ridistribuzione o opere derivate senza autorizzazione scritta esplicita.

---

## 📱 Informazioni su questo repository

Questo repository contiene il codice sorgente completo dell’app **Photon-Exposure-Engine (Lux Exposure Calculator)**.

È pubblicato per:

* Mostrare competenze di sviluppo
* Servire come risorsa didattica per fotografi e sviluppatori
* Documentare le basi matematiche del calcolo dell’esposizione

La **versione ufficiale e pronta all’uso** sarà rilasciata su Google Play Store.
Il codice sorgente **non è autorizzato per uso commerciale**.

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

Raddoppiare l’ISO aumenta l’esposizione di **+1 EV (uno stop)**.

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
L’app include una tabella di combinazioni equivalenti per dimostrare questo principio.

---

## 📚 Fonti

| Fonte                        | Descrizione                                               |
| ---------------------------- | --------------------------------------------------------- |
| Wikipedia – “Exposure value” | Definizione formale di EV e tabella di riferimento lux/EV |
| ANSI PH3.49-1971             | Costante di calibrazione C = 250                          |
| ISO 2720:1974                | Standard internazionale per luxmetri                      |
| ANSI PH2.7-1986              | Tabella di riferimento EV per scene reali                 |

---

## 🧱 Struttura del progetto

```
Photon-Exposure-Engine/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/photography/luxexposimeter/
│       │   ├── ExposureCalculator.java
│       │   ├── MainActivity.java
│       │   └── ExposureCalculatorTest.java
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

## ⚙️ Funzionalità dell’app

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

---

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

**Risultato:** 67 test — 0 fallimenti ✅

Esecuzione test:

```bash
cd app/src/main/java/com/photography/luxexposimeter
javac ExposureCalculator.java ExposureCalculatorTest.java
java ExposureCalculatorTest
```

---

## 🧠 Note tecniche

* `C = 250` è valido per **sensori flat (cosine)**
* Sensori emisferici utilizzano:

  * `C = 320` (Minolta)
  * `C = 340` (Sekonic)
* I f-number standard seguono la scala fotografica ISO (inclusi 1/3 e 1/2 stop)
* I tempi di esposizione vanno da **30s a 1/8000s**
* I confronti sono logaritmici per corrispondere agli stop di esposizione

---

## 📄 Licenza

Copyright © 2025 Antonio Dicorato. Tutti i diritti riservati.

Questo codice sorgente è fornito **esclusivamente a scopo educativo e per portfolio**.

Non è consentito:

* Uso commerciale
* Modifica o ridistribuzione
* Creazione di opere derivate

senza autorizzazione scritta esplicita.

Per richieste di licenza o collaborazioni:
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

---

### Disclaimer

Questo software è fornito *“così com’è”*, senza alcuna garanzia.
L’autore non è responsabile per eventuali danni derivanti dal suo utilizzo.

---

## 📦 Disponibilità dell’app

L’applicazione completa sarà rilasciata su Google Play Store.

---

### Disclaimer

This software is provided *“as is”*, without warranty of any kind.
The author is not liable for any damages arising from its use.

---

## 📦 App availability

The full application will be released on the Google Play Store.

---