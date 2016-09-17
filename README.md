[![Build Status](https://travis-ci.org/mapsforge/vtm.svg?branch=master)](https://travis-ci.org/mapsforge/vtm)
[![GitHub license](https://img.shields.io/badge/license-LGPL3-blue.svg)](COPYING.LESSER)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.mapsforge/vtm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.mapsforge/vtm)

# V™

VTM was developed within the [OpenScienceMap](http://opensciencemap.org) project.

**This fork continues VTM development. And provides compatibility with latest [Mapsforge](https://github.com/mapsforge/mapsforge).**

If you have any questions or problems, don't hesitate to ask our public [mailing list](https://groups.google.com/group/mapsforge-dev) for help.

See the [integration guide](docs/Integration.md) and [changelog](docs/Changelog.md). And read through [how to contribute](.github/CONTRIBUTING.md) guidelines.

## Features
- Java map library
- OpenGL vector-tile rendering
- Themeable vector layers
- Support for multiple tile sources:
  - primary opensciencemap (.vtm)
  - mapsforge files
  - GeoJSON tiles
  - bitmap: any quadtree-scheme tiles as texture
- Backends:
  - Android (optional libGDX)
  - iOS (using libGDX/RoboVM, [instructions](docs/ios.md))
  - Desktop (using libGDX/LWJGL)
  - HTML5/WebGL (using libGDX/GWT)

### Projects
- **vtm** contains the core library
- **vtm-android** Android backend (no libGDX required)
- **vtm-android-example** provides examples using **vtm-android**
- **vtm-gdx** common libGDX backend
- **vtm-android-gdx** Android backend (with libGDX)
- **vtm-desktop** Desktop backend
- **vtm-ios** iOS backend
- **vtm-web** HTML5/GWT backend
- **vtm-web-app** HTML5/GWT application

The libGDX backend for GWT is experimental.

## Applications
- VTM is used by many [applications](docs/Applications.md).

## WebGL Demo
[OpenScienceMap](http://opensciencemap.org/s3db/#scale=17,rot=61,tilt=51,lat=53.075,lon=8.807) view of Bremen.
- Hold right mouse button to change view direction.

## Credits
This library contains code from several projects:
- **Android**: some Matrix code, TimSort (http://source.android.com)
- **libGDX**: AsyncTask, MathUtils and Interpolation classes (https://github.com/libgdx)
- **mapsforge**: based on 0.2.4 (https://github.com/mapsforge/mapsforge)
- **osmdroid**: some overlay classes (https://github.com/osmdroid/osmdroid)
- **tessellate**: (https://github.com/cscheid/tessellate)

## Screenshots
![Screenshot Samples App Bremen 1](docs/images/screenshot-bremen-1.png)
![Screenthot Samples App Bremen 2](docs/images/screenshot-bremen-2.png)
