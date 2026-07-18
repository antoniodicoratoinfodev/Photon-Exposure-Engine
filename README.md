# Photon-Exposure-Engine (Lux Exposure Calculator)

**Android App (Java): Professional Photography Exposure Converter**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)
![Platform: Android 6.0+](https://img.shields.io/badge/Platform-Android%206.0%2B-green.svg)
![Language: Java 17](https://img.shields.io/badge/Language-Java%2017-orange.svg)

🇬🇧 **English** · [🇮🇹 Italiano](#photon-exposure-engine-lux-exposure-calculator-italiano)

> ⚠️ **IMPORTANT: READ THIS FIRST**
>
> This source code is made public **ONLY FOR INSPECTION AND TO SHOWCASE THE AUTHOR'S WORK ON GITHUB**.
> Public access does not permit copying or reusing the code, in whole or in part, in forks, personal or third-party projects, derivative works, products, or services.
> You are **NOT PERMITTED** to run, execute, deploy, or use this software for its intended purpose (exposure calculation) unless you purchase the official commercial version from the Google Play Store.  
> See the [LICENSE](./LICENSE.md) file for full legal terms.

Converts an illuminance value in lux, measured by a light meter, into the photographic exposure triad: **ISO**, **aperture (f-number)**, and **shutter speed**. It also corrects reciprocity failure for supported film stocks.

## 📱 About this repository

This repository contains the full source code for the **Photon-Exposure-Engine (Lux Exposure Calculator)** app.

It is published **solely** to:

* Showcase development skills
* Allow photographers and developers to inspect the code without reusing it
* Document the app's behavior and project structure

**The source code is not open source and is not licensed for reuse.** You may not use a fork or other reproduction of this repository as the basis for another project, copy or modify any part of the code, create derivative works, or incorporate it into your own or a third party's project, product, or service. This prohibition applies to personal, educational, professional, production, and commercial projects.

The official, ready-to-use version is available for purchase on the Google Play Store.

## 📸 Screenshots

Complete interface coverage in both light and dark themes.

| Screen | Dark theme | Light theme |
|---|---|---|
| Digital workspace | <a href="screenshots/dark_01_digital_top.png"><img src="screenshots/dark_01_digital_top.png" alt="Digital workspace in dark theme" width="220"></a> | <a href="screenshots/light_01_digital_top.png"><img src="screenshots/light_01_digital_top.png" alt="Digital workspace in light theme" width="220"></a> |
| Digital result | <a href="screenshots/dark_02_digital_results.png"><img src="screenshots/dark_02_digital_results.png" alt="Digital result in dark theme" width="220"></a> | <a href="screenshots/light_02_digital_results.png"><img src="screenshots/light_02_digital_results.png" alt="Digital result in light theme" width="220"></a> |
| Exposure map | <a href="screenshots/dark_03_digital_exposuremap.png"><img src="screenshots/dark_03_digital_exposuremap.png" alt="Exposure map in dark theme" width="220"></a> | <a href="screenshots/light_03_digital_exposuremap.png"><img src="screenshots/light_03_digital_exposuremap.png" alt="Exposure map in light theme" width="220"></a> |
| Analog workspace | <a href="screenshots/dark_06_analog_top.png"><img src="screenshots/dark_06_analog_top.png" alt="Analog workspace in dark theme" width="220"></a> | <a href="screenshots/light_06_analog_top.png"><img src="screenshots/light_06_analog_top.png" alt="Analog workspace in light theme" width="220"></a> |
| Analog result | <a href="screenshots/dark_07_analog_result.png"><img src="screenshots/dark_07_analog_result.png" alt="Analog result in dark theme" width="220"></a> | <a href="screenshots/light_07_analog_result.png"><img src="screenshots/light_07_analog_result.png" alt="Analog result in light theme" width="220"></a> |
| Save exposure dialog | <a href="screenshots/dark_08_dialog_save.png"><img src="screenshots/dark_08_dialog_save.png" alt="Save exposure dialog in dark theme" width="220"></a> | <a href="screenshots/light_08_dialog_save.png"><img src="screenshots/light_08_dialog_save.png" alt="Save exposure dialog in light theme" width="220"></a> |
| Saved exposures | <a href="screenshots/dark_09_saved.png"><img src="screenshots/dark_09_saved.png" alt="Saved exposures in dark theme" width="220"></a> | <a href="screenshots/light_09_saved.png"><img src="screenshots/light_09_saved.png" alt="Saved exposures in light theme" width="220"></a> |
| Saved exposure detail | <a href="screenshots/dark_10_dialog_detail.png"><img src="screenshots/dark_10_dialog_detail.png" alt="Saved exposure detail in dark theme" width="220"></a> | <a href="screenshots/light_10_dialog_detail.png"><img src="screenshots/light_10_dialog_detail.png" alt="Saved exposure detail in light theme" width="220"></a> |

## ⚙️ App features

### Input

* **Lux value**: manual input from light meter
* **ISO**: selectable (25 → 102400)
* **Digital / Analog tabs**: Analog adds film selection and reciprocity correction
* **Film stock**: 21 selectable stocks (Ilford, Kentmere, Kodak, Fujifilm, Foma, generic B&W)

### Calculation modes

| Mode                      | Fixed input   | Calculated    |
| ------------------------- | ------------- | ------------- |
| **A: Aperture Priority** | f-number      | Shutter speed |
| **B: Shutter Priority**  | Shutter speed | f-number      |

### Output

* Recommended aperture and shutter speed
* Reciprocity-corrected time on the Analog tab, with a flag when the value is extrapolated beyond published data
* Scene description

### Screens

| Screen | Purpose |
|---|---|
| `MainActivity` | Exposure calculator, Digital/Analog tabs, dark-theme toggle |
| `SavedExposuresActivity` | Log of saved exposures, persisted locally |

## 🧱 Project structure

```
Photon-Exposure-Engine/
├── app/
│   ├── build.gradle
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/photography/luxexposimeter/
│       │   │   ├── ExposureCalculator.java      # exposure calculator
│       │   │   ├── FilmStock.java               # film stock definitions
│       │   │   ├── ReciprocityCalculator.java   # film compensation logic
│       │   │   ├── ExposureLogStore.java        # persistence of saved exposures
│       │   │   ├── SavedExposure.java           # saved-exposure model
│       │   │   ├── MainActivity.java            # calculator screen
│       │   │   └── SavedExposuresActivity.java  # saved-exposure log
│       │   └── res/
│       │       ├── layout/                      # activity, dialog, item layouts
│       │       ├── values/                      # strings, colors, themes
│       │       ├── values-night/colors.xml      # dark theme palette
│       │       ├── drawable/                    # icons and backgrounds
│       │       └── mipmap-*/                    # launcher icons
│       └── test/java/com/photography/luxexposimeter/
│           ├── ExposureCalculatorTest.java
│           └── ReciprocityCalculatorTest.java
├── Samples/                                     # screenshots
├── build.gradle
├── settings.gradle
├── gradle.properties
├── LICENSE.md
└── README.md
```

## 🚀 How to import in Android Studio

1. Open Android Studio
2. **File → Open** → select the `Photon-Exposure-Engine/` folder
3. Wait for Gradle sync
4. Connect a device (API 23+) or start an emulator
5. Press **Run** ▶

> **Reminder:** Importing and building the project is allowed for code inspection only.  
> Running the compiled app on a device is **prohibited** without purchasing the commercial version.

### Requirements

* Android Studio version compatible with **AGP 9.2.1** (see the
  [AGP / Android Studio compatibility table](https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility))
* Android Gradle Plugin 9.2.1 · Gradle 9.6.1
* compileSdk / targetSdk 37
* minSdk 23 (Android 6.0 Marshmallow)
* Java 17

## 🧪 Unit tests

The unit-test suites cover the exposure calculator and Analog-tab behavior.
Both suites are pure Java (no Android dependencies). Run them via Gradle:

```bash
./gradlew testDebugUnitTest
```

Each class also exposes a `main()` that prints a per-assertion pass/fail report,
so the suites can be run standalone without Gradle or the Android SDK. Only the
JUnit jar is needed on the classpath:

```bash
JUNIT=$(find ~/.gradle/caches -name 'junit-4.13.2.jar' | head -1)
cd app/src/main/java/com/photography/luxexposimeter
javac -cp "$JUNIT" -d /tmp/pee \
      ExposureCalculator.java FilmStock.java ReciprocityCalculator.java \
      ../../../../../test/java/com/photography/luxexposimeter/*.java
java -cp "/tmp/pee:$JUNIT" com.photography.luxexposimeter.ExposureCalculatorTest
java -cp "/tmp/pee:$JUNIT" com.photography.luxexposimeter.ReciprocityCalculatorTest
```

## 📄 License

Copyright © 2026 Antonio Dicorato. All rights reserved.

This source code is made publicly available on GitHub **solely for inspection and to showcase the author's work**. Its presence in a public repository does not make it open source or grant permission to reuse the code.

**You are strictly PROHIBITED from:**

- Running, executing, or deploying this software for any purpose.
- Using the application for its intended photographic exposure calculation purpose or any similar real-world functionality.
- Using a fork or other reproduction of the repository as the basis for another project, or copying, modifying, or creating derivative works from the code or any portion of it.
- Incorporating the code, in whole or in part, into personal or third-party projects, products, services, coursework submissions, internal tools, or production systems.
- Distributing, sublicensing, or making the software available to third parties in any compiled or executable form.
- Using or exploiting the source code or application for commercial, professional, or business purposes without an authorized purchase or a separate written license.

If you wish to use this application for its intended purpose (exposure calculation, photography assistance, or any practical functionality), you **MUST purchase the commercial version** from the Google Play Store. The commercial version is the only authorized way to use the software as an application.

For commercial licensing or permission requests:  
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

### Disclaimer

This software is provided *"as is"*, without warranty of any kind.  
The author is not liable for any damages arising from its use.

## 📦 App availability

The full application will be available for purchase on the Google Play Store.

# Photon-Exposure-Engine (Lux Exposure Calculator) Italiano

**App Android (Java): Convertitore Professionale per Esposizione Fotografica**

![License: Proprietary](https://img.shields.io/badge/License-Proprietary-red.svg)
![Platform: Android 6.0+](https://img.shields.io/badge/Platform-Android%206.0%2B-green.svg)
![Language: Java 17](https://img.shields.io/badge/Language-Java%2017-orange.svg)

[🇬🇧 English](#photon-exposure-engine-lux-exposure-calculator) · 🇮🇹 **Italiano**

> ⚠️ **IMPORTANTE: LEGGI PRIMA**
>
> Questo codice sorgente è reso pubblico **SOLO PER ISPEZIONARLO E MOSTRARE IL LAVORO DELL'AUTORE SU GITHUB**.
> L'accesso pubblico non autorizza a copiare o riutilizzare il codice, in tutto o in parte, in fork, progetti propri o di terzi, opere derivate, prodotti o servizi.
> **NON È CONSENTITO** eseguire, avviare, distribuire o usare questo software per il suo scopo principale (calcolo dell'esposizione) a meno che non si acquisti la versione commerciale ufficiale dal Google Play Store.  
> Consulta il file [LICENSE](./LICENSE.md) per i termini legali completi.

Converte un valore di illuminamento in lux, misurato con un luxmetro, nella triade fotografica dell'esposizione: **ISO**, **apertura (f-number)** e **tempo di esposizione**. Corregge inoltre il difetto di reciprocità per le pellicole supportate.

## 📱 Informazioni su questo repository

Questo repository contiene il codice sorgente completo dell'app **Photon-Exposure-Engine (Lux Exposure Calculator)**.

È pubblicato **esclusivamente** per:

* Mostrare competenze di sviluppo
* Consentire a fotografi e sviluppatori di ispezionare il codice senza riutilizzarlo
* Documentare il comportamento dell'app e la struttura del progetto

**Il codice sorgente non è open source e non è concesso in licenza per il riutilizzo.** Non è consentito usare un fork o un'altra riproduzione del repository come base per un altro progetto, copiare o modificare parti del codice, creare opere derivate oppure incorporarlo in progetti, prodotti o servizi propri o di terzi. Il divieto si applica a progetti personali, didattici, professionali, di produzione e commerciali.

La versione ufficiale e pronta all'uso è disponibile per l'acquisto su Google Play Store.

## 📸 Screenshot

| | | |
|---|---|---|
| ![Screenshot 1](Samples/1.png) | ![Screenshot 2](Samples/2.png) | ![Screenshot 3](Samples/3.png) |

## ⚙️ Funzionalità dell'app

### Input

* **Valore in lux**: inserimento manuale dal luxmetro
* **ISO**: selezionabile (25 → 102400)
* **Tab Digital / Analog**: il tab Analog aggiunge la scelta della pellicola e la correzione di reciprocità
* **Pellicola**: 21 emulsioni selezionabili (Ilford, Kentmere, Kodak, Fujifilm, Foma, B&N generica)

### Modalità di calcolo

| Modalità                    | Input fisso          | Calcolato            |
| --------------------------- | -------------------- | -------------------- |
| **A: Priorità Apertura**   | f-number             | Tempo di esposizione |
| **B: Priorità Otturatore** | Tempo di esposizione | f-number             |

### Output

* Apertura e tempo di esposizione consigliati
* Tempo corretto per la reciprocità nel tab Analog, con segnalazione quando il valore è estrapolato oltre i dati pubblicati
* Descrizione della scena

### Schermate

| Schermata | Scopo |
|---|---|
| `MainActivity` | Calcolatore di esposizione, tab Digital/Analog, interruttore tema scuro |
| `SavedExposuresActivity` | Registro delle esposizioni salvate, persistito localmente |

## 🧱 Struttura del progetto

```
Photon-Exposure-Engine/
├── app/
│   ├── build.gradle
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/photography/luxexposimeter/
│       │   │   ├── ExposureCalculator.java      # calcolatore di esposizione
│       │   │   ├── FilmStock.java               # definizioni delle pellicole
│       │   │   ├── ReciprocityCalculator.java   # logica di compensazione
│       │   │   ├── ExposureLogStore.java        # persistenza esposizioni salvate
│       │   │   ├── SavedExposure.java           # modello esposizione salvata
│       │   │   ├── MainActivity.java            # schermata calcolatore
│       │   │   └── SavedExposuresActivity.java  # registro esposizioni salvate
│       │   └── res/
│       │       ├── layout/                      # layout activity, dialog, item
│       │       ├── values/                      # stringhe, colori, temi
│       │       ├── values-night/colors.xml      # palette tema scuro
│       │       ├── drawable/                    # icone e sfondi
│       │       └── mipmap-*/                    # icone launcher
│       └── test/java/com/photography/luxexposimeter/
│           ├── ExposureCalculatorTest.java
│           └── ReciprocityCalculatorTest.java
├── Samples/                                     # screenshot
├── build.gradle
├── settings.gradle
├── gradle.properties
├── LICENSE.md
└── README.md
```

## 🚀 Come importare in Android Studio

1. Aprire Android Studio
2. **File → Open** → selezionare la cartella `Photon-Exposure-Engine/`
3. Attendere la sincronizzazione di Gradle
4. Collegare un dispositivo (API 23+) o avviare un emulatore
5. Premere **Run** ▶

> **Promemoria:** L'importazione e la compilazione del progetto sono consentite solo per ispezione del codice.  
> L'esecuzione dell'app compilata su un dispositivo è **vietata** senza l'acquisto della versione commerciale.

### Requisiti

* Versione di Android Studio compatibile con **AGP 9.2.1** (vedi la
  [tabella di compatibilità AGP / Android Studio](https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility))
* Android Gradle Plugin 9.2.1 · Gradle 9.6.1
* compileSdk / targetSdk 37
* minSdk 23 (Android 6.0 Marshmallow)
* Java 17

## 🧪 Test unitari

Le suite di test coprono il calcolatore di esposizione e il comportamento del
tab Analog. Entrambe sono in Java puro (nessuna dipendenza Android). Esecuzione con Gradle:

```bash
./gradlew testDebugUnitTest
```

Ogni classe espone anche un `main()` che stampa un report pass/fail per singola
asserzione, quindi le suite si possono eseguire in standalone senza Gradle né
Android SDK. Serve solo il jar di JUnit nel classpath:

```bash
JUNIT=$(find ~/.gradle/caches -name 'junit-4.13.2.jar' | head -1)
cd app/src/main/java/com/photography/luxexposimeter
javac -cp "$JUNIT" -d /tmp/pee \
      ExposureCalculator.java FilmStock.java ReciprocityCalculator.java \
      ../../../../../test/java/com/photography/luxexposimeter/*.java
java -cp "/tmp/pee:$JUNIT" com.photography.luxexposimeter.ExposureCalculatorTest
java -cp "/tmp/pee:$JUNIT" com.photography.luxexposimeter.ReciprocityCalculatorTest
```

## 📄 Licenza

Copyright © 2026 Antonio Dicorato. Tutti i diritti riservati.

Questo codice sorgente è reso pubblico su GitHub **esclusivamente per consentirne l'ispezione e mostrare il lavoro dell'autore**. La presenza in un repository pubblico non lo rende open source e non concede alcun permesso di riutilizzo.

**È SEVERAMENTE VIETATO:**

- Eseguire, avviare o distribuire questo software per qualsiasi scopo.
- Utilizzare l'applicazione per il suo scopo previsto (calcolo dell'esposizione fotografica o qualsiasi funzionalità simile nel mondo reale).
- Usare un fork o un'altra riproduzione del repository come base per un altro progetto, oppure copiare, modificare o creare opere derivate dal codice o da qualsiasi sua parte.
- Incorporare il codice, in tutto o in parte, in progetti, prodotti, servizi, elaborati didattici, strumenti interni o sistemi di produzione propri o di terzi.
- Distribuire, concedere in sublicenza o mettere il software a disposizione di terzi in forma compilata o eseguibile.
- Utilizzare o sfruttare il codice sorgente o l'applicazione per scopi commerciali, professionali o aziendali senza un acquisto autorizzato o una licenza scritta separata.

Se desideri utilizzare questa applicazione per il suo scopo principale (calcolo dell'esposizione, assistenza fotografica o qualsiasi funzionalità pratica), **DEVI ACQUISTARE la versione commerciale** dal Google Play Store. La versione commerciale è l'unico modo autorizzato per usare il software come applicazione.

Per richieste di licenza commerciale o autorizzazioni:  
**[antoniodicoratoinfodev@gmail.com](mailto:antoniodicoratoinfodev@gmail.com)**

### Disclaimer

Questo software è fornito *"così com'è"*, senza alcuna garanzia.  
L'autore non è responsabile per eventuali danni derivanti dal suo utilizzo.

## 📦 Disponibilità dell'app

L'applicazione completa sarà disponibile per l'acquisto sul Google Play Store.
