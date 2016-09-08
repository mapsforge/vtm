# Integration guide

This article describes how to integrate the library in your project. Check for current version at Maven badge on main page.

## Gradle

### Core
```groovy
compile 'org.mapsforge:vtm:[CURRENT-VERSION]'
compile 'org.mapsforge:vtm-themes:[CURRENT-VERSION]'
compile 'org.slf4j:slf4j-api:1.7.21'
```

### Android
```groovy
compile 'org.mapsforge:vtm-android:[CURRENT-VERSION]'
compile 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-armeabi'
compile 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-armeabi-v7a'
compile 'org.mapsforge:vtm-android:[CURRENT-VERSION]:natives-x86'
compile 'com.caverock:androidsvg:1.2.2-beta-1'
```

### iOS

Detailed iOS instructions can be found [here](ios.md).

### Java
```groovy
compile 'org.mapsforge:vtm-gdx:[CURRENT-VERSION]'
compile 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]'
compile 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-linux'
compile 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-osx'
compile 'org.mapsforge:vtm-desktop:[CURRENT-VERSION]:natives-windows'
compile 'com.badlogicgames.gdx:gdx:1.9.3'
compile 'com.badlogicgames.gdx:gdx-platform:1.9.3:natives-desktop'
compile 'com.badlogicgames.gdx:gdx-backend-lwjgl:1.9.3'
compile 'org.lwjgl.lwjgl:lwjgl:2.9.3'
compile 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-linux'
compile 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-osx'
compile 'org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-windows'
compile 'com.kitfox.svg:svg-salamander:1.0'
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

## JitPack

We support also [JitPack](https://jitpack.io/#mapsforge/vtm) for publishing. This can be used for the releases, but it's also useful for integrating SNAPSHOT builds in your application (not available in Maven Central).

For example in order to include the `vtm` module `master-SNAPSHOT` with Gradle.

Add as repository:
```groovy
maven { url "https://jitpack.io" }
```

And declare as dependency:
```groovy
compile 'com.github.mapsforge.vtm:vtm:master-SNAPSHOT'
```

The same syntax applies for all modules. And with similar way you can declare the dependencies in Maven too.

## Jars

You can find jars (regular and with dependencies) in our [Jenkins CI server](http://ci.mapsforge.org/).

Third party jars can be found at their respective sites or in Maven Central repository.
