# Programming and Modelling - WiSe 2022/2023

## Project for 6CP: Uno

This is a JavaFX application built with Gradle. The project uses JavaFX 17 and the Gradle wrapper uses Gradle 7.5.1. Use **JDK 17** to build and run this version of the project. Maven is not needed.

## Build and run from the terminal

Clone the repository and enter its directory:

```bash
git clone https://github.com/MusLead/UNO_PuM.git
cd UNO_PuM
```

Install JDK 17 on macOS with Homebrew, if needed, and select it for the current terminal session:

```bash
brew install openjdk@17
export JAVA_HOME="$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

The version shown should start with `17`. Then compile and launch the game:

```bash
./gradlew classes
./gradlew run
```

The included `./gradlew` wrapper downloads the project's Gradle version automatically; a separate Gradle installation is not required. The entry point is `de.uniks.pmws2223.uno.Main`.

## Create a macOS app

These steps run on macOS with JDK 17 selected as shown above. First, collect the application JAR and its runtime dependencies:

```bash
./gradlew installDist
```

Then create a self-contained `UNO.app` bundle with the JDK's `jpackage` tool:

```bash
jpackage \
  --type app-image \
  --name UNO \
  --input build/install/PMWS2223_uno/lib \
  --main-jar PMWS2223_uno-1.0.0.jar \
  --main-class de.uniks.pmws2223.uno.Main \
  --dest dist
```

Run the result:

```bash
open dist/UNO.app
```

The app bundle includes a Java runtime, so installing Java separately is not required to run this packaged app. If the project version in `build.gradle` changes, update the filename passed to `--main-jar` accordingly. To check the actual filename, run `ls build/install/PMWS2223_uno/lib/PMWS2223_uno*.jar`.

`jpackage` will not overwrite an existing `dist/UNO.app`. For a new build, choose a new destination such as `--dest dist-new` and test that new app before distributing it.

## ZIP the app for a GitHub Release

After testing `dist/UNO.app`, create a ZIP of the **whole** app bundle:

```bash
ditto -c -k --sequesterRsrc --keepParent \
  dist/UNO.app dist/UNO-macOS.zip
```

Attach `dist/UNO-macOS.zip` to a release on the repository's [Releases page](https://github.com/MusLead/UNO_PuM/releases). The automatically generated GitHub source archives contain source code; the attached ZIP contains the runnable macOS app. Build separate app bundles on Apple Silicon and Intel Macs if you want to distribute to both architectures.
