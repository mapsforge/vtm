# Integration guide

This article describes how to integrate the library in your project, with Gradle / Maven / Jars or SNAPSHOT builds.

Current version is [![Maven Central](https://img.shields.io/maven-central/v/org.mapsforge/vtm.svg)](https://search.maven.org/search?q=g:org.mapsforge)

## Gradle

### Core
```groovy
implementation 'org.mapsforge:vtm:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-themes:[CURRENT-VERSION]'
implementation 'org.slf4j:slf4j-api:1.7.28'
```

### Android
```groovy
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-armeabi-v7a'
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-arm64-v8a'
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86'
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86_64'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]'
implementation 'com.caverock:androidsvg:1.4'
```

### Android (libGDX)
```groovy
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-armeabi-v7a'
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-arm64-v8a'
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86'
runtimeOnly 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86_64'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-gdx:[CURRENT-VERSION]'
runtimeOnly 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-armeabi-v7a'
runtimeOnly 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-arm64-v8a'
runtimeOnly 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-x86'
runtimeOnly 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-x86_64'
implementation 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]'
implementation 'com.badlogicgames.gdx:gdx:1.11.0'
implementation 'com.badlogicgames.gdx:gdx-backend-android:1.11.0'
implementation 'com.caverock:androidsvg:1.4'
```

### iOS

Detailed iOS instructions can be found [here](ios.md).

### Desktop
```groovy
implementation 'org.mapsforge:vtm-gdx:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]'
runtimeOnly 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-linux'
runtimeOnly 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-osx'
runtimeOnly 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-windows'
implementation 'com.badlogicgames.gdx:gdx:1.11.0'
runtimeOnly 'com.badlogicgames.gdx:gdx-platform:1.11.0:natives-desktop'
implementation 'guru.nidi.com.kitfox:svgSalamander:1.1.3'
implementation 'net.sf.kxml:kxml2:2.3.0'
```

### Desktop (LWJGL)
```groovy
implementation 'org.mapsforge:vtm-desktop-lwjgl:[CURRENT-VERSION]'
implementation 'com.badlogicgames.gdx:gdx-backend-lwjgl:1.11.0'
implementation 'org.lwjgl.lwjgl:lwjgl:2.9.3'
runtimeOnly 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-linux'
runtimeOnly 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-osx'
runtimeOnly 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-windows'
```

### Desktop (LWJGL 3)
```groovy
implementation 'org.mapsforge:vtm-desktop-lwjgl3:[CURRENT-VERSION]'
implementation 'com.badlogicgames.gdx:gdx-backend-lwjgl3:1.11.0'
implementation 'org.lwjgl:lwjgl:3.3.1'
runtimeOnly 'org.lwjgl:lwjgl:3.3.1:natives-linux'
runtimeOnly 'org.lwjgl:lwjgl:3.3.1:natives-macos'
runtimeOnly 'org.lwjgl:lwjgl:3.3.1:natives-windows'
```

### JTS geometries

```groovy
implementation 'org.mapsforge:vtm-jts:[CURRENT-VERSION]'
// https://github.com/locationtech/jts/issues/145
implementation 'org.locationtech.jts:jts-core:1.15.1'
```

### Online tiles

```groovy
implementation 'org.mapsforge:vtm-http:[CURRENT-VERSION]'
// https://github.com/square/okhttp/issues/4481
implementation 'com.squareup.okhttp3:okhttp:3.12.13'
implementation 'com.squareup.okio:okio:1.15.0'
```

### MBTiles (Android)

```groovy
implementation 'org.mapsforge:vtm-android-mvt:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-mvt:[CURRENT-VERSION]'
implementation 'com.google.protobuf:protobuf-java:3.6.1'
implementation 'com.wdtinc:mapbox-vector-tile:3.1.0'
// https://github.com/locationtech/jts/issues/145
implementation 'org.locationtech.jts:jts-core:1.15.1'
```

### Mapbox vector tiles

```groovy
implementation 'org.mapsforge:vtm-mvt:[CURRENT-VERSION]'
implementation 'com.google.protobuf:protobuf-java:3.6.1'
implementation 'com.wdtinc:mapbox-vector-tile:3.1.0'
// https://github.com/locationtech/jts/issues/145
implementation 'org.locationtech.jts:jts-core:1.15.1'
```

### GeoJSON vector tiles

```groovy
implementation 'org.mapsforge:vtm-json:[CURRENT-VERSION]'
implementation 'com.fasterxml.jackson.core:jackson-annotations:2.9.9'
implementation 'com.fasterxml.jackson.core:jackson-core:2.9.9'
implementation 'com.fasterxml.jackson.core:jackson-databind:2.9.9'
```

### jeo (indoor maps)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

implementation 'org.mapsforge:vtm-jeo:[CURRENT-VERSION]'
implementation('com.github.jeo.jeo:jeo-carto:master-SNAPSHOT') {
    exclude group: 'org.slf4j', module: 'slf4j-jdk14'
}
```

## Snapshots

We publish SNAPSHOT builds to Sonatype OSS Repository Hosting.

You need to add the repository:
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}
```

And declare the dependencies like:
```groovy
implementation 'org.mapsforge:vtm:master-SNAPSHOT'
...
```

For checking latest snapshot on every build:
```groovy
configurations.all {
    resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
}
```

## Maven

The dependencies for Maven are declared in a similar way. For example:

```xml
<dependency>
    <groupId>org.mapsforge</groupId>
    <artifactId>vtm</artifactId>
    <version>[CURRENT-VERSION]</version>
</dependency>
```

## Jars

You can find release and snapshot jars (regular and with dependencies) in [Maven Central](https://search.maven.org/search?q=g:org.mapsforge) and [Sonatype OSS Repository Hosting](https://oss.sonatype.org/content/repositories/snapshots/org/mapsforge/).

Third party jars can be found at their respective sites or in Maven Central repository.
