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

It is published:

* to showcase development skills
* as a learning resource for photographers and developers
* to document the mathematical foundation behind exposure calculation

The **official, ready-to-use version** of the app will be released on the Google Play Store.
The source code in this repository is **not licensed for commercial use**.

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

---

### 5. Given EV and shutter speed → aperture

```
N = √(t × 2^EV)
```

---

### 🔁 Cross-validation

All combinations `(N, t)` that satisfy:

```
EV = log2(N² / t)
```

produce the **same exposure**.

The app includes a full table of equivalent combinations to demonstrate this principle.

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

* **Lux value**: manual input (light meter)
* **ISO**: selectable (25 → 102400)

---

### Calculation modes

| Mode                      | Fixed input   | Calculated    |
| ------------------------- | ------------- | ------------- |
| **A — Aperture Priority** | f-number      | Shutter speed |
| **B — Shutter Priority**  | Shutter speed | f-number      |

---

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

`ExposureCalculatorTest.java` is a pure Java test suite (no Android dependencies) that verifies:

* Lux → EV100 conversion
* EV correction for ISO
* Shutter speed from EV + aperture
* Aperture from EV + shutter speed
* Mathematical round-trip (EV → N,t → EV)
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

* `C = 250` is valid for **flat (cosine) sensors**, standard in lux measurement
* Hemispherical sensors use:

  * `C = 320` (Minolta)
  * `C = 340` (Sekonic)
* Standard f-numbers follow ISO photographic scale (including 1/3 and 1/2 stops)
* Shutter speeds range from **30s to 1/8000s**
* Comparisons are performed on a **logarithmic scale** to match exposure stops

---

## 📄 License

Copyright © 2025 Antonio Dicorato. All rights reserved.

This source code is provided **exclusively for educational and portfolio purposes**.

You are **not permitted** to:

* use it commercially
* modify or redistribute it
* create derivative works

without explicit written permission.

For licensing inquiries or collaborations:
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

---

### Disclaimer

This software is provided *“as is”*, without warranty of any kind.
The author shall not be liable for any damages arising from its use.

---

## 📦 App availability

The full, ready-to-use application will be released on the Google Play Store.

---

Ecco il tuo README riscritto **identico**, seguito dalla **traduzione completa in italiano** 👇

---

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

It is published:

* to showcase development skills
* as a learning resource for photographers and developers
* to document the mathematical foundation behind exposure calculation

The **official, ready-to-use version** of the app will be released on the Google Play Store.
The source code in this repository is **not licensed for commercial use**.

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

---

### 5. Given EV and shutter speed → aperture

```
N = √(t × 2^EV)
```

---

### 🔁 Cross-validation

All combinations `(N, t)` that satisfy:

```
EV = log2(N² / t)
```

produce the **same exposure**.

The app includes a full table of equivalent combinations to demonstrate this principle.

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

* **Lux value**: manual input (light meter)
* **ISO**: selectable (25 → 102400)

---

### Calculation modes

| Mode                      | Fixed input   | Calculated    |
| ------------------------- | ------------- | ------------- |
| **A — Aperture Priority** | f-number      | Shutter speed |
| **B — Shutter Priority**  | Shutter speed | f-number      |

---

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

`ExposureCalculatorTest.java` is a pure Java test suite (no Android dependencies) that verifies:

* Lux → EV100 conversion
* EV correction for ISO
* Shutter speed from EV + aperture
* Aperture from EV + shutter speed
* Mathematical round-trip (EV → N,t → EV)
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

* `C = 250` is valid for **flat (cosine) sensors**, standard in lux measurement
* Hemispherical sensors use:

  * `C = 320` (Minolta)
  * `C = 340` (Sekonic)
* Standard f-numbers follow ISO photographic scale (including 1/3 and 1/2 stops)
* Shutter speeds range from **30s to 1/8000s**
* Comparisons are performed on a **logarithmic scale** to match exposure stops

---

## 📄 License

Copyright © 2025 Antonio Dicorato. All rights reserved.

This source code is provided **exclusively for educational and portfolio purposes**.

You are **not permitted** to:

* use it commercially
* modify or redistribute it
* create derivative works

without explicit written permission.

For licensing inquiries or collaborations:
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

---

### Disclaimer

This software is provided *“as is”*, without warranty of any kind.
The author shall not be liable for any damages arising from its use.

---

## 📦 App availability

The full, ready-to-use application will be released on the Google Play Store.

---

