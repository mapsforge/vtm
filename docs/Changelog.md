# Changelog

## Version 0.10.0 (2018-08-28)

- Map overzoom [#393](https://github.com/mapsforge/vtm/issues/393)
- Buildings overzoom [#503](https://github.com/mapsforge/vtm/issues/503)
- Labels / symbols overzoom [#544](https://github.com/mapsforge/vtm/pull/544)
- S3DB layer [#475](https://github.com/mapsforge/vtm/pull/475)
- vtm-mvt module with MVT tile decoder [#481](https://github.com/mapsforge/vtm/pull/481)
- Nextzen MVT / GeoJSON vector tiles [#498](https://github.com/mapsforge/vtm/issues/498)
- OpenMapTiles MVT vector tiles [#482](https://github.com/mapsforge/vtm/issues/482)
- Location texture renderer [#547](https://github.com/mapsforge/vtm/issues/547)
- Render themes: symbols on lines [#495](https://github.com/mapsforge/vtm/issues/495)
- Render themes: styles improvements [#479](https://github.com/mapsforge/vtm/pull/479)
- Internal render themes improvements [#488](https://github.com/mapsforge/vtm/pull/488)
- Map view roll [#474](https://github.com/mapsforge/vtm/pull/474)
- Physical fling and fling on rotation / scale [#497](https://github.com/mapsforge/vtm/pull/497) [#499](https://github.com/mapsforge/vtm/pull/499)
  - `Parameters.ANIMATOR2 = true;`
- Scale factor for short vertices calculation [#537](https://github.com/mapsforge/vtm/issues/537)
  - `Parameters.CUSTOM_COORD_SCALE = false;`
- Polygon symbols default disabled [#405](https://github.com/mapsforge/vtm/issues/405)
  - `Parameters.POLY_SYMBOL = true;`
- Map fractional zoom [#487](https://github.com/mapsforge/vtm/issues/487)
- Render theme fallback internal resources [#477](https://github.com/mapsforge/vtm/issues/477)
- Fix layers synchronization [#507](https://github.com/mapsforge/vtm/issues/507)
- Fix Mapsforge clipping on small zooms [#264](https://github.com/mapsforge/vtm/issues/264)
- Fix PathLayer (vtm) reverse segments [#220](https://github.com/mapsforge/vtm/issues/220)
- Fix FadeStep alpha interpolation [#486](https://github.com/mapsforge/vtm/issues/486)
- Fix libGDX flickering [#148](https://github.com/mapsforge/vtm/issues/148) [#149](https://github.com/mapsforge/vtm/issues/149)
- ViewController refactor [#523](https://github.com/mapsforge/vtm/pull/523)
  - `ViewController.setScreenSize` renamed to `setViewSize`
  - `ViewController.setMapScreenCenter` renamed to `setMapViewCenter`
- Android 9 compatibility [#550](https://github.com/mapsforge/vtm/issues/550)
- JTS (LocationTech) [#484](https://github.com/mapsforge/vtm/issues/484)
- SVG Salamander (JitPack) [#560](https://github.com/mapsforge/vtm/issues/560)
- Many other minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.10.0)

## Version 0.9.2 (2018-01-04)

- Gradle fix transitive dependencies [#433](https://github.com/mapsforge/vtm/issues/433)
- libGDX 1.9.8 [#464](https://github.com/mapsforge/vtm/issues/464)
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.9.2)

## Version 0.9.1 (2017-12-29)

- LwHttp engine fix http headers [#460](https://github.com/mapsforge/vtm/issues/460)
- S3DBLayer renamed to S3DBTileLayer [#452](https://github.com/mapsforge/vtm/issues/452)
- Many other minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.9.1)

## Version 0.9.0 (2017-12-03)

- Mapsforge maps **v5** support [#429](https://github.com/mapsforge/vtm/issues/429)
- Mapsforge themes compatibility [#100](https://github.com/mapsforge/vtm/issues/100)
- Render themes: line symbol [#124](https://github.com/mapsforge/vtm/issues/124)
- Render themes: stroke dash array [#131](https://github.com/mapsforge/vtm/issues/131)
- PathLayer overlay touch events [#316](https://github.com/mapsforge/vtm/issues/316)
- VectorLayer (polygon) overlay touch events [#424](https://github.com/mapsforge/vtm/issues/424)
- Two finger tap zoom out gesture [#423](https://github.com/mapsforge/vtm/issues/423)
- POI Search example [#394](https://github.com/mapsforge/vtm/issues/394)
- Mapsforge Reverse Geocoding [#383](https://github.com/mapsforge/vtm/issues/383)
- Core utilities [#396](https://github.com/mapsforge/vtm/issues/396)
  - `MercatorProjection.groundResolution` renamed to `groundResolutionWithScale`
- Map scaling improvements [#401](https://github.com/mapsforge/vtm/issues/401)
- Mapsforge fix artifacts zoom >17 [#231](https://github.com/mapsforge/vtm/issues/231)
- Mapzen building extrusions [#419](https://github.com/mapsforge/vtm/issues/419)
  - BuildingLayer expects height tags in meters
- Polygon symbol positioning [#405](https://github.com/mapsforge/vtm/issues/405)
- PolyLabel default disabled [#402](https://github.com/mapsforge/vtm/issues/402)
  - `Parameters.POLY_LABEL = true;`
- vtm-theme-comparator module [#387](https://github.com/mapsforge/vtm/issues/387)
- Feature parameters [#403](https://github.com/mapsforge/vtm/issues/403)
- vtm-android-gdx module enhancements [#435](https://github.com/mapsforge/vtm/issues/435)
- Gradle 4 / Android plugin 3 support [#433](https://github.com/mapsforge/vtm/issues/433)
- libGDX 1.9.7 [#434](https://github.com/mapsforge/vtm/issues/434)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Many other minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.9.0)

## Version 0.8.0 (2017-07-19)

- Real time (SVG) texture atlas [#63](https://github.com/mapsforge/vtm/issues/63)
  - `Parameters.TEXTURE_ATLAS = true;`
- Marker clustering [#312](https://github.com/mapsforge/vtm/issues/312)
- Osmagray theme [#300](https://github.com/mapsforge/vtm/issues/300)
- Symbol rotation [#294](https://github.com/mapsforge/vtm/issues/294)
- Location renderer improvements [#317](https://github.com/mapsforge/vtm/issues/317)
- POT textures [#334](https://github.com/mapsforge/vtm/issues/334)
  - `Parameters.POT_TEXTURES = true;`
- OkHttp external cache [#135](https://github.com/mapsforge/vtm/issues/135)
- Texture atlas improvements [#301](https://github.com/mapsforge/vtm/pull/301) [#304](https://github.com/mapsforge/vtm/pull/304)
- vtm-json module [#367](https://github.com/mapsforge/vtm/issues/367)
- Mapzen GeoJSON vector tiles [#55](https://github.com/mapsforge/vtm/issues/55)
- vtm-ios-example module [#326](https://github.com/mapsforge/vtm/issues/326)
- Handle layers enabled state [#342](https://github.com/mapsforge/vtm/issues/342)
- Fix coord scale short overflow [#343](https://github.com/mapsforge/vtm/issues/343)
- Mapsforge map read improvements [#357](https://github.com/mapsforge/vtm/issues/357) [#370](https://github.com/mapsforge/vtm/issues/370)
- Improve canvas DPI setting [#349](https://github.com/mapsforge/vtm/issues/349)
- OSM indoor layer enhancements [#366](https://github.com/mapsforge/vtm/issues/366)
- Gretty plugin at web modules [#338](https://github.com/mapsforge/vtm/issues/338)
- libGDX 1.9.6 [#333](https://github.com/mapsforge/vtm/issues/333)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Many other minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.8.0)

## Version 0.7.0 (2017-02-26)

- Mapsforge multiple map files [#208](https://github.com/mapsforge/vtm/issues/208)
- New gestures implementation [#253](https://github.com/mapsforge/vtm/issues/253)
  - `Parameters.MAP_EVENT_LAYER2 = true;`
- Polygon label position enhancements [#80](https://github.com/mapsforge/vtm/issues/80)
- vtm-web modules update [#51](https://github.com/mapsforge/vtm/issues/51)
- Mapzen MVT vector tiles [#57](https://github.com/mapsforge/vtm/issues/57)
- SVG resources scaling in themes [#214](https://github.com/mapsforge/vtm/issues/214)
- Circle map style [#122](https://github.com/mapsforge/vtm/issues/122)
- Oneway arrows in themes [#275](https://github.com/mapsforge/vtm/issues/275)
- Texture atlas from bitmaps [#283](https://github.com/mapsforge/vtm/pull/283)
- PathLayer (vtm) fix disappearing segments [#108](https://github.com/mapsforge/vtm/issues/108)
- House numbers (nodes) fix visibility [#168](https://github.com/mapsforge/vtm/issues/168)
- Android fix quick scale vs long press [#250](https://github.com/mapsforge/vtm/issues/250)
- Use baseline 160dpi in scaling [#236](https://github.com/mapsforge/vtm/issues/236)
- OkHttp3 update [#138](https://github.com/mapsforge/vtm/issues/138)
- libGDX double tap zoom [#263](https://github.com/mapsforge/vtm/issues/263)
- MapFileTileSource zoom level API enhancements [#219](https://github.com/mapsforge/vtm/issues/219)
- Animator enhancements with easing functions [#246](https://github.com/mapsforge/vtm/issues/246)
- Tile grid layer scaling [#238](https://github.com/mapsforge/vtm/issues/238)
- Internal render themes new SVG resources [#251](https://github.com/mapsforge/vtm/issues/251)
- Fix theme parsing in non-Latin locales [#297](https://github.com/mapsforge/vtm/issues/297)
- libGDX 1.9.5 [#270](https://github.com/mapsforge/vtm/issues/270)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Many other minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.7.0)

## Version 0.6.0 (2016-10-28) - VTM revive

- Mapsforge maps **v4** support [#34](https://github.com/mapsforge/vtm/issues/34)
- Render theme SVG resources [#60](https://github.com/mapsforge/vtm/issues/60)
- Render theme styles [#93](https://github.com/mapsforge/vtm/issues/93)
- vtm-ios module update [#29](https://github.com/mapsforge/vtm/issues/29)
- Native libraries for all platforms [#14](https://github.com/mapsforge/vtm/issues/14)
- Line stipple and texture rendering [#105](https://github.com/mapsforge/vtm/issues/105)
- Layer groups [#99](https://github.com/mapsforge/vtm/issues/99) [#103](https://github.com/mapsforge/vtm/issues/103)
- Location renderer and layer [#171](https://github.com/mapsforge/vtm/issues/171)
- Map scale bar [#84](https://github.com/mapsforge/vtm/issues/84)
- Tile size based on scale factor [#183](https://github.com/mapsforge/vtm/issues/183)
  - `Parameters.CUSTOM_TILE_SIZE = false;`
- libGDX layer gestures [#151](https://github.com/mapsforge/vtm/issues/151)
- Render theme area tessellation option [#37](https://github.com/mapsforge/vtm/issues/37)
- Render theme resources optional location prefixes [#66](https://github.com/mapsforge/vtm/issues/66)
- Graphics API platform enhancements [#92](https://github.com/mapsforge/vtm/issues/92)
- GeoPoint & BoundingBox improvements [#201](https://github.com/mapsforge/vtm/issues/201) [#200](https://github.com/mapsforge/vtm/issues/200)
- vtm-jts module [#53](https://github.com/mapsforge/vtm/issues/53)
- vtm-http module [#140](https://github.com/mapsforge/vtm/issues/140)
- LWJGL desktop libGDX backend [#129](https://github.com/mapsforge/vtm/issues/129)
- Available on [Maven Central](https://search.maven.org/search?q=g:org.mapsforge)
- SNAPSHOT builds publish to Sonatype OSSRH [#165](https://github.com/mapsforge/vtm/issues/165)
- libGDX 1.9.4 [#164](https://github.com/mapsforge/vtm/issues/164)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Many other minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.6.0)
