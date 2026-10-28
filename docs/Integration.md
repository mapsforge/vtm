# Integration guide

Clone the project and run the Gradle task `publishToMavenLocal`.

Add the repository:

```groovy
repositories {
    mavenLocal()
}
```

## Map

```groovy
implementation 'org.mapsforge:vtm:master-SNAPSHOT@jar'
implementation 'org.mapsforge:vtm-themes:master-SNAPSHOT@jar'
```

## Android

```groovy
implementation 'org.mapsforge:vtm-android:master-SNAPSHOT@jar'
runtimeOnly 'org.mapsforge:vtm-android:master-SNAPSHOT:natives-armeabi-v7a@jar'
runtimeOnly 'org.mapsforge:vtm-android:master-SNAPSHOT:natives-arm64-v8a@jar'
runtimeOnly 'org.mapsforge:vtm-android:master-SNAPSHOT:natives-x86@jar'
runtimeOnly 'org.mapsforge:vtm-android:master-SNAPSHOT:natives-x86_64@jar'
implementation 'com.caverock:androidsvg:1.4'
```

## Desktop

```groovy
implementation 'org.mapsforge:vtm-gdx:master-SNAPSHOT@jar'
implementation 'org.mapsforge:vtm-desktop:master-SNAPSHOT@jar'
runtimeOnly 'org.mapsforge:vtm-desktop:master-SNAPSHOT:natives-linux@jar'
runtimeOnly 'org.mapsforge:vtm-desktop:master-SNAPSHOT:natives-osx@jar'
runtimeOnly 'org.mapsforge:vtm-desktop:master-SNAPSHOT:natives-windows@jar'
implementation 'com.badlogicgames.gdx:gdx:1.11.0'
runtimeOnly 'com.badlogicgames.gdx:gdx-platform:1.11.0:natives-desktop'
implementation 'guru.nidi.com.kitfox:svgSalamander:1.1.3'
implementation 'net.sf.kxml:kxml2:2.3.0'
```

### LWJGL 2

```groovy
implementation 'org.mapsforge:vtm-desktop-lwjgl:master-SNAPSHOT@jar'
implementation 'com.badlogicgames.gdx:gdx-backend-lwjgl:1.11.0'
implementation 'org.lwjgl.lwjgl:lwjgl:2.9.3'
runtimeOnly 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-linux'
runtimeOnly 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-osx'
runtimeOnly 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-windows'
```

### LWJGL 3

```groovy
implementation 'org.mapsforge:vtm-desktop-lwjgl3:master-SNAPSHOT@jar'
implementation 'com.badlogicgames.gdx:gdx-backend-lwjgl3:1.11.0'
implementation 'org.lwjgl:lwjgl:3.3.1'
runtimeOnly 'org.lwjgl:lwjgl:3.3.1:natives-linux'
runtimeOnly 'org.lwjgl:lwjgl:3.3.1:natives-macos'
runtimeOnly 'org.lwjgl:lwjgl:3.3.1:natives-windows'
```

## Features

### Hillshading

```groovy
implementation 'org.mapsforge:vtm-hillshading:master-SNAPSHOT@jar'
implementation 'org.mapsforge:mapsforge-core:master-SNAPSHOT@jar'
implementation 'org.mapsforge:mapsforge-map:master-SNAPSHOT@jar'
implementation 'org.mapsforge:mapsforge-map-android:master-SNAPSHOT@jar'
```

### Overlays

```groovy
implementation 'org.mapsforge:vtm-jts:master-SNAPSHOT@jar'
implementation 'org.locationtech.jts:jts-core:1.20.0'
```

### Online tiles

```groovy
implementation 'org.mapsforge:vtm-http:master-SNAPSHOT@jar'
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
implementation 'com.squareup.okio:okio:3.6.0'
implementation 'com.squareup.okio:okio-jvm:3.6.0'
```

### MBTiles

```groovy
implementation 'org.mapsforge:vtm-mvt:master-SNAPSHOT@jar'
implementation 'org.mapsforge:vtm-android-mvt:master-SNAPSHOT@jar'
implementation 'com.google.protobuf:protobuf-java:3.24.2'
implementation 'io.github.ci-cmg:mapbox-vector-tile:4.0.6'
implementation 'org.locationtech.jts:jts-core:1.20.0'
```

### Mapbox vector tiles

```groovy
implementation 'org.mapsforge:vtm-mvt:master-SNAPSHOT@jar'
implementation 'com.google.protobuf:protobuf-java:3.24.2'
implementation 'io.github.ci-cmg:mapbox-vector-tile:4.0.6'
implementation 'org.locationtech.jts:jts-core:1.20.0'
```

### GeoJSON vector tiles

```groovy
implementation 'org.mapsforge:vtm-json:master-SNAPSHOT@jar'
implementation 'com.fasterxml.jackson.core:jackson-annotations:2.9.9'
implementation 'com.fasterxml.jackson.core:jackson-core:2.9.9'
implementation 'com.fasterxml.jackson.core:jackson-databind:2.9.9'
```