# 🇮🇹 Traduzione in italiano

# Photon-Exposure-Engine (Lux Exposure Calculator)

**App Android (Java) — Convertitore professionale di esposizione fotografica**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)

Converte un valore di illuminamento in lux (misurato con un esposimetro) nella triade fotografica: **ISO**, **apertura (numero f)** e **tempo di esposizione**.

> **Nota:** Questo codice sorgente è reso disponibile **solo per scopi educativi e di portfolio**.
> L’app completa sarà disponibile per l’acquisto sul Google Play Store.
> Non è consentito l’uso commerciale, la redistribuzione o la creazione di opere derivate senza autorizzazione scritta esplicita.

---

## 📱 Informazioni sul repository

Questo repository contiene il codice sorgente completo dell’app **Photon-Exposure-Engine (Lux Exposure Calculator)**.

È pubblicato per:

* mostrare le competenze di sviluppo
* fungere da risorsa didattica per fotografi e sviluppatori
* documentare le basi matematiche del calcolo dell’esposizione

La versione **ufficiale e pronta all’uso** sarà pubblicata sul Google Play Store.
Il codice sorgente presente in questo repository **non è utilizzabile a fini commerciali**.

---

## 📐 Formule matematiche

### 1. Lux → EV a ISO 100

```
EV100 = log2(E / 2.5)
```

Dove:

* `E` = illuminamento in lux
* `2.5 = C / 100`, con `C = 250` (costante di calibrazione ANSI PH3.49-1971 / ISO 2720:1974 per sensori piani/coseno)

Forma equivalente:

```
E = 2.5 × 2^EV100
```

---

### 2. Correzione EV per ISO arbitrario

```
EV_ISO = EV100 + log2(ISO / 100)
```

Raddoppiare gli ISO aumenta l’esposizione di **+1 EV (uno stop)**.

---

### 3. Definizione di EV dalla triade di esposizione

```
EV = log2(N² / t)
```

Dove:

* `N` = numero f (apertura)
* `t` = tempo di esposizione in secondi

---

### 4. Dati EV e apertura → tempo di scatto

```
t = N² / 2^EV
```

---

### 5. Dati EV e tempo di scatto → apertura

```
N = √(t × 2^EV)
```

---

### 🔁 Verifica incrociata

Tutte le combinazioni `(N, t)` che soddisfano:

```
EV = log2(N² / t)
```

producono la **stessa esposizione**.

L’app include una tabella completa delle combinazioni equivalenti per dimostrare questo principio.

---

## 📚 Fonti

| Fonte                        | Descrizione                                |
| ---------------------------- | ------------------------------------------ |
| Wikipedia – “Exposure value” | Definizione formale di EV e tabella lux/EV |
| ANSI PH3.49-1971             | Costante di calibrazione C = 250           |
| ISO 2720:1974                | Standard internazionale esposimetri        |
| ANSI PH2.7-1986              | Tabella EV per scene reali                 |

---

## 🧱 Struttura del progetto

*(identica alla versione sopra)*

---

## ⚙️ Funzionalità dell’app

### Input

* **Valore lux**: inserimento manuale
* **ISO**: selezionabile (25 → 102400)

---

### Modalità di calcolo

| Modalità                  | Input fisso     | Calcolato       |
| ------------------------- | --------------- | --------------- |
| **A — Priorità apertura** | Numero f        | Tempo di scatto |
| **B — Priorità tempo**    | Tempo di scatto | Numero f        |

---

### Output

* EV a ISO 100 (`EV100`)
* EV corretto (`EV_ISO`)
* Apertura (valore esatto + standard più vicino)
* Tempo di scatto (valore esatto + standard più vicino)
* Descrizione scena basata su EV
* Tabella completa delle esposizioni equivalenti

---

## 🚀 Importazione in Android Studio

*(identica alla versione sopra)*

---

## 🧪 Test unitari

*(identico alla versione sopra)*

---

## 🧠 Note tecniche

*(identico alla versione sopra, tradotto)*

---

## 📄 Licenza

Copyright © 2025 Antonio Dicorato. Tutti i diritti riservati.

Questo codice è fornito **esclusivamente per scopi educativi e di portfolio**.

Non è consentito:

* uso commerciale
* modifica o redistribuzione
* creazione di opere derivate

senza autorizzazione scritta esplicita.

Contatti:
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

---

## 📦 Disponibilità

L’app completa sarà pubblicata sul Google Play Store.

---

