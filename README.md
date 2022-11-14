[![Maven Central](https://img.shields.io/maven-central/v/org.mapsforge/vtm.svg)](https://search.maven.org/search?q=g:org.mapsforge)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](http://www.gnu.org/licenses/lgpl-3.0)

# V™

<img src="docs/logo/VTM.svg" style="bottom:0;" width="200" align="right">

VTM (Vector Tile Map) was developed within the [OpenScienceMap](https://github.com/opensciencemap) project.

**This fork continues VTM development and provides compatibility with [Mapsforge](https://github.com/mapsforge/mapsforge).**

See the **[integration guide](docs/Integration.md)** and [changelog](docs/Changelog.md). And read through [how to contribute](docs/CONTRIBUTING.md) guidelines.

If you have any questions or problems, don't hesitate to ask the Discussions for help.

## Features
- Java map library
- OpenGL vector-tile rendering
- Themeable vector layers ([render themes](docs/Rendertheme.md))
- Support for multiple tile sources:
  - OpenScienceMap vector tiles
  - Mapsforge vector maps
  - MBTiles vector & raster maps
  - Mapbox vector tiles (e.g. Mapilion, Mapzen, Nextzen, OpenMapTiles)
  - GeoJSON vector tiles (e.g. Mapzen, Nextzen)
  - Raster tiles: any quadtree-scheme tiles as texture
- Backends:
  - Android ([example](vtm-android-example/src/org/oscim/android/test/GettingStarted.java))
  - iOS (libGDX/RoboVM, [instructions](docs/ios.md))
  - Desktop (libGDX/LWJGL, [instructions](docs/desktop.md))
  - HTML5/WebGL (libGDX/GWT, [instructions](docs/web.md))
- Building VTM: set `ANDROID_HOME` environment variable with the Android SDK installation directory

### Projects
- **vtm** core library
- **vtm-android** Android backend
- **vtm-android-example** Android examples
- **vtm-gdx** common libGDX backend
- **vtm-android-gdx** Android libGDX backend
- **vtm-desktop** Desktop libGDX backend
- **vtm-desktop-lwjgl** Desktop LWJGL backend
- **vtm-desktop-lwjgl3** Desktop LWJGL 3 backend
- **vtm-playground** Desktop examples
- **vtm-ios** iOS libGDX backend
- **vtm-ios-example** iOS examples
- **vtm-web** HTML5/GWT libGDX backend
- **vtm-web-app** HTML5/GWT application

## Applications
- VTM is used by many [applications](docs/Applications.md).

## Maps
- Mapsforge [map providers](docs/Mapsforge-Maps.md).

## Credits
This library contains code from several projects:
- **Android** (Apache 2.0): some Matrix code, TimSort (http://source.android.com)
- **libGDX** (Apache 2.0): AsyncTask, MathUtils, Interpolation, PixmapPacker (https://github.com/libgdx)
- **mapsforge** (LGPL3): based on 0.2.4 (https://github.com/mapsforge/mapsforge)
- **osmdroid** (Apache 2.0): some overlay classes (https://github.com/osmdroid/osmdroid)
- **tessellate** (SGI Free Software License B 2.0): (https://github.com/cscheid/tessellate)

## License

VTM library is under [LGPL v3 license](http://www.gnu.org/licenses/lgpl-3.0), with an important simplification: the constraints mentioned in sections LGPL v3 §4(d) and §4(e) are waived.

This means that you are allowed to convey a Combined Work without providing the user any way to recombine or relink the application and without providing any shared library mechanism.

In other words, you are allowed to include VTM library in your Android application, without making your application open source.

## Screenshots

| Android       | iOS           |
| ------------- | ------------- |
|<img src="docs/images/android.png" width="1000">|<img src="docs/images/ios.png" width="1000">|

| Desktop       |
| ------------- |
|![Desktop](docs/images/desktop.png)|

| Browser       |
| ------------- |
|![Browser](docs/images/browser.png)|
