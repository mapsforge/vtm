[![Maven Central](https://img.shields.io/maven-central/v/org.mapsforge/vtm.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.mapsforge%22)
[![Build Status](https://travis-ci.org/mapsforge/vtm.svg?branch=master)](https://travis-ci.org/mapsforge/vtm)
[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](http://www.gnu.org/licenses/lgpl-3.0)

# V™

VTM was developed within the [OpenScienceMap](https://github.com/opensciencemap) project.

**This fork continues VTM development. And provides compatibility with latest [Mapsforge](https://github.com/mapsforge/mapsforge).**

See the **[integration guide](docs/Integration.md)** and [changelog](docs/Changelog.md). And read through [how to contribute](.github/CONTRIBUTING.md) guidelines.

If you have any questions or problems, don't hesitate to ask our public [mailing list](https://groups.google.com/group/mapsforge-dev) for help.

## Features
- Java map library
- OpenGL vector-tile rendering
- Themeable vector layers
- Support for multiple tile sources:
  - OpenScienceMap (.vtm)
  - Mapsforge (.map)
  - Mapbox tiles (.mvt)
  - GeoJSON tiles
  - Raster tiles: any quadtree-scheme tiles as texture
- Backends:
  - Android (optional libGDX)
  - iOS (using libGDX/RoboVM, [instructions](docs/ios.md))
  - Desktop (using libGDX/LWJGL)
  - HTML5/WebGL (using libGDX/GWT, [instructions](docs/web.md))

### Projects
- **vtm** contains the core library
- **vtm-android** Android backend (no libGDX required)
- **vtm-android-example** provides examples using vtm-android
- **vtm-gdx** common libGDX backend
- **vtm-android-gdx** Android backend (with libGDX)
- **vtm-desktop** Desktop backend
- **vtm-ios** iOS backend
- **vtm-web** HTML5/GWT backend
- **vtm-web-app** HTML5/GWT application

## WebGL Demo
[OpenScienceMap](http://opensciencemap.org/s3db/#scale=17,rot=61,tilt=51,lat=53.075,lon=8.807) view of Bremen.
- Hold right mouse button to change view direction.

## Applications
- VTM is used by many [applications](docs/Applications.md).

## Maps
- Mapsforge [map providers](docs/Mapsforge-Maps.md).

## Credits
This library contains code from several projects:
- **Android** (Apache 2.0): some Matrix code, TimSort (http://source.android.com)
- **libGDX** (Apache 2.0): AsyncTask, MathUtils and Interpolation classes (https://github.com/libgdx)
- **mapsforge** (LGPL3): based on 0.2.4 (https://github.com/mapsforge/mapsforge)
- **osmdroid** (Apache 2.0): some overlay classes (https://github.com/osmdroid/osmdroid)
- **tessellate** (SGI Free Software License B 2.0): (https://github.com/cscheid/tessellate)

## Screenshots
![Screenshot Samples App Berlin 1](docs/images/screenshot-berlin-1.png)
