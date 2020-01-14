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
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-armeabi-v7a'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-arm64-v8a'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86_64'
implementation 'com.caverock:androidsvg:1.4'
```

### Android (libGDX)
```groovy
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-armeabi-v7a'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-arm64-v8a'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86'
implementation 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86_64'
implementation 'org.mapsforge:vtm-gdx:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-armeabi-v7a'
implementation 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-arm64-v8a'
implementation 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-x86'
implementation 'org.mapsforge:vtm-android-gdx:[CURRENT-VERSION]:natives-x86_64'
implementation 'com.badlogicgames.gdx:gdx:1.9.10'
implementation 'com.badlogicgames.gdx:gdx-backend-android:1.9.10'
implementation 'com.caverock:androidsvg:1.4'
```

### iOS

Detailed iOS instructions can be found [here](ios.md).

### Desktop
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

implementation 'org.mapsforge:vtm-gdx:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]'
implementation 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-linux'
implementation 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-osx'
implementation 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-windows'
implementation 'org.mapsforge:vtm-desktop-lwjgl:[CURRENT-VERSION]'
implementation 'com.badlogicgames.gdx:gdx:1.9.10'
implementation 'com.badlogicgames.gdx:gdx-platform:1.9.10:natives-desktop'
implementation 'com.github.blackears:svgSalamander:v1.1.1'
```

### Desktop (LWJGL)
```groovy
implementation 'com.badlogicgames.gdx:gdx-backend-lwjgl:1.9.10'
implementation 'org.lwjgl.lwjgl:lwjgl:2.9.3'
implementation 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-linux'
implementation 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-osx'
implementation 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-windows'
```

### Desktop (LWJGL 3)
```groovy
implementation 'com.badlogicgames.gdx:gdx-backend-lwjgl3:1.9.10'
implementation 'org.lwjgl:lwjgl:3.2.3'
implementation 'org.lwjgl:lwjgl:3.2.3:natives-linux'
implementation 'org.lwjgl:lwjgl:3.2.3:natives-macos'
implementation 'org.lwjgl:lwjgl:3.2.3:natives-windows'
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
implementation 'com.squareup.okhttp3:okhttp:3.12.5'
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

Add _first_ the Boundless repository:
```groovy
repositories {
    maven { url 'https://repo.boundlessgeo.com/main/' }
    jcenter()
    ...
}
```

```groovy
implementation 'org.mapsforge:vtm-jeo:[CURRENT-VERSION]'
implementation('org.jeo:jeo:0-SNAPSHOT') {
    exclude group: 'org.slf4j', module: 'slf4j-jdk14'
}
implementation('org.jeo:jeo-carto:0-SNAPSHOT') {
    exclude group: 'org.slf4j', module: 'slf4j-jdk14'
}
implementation('org.jeo:jeo-render:0-SNAPSHOT') {
    exclude group: 'org.slf4j', module: 'slf4j-jdk14'
}
implementation 'org.osgeo:proj4j:0.1.0:jeo'
implementation 'com.metaweb:lessen:1.0'
implementation 'com.vividsolutions:jts:1.13'
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
