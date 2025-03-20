# Changelog

## Next version

- Hillshading from HGT digital elevation model data [#1189](https://github.com/mapsforge/vtm/pull/1189)
  - `vtm-hillshading` module
- Online tiles support multiple servers [#1196](https://github.com/mapsforge/vtm/pull/1196)
- Render theme tessellation improvements [#1202](https://github.com/mapsforge/vtm/pull/1202) [#1207](https://github.com/mapsforge/vtm/pull/1207)
- Motorider map theme improvements [#1183](https://github.com/mapsforge/vtm/issues/1183)
- Biker map theme improvements [#1192](https://github.com/mapsforge/vtm/issues/1192)
- Rename `MapDatabase` to `MapFile` [#1184](https://github.com/mapsforge/vtm/pull/1184)
  - Rename `MultiMapDatabase` to `MultiMapFile`
- Update MapBox vector tile dependency [#1199](https://github.com/mapsforge/vtm/pull/1199)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.25.0)

## Version 0.24.0 (2025-03-03)

- Android native libs for 16 KB page size [#1179](https://github.com/mapsforge/vtm/pull/1179)
- Multi-map `MapFileTileSource.setPriority` [#1176](https://github.com/mapsforge/vtm/pull/1176)
- Color filter theme resources [#1175](https://github.com/mapsforge/vtm/pull/1175)
  - `ThemeCallback.getBitmap`
- Rename `MapDatabase.readLabels` to `readNamedItems` [#1181](https://github.com/mapsforge/vtm/pull/1181)
  - Rename `MapDatabase.Selector.LABELS` to `NAMED`
- Update JTS, OkHttp dependencies [#1172](https://github.com/mapsforge/vtm/pull/1172) [#1173](https://github.com/mapsforge/vtm/pull/1173)
- Remove SLF4J dependency [#1167](https://github.com/mapsforge/vtm/pull/1167) [#1170](https://github.com/mapsforge/vtm/pull/1170)
- Libtess2 v1.0.1 [#1178](https://github.com/mapsforge/vtm/pull/1178)
- Disable `vtm-ios` [#1166](https://github.com/mapsforge/vtm/pull/1166)
- Remove `vtm-theme-comparator` [#1169](https://github.com/mapsforge/vtm/pull/1169)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.24.0)

## Version 0.23.0 (2025-02-22)

- Android native libs for 16 KB page size [#1162](https://github.com/mapsforge/vtm/pull/1162)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.23.0)

## Version 0.22.0 (2024-10-10)

- `vtm-themes` change package [#1130](https://github.com/mapsforge/vtm/issues/1130)
- Add native libraries module [#1126](https://github.com/mapsforge/vtm/pull/1126)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.22.0)

## Version 0.21.0 (2024-03-15)

- Drawable style `dropDistance` option [#1085](https://github.com/mapsforge/vtm/pull/1085)
  - Removed `pointReduction` option
- Line style `transparent` option [#1100](https://github.com/mapsforge/vtm/pull/1100)
- Canvas adapter: line scale option [#1075](https://github.com/mapsforge/vtm/pull/1075)
- Disable `vtm-app`
- Disable `vtm-web`, `vtm-web-app` [#1110](https://github.com/mapsforge/vtm/issues/1110)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.21.0)

## Version 0.20.0 (2023-08-15)

- Motorider map theme [#1183](https://github.com/mapsforge/vtm/issues/1183)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.20.0)

## Version 0.19.0 (2023-04-26)

- Render themes: line texture improvements [#1008](https://github.com/mapsforge/vtm/pull/1008)
- Biker map theme [#1192](https://github.com/mapsforge/vtm/issues/1192)
- MVT simplification [#956](https://github.com/mapsforge/vtm/pull/956)
  - `Parameters.SIMPLIFICATION_TOLERANCE`, `Parameters.SIMPLIFICATION_EXCEPTIONS`
- libGDX 1.11.0 [#972](https://github.com/mapsforge/vtm/pull/972) [#977](https://github.com/mapsforge/vtm/pull/977)
- Disable `vtm-jeo` [#997](https://github.com/mapsforge/vtm/pull/997)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.19.0)

## Version 0.18.0 (2022-06-18)

- Mapsforge: deduplicate maps [#903](https://github.com/mapsforge/vtm/pull/903)
- Fix overlapping map regions [#903](https://github.com/mapsforge/vtm/pull/903) [#905](https://github.com/mapsforge/vtm/pull/905)
- Fix `vtm-jni64.dll` [#911](https://github.com/mapsforge/vtm/pull/911)
- Mapsforge: simplification exceptions [#906](https://github.com/mapsforge/vtm/pull/906)
  - `Parameters.SIMPLIFICATION_EXCEPTIONS`
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.18.0)

## Version 0.17.0 (2022-01-03)

- Drawable style point reduction option [#862](https://github.com/mapsforge/vtm/pull/862)
- Drawable style texture repeat option [#862](https://github.com/mapsforge/vtm/pull/862)
- Draggable marker Android sample [#869](https://github.com/mapsforge/vtm/pull/869)
- Move cluster experiment in samples [#858](https://github.com/mapsforge/vtm/pull/858)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.17.0)

## Version 0.16.0 (2021-05-27)

- Android: scoped storage map / theme example [#804](https://github.com/mapsforge/vtm/pull/804)
- Render theme from zip archive [#804](https://github.com/mapsforge/vtm/pull/804)
- Render themes: custom resource providers [#804](https://github.com/mapsforge/vtm/pull/804)
- Nautical unit adapter with feet [#803](https://github.com/mapsforge/vtm/pull/803)
- Distant labels rendering option [#844](https://github.com/mapsforge/vtm/pull/844)
  - `Parameters.DISTANT_LABELS`
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.16.0)

## Version 0.15.0 (2021-01-01)

- Android: scoped storage map example [#785](https://github.com/mapsforge/vtm/pull/785)
- Mapsforge: map stream support [#784](https://github.com/mapsforge/vtm/pull/784)
- Render theme from Android content providers [#783](https://github.com/mapsforge/vtm/pull/783)
- Render theme xml pull parser [#786](https://github.com/mapsforge/vtm/pull/786)
- Symbol scale option [#790](https://github.com/mapsforge/vtm/pull/790)
  - `Parameters.SYMBOL_SCALING`
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.15.0)

## Version 0.14.0 (2020-08-25)

- Render themes: symbol styles [#769](https://github.com/mapsforge/vtm/pull/769)
- More mutable itemized layer [#771](https://github.com/mapsforge/vtm/pull/771)
- Marker renderer sort option
  - `Parameters.MARKER_SORT`
- Update `vtm-jeo` module [#770](https://github.com/mapsforge/vtm/pull/770)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.14.0)

## Version 0.13.0 (2020-01-12)

- Render themes: symbols on lines with billboard / rotation [#743](https://github.com/mapsforge/vtm/pull/743)
- Location texture renderer: rewrite and optimize [#750](https://github.com/mapsforge/vtm/pull/750)
- Fix stroke cap line ending [#758](https://github.com/mapsforge/vtm/pull/758)
- Mapsforge: fix ways precision loss [#752](https://github.com/mapsforge/vtm/pull/752)
- Mapsforge: additional simplification [#757](https://github.com/mapsforge/vtm/pull/757)
  - `Parameters.SIMPLIFICATION_TOLERANCE`
- Android: OpenGL ES 2.0 default for performance / stability [#749](https://github.com/mapsforge/vtm/pull/749)
  - `MapView.OPENGL_VERSION`
- Android: threaded system initialization
  - `Parameters.THREADED_INIT`
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.13.0)

## Version 0.12.0 (2019-09-17)

- MBTiles vector tile source (Android) [#740](https://github.com/mapsforge/vtm/pull/740)
  - `vtm-android-mvt` module
- Render themes: text background color [#737](https://github.com/mapsforge/vtm/pull/737)
- `vtm-desktop-lwjgl` module [#714](https://github.com/mapsforge/vtm/pull/714)
- `vtm-desktop-lwjgl3` module [#717](https://github.com/mapsforge/vtm/pull/717)
- Fix marker touch events [#723](https://github.com/mapsforge/vtm/issues/723)
- Calculation of centroids for all polygons [#734](https://github.com/mapsforge/vtm/pull/734)
  - `Parameters.POLY_CENTROID`
- Disable optimal placement of labels or symbols on polygons
  - `Parameters.POLY_LABEL`
- Android 10 compatibility [#728](https://github.com/mapsforge/vtm/issues/728)
- libGDX 1.9.10 [#731](https://github.com/mapsforge/vtm/issues/731)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.12.0)

## Version 0.11.1 (2019-04-12)

- MBTiles raster tile source (Android) [#708](https://github.com/mapsforge/vtm/pull/708)
- Fix Android 4.2 crash [#713](https://github.com/mapsforge/vtm/issues/713)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.11.1)

## Version 0.11.0 (2019-03-25)

- Render themes: tag transform [#420](https://github.com/mapsforge/vtm/issues/420)
- Render themes: PNG scaling [#595](https://github.com/mapsforge/vtm/issues/595)
- Building shadows [#575](https://github.com/mapsforge/vtm/issues/575)
- Map scaling improvements [#401](https://github.com/mapsforge/vtm/issues/401)
- PathLayer(s) scaled width [#594](https://github.com/mapsforge/vtm/issues/594)
- Mapilion MVT vector tiles & Hillshading [#614](https://github.com/mapsforge/vtm/issues/614)
- Overpass tile source [#663](https://github.com/mapsforge/vtm/issues/663)
- `vtm-gdx-poi3d` module [#600](https://github.com/mapsforge/vtm/pull/600)
- `vtm-models` module [#580](https://github.com/mapsforge/vtm/issues/580)
- `ViewController` refactor [#625](https://github.com/mapsforge/vtm/pull/625)
  - `getMapViewCenter`, `setMapViewCenter` with pivotX, pivotY
- `ThemeCallback.getColor` refactor [#274](https://github.com/mapsforge/vtm/issues/274)
- Enable physical fling and fling on rotation / scale
  - `Parameters.ANIMATOR2`
- Enable optimal placement of labels or symbols on polygons
  - `Parameters.POLY_LABEL`
- Enable placement of symbols on polygons
  - `Parameters.POLY_SYMBOL`
- OpenGL ES 3.0 support [#646](https://github.com/mapsforge/vtm/issues/646)
- OpenGL ES 2.0 complete [#642](https://github.com/mapsforge/vtm/pull/642)
- libGDX 1.9.9 [#635](https://github.com/mapsforge/vtm/issues/635)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.11.0)

## Version 0.10.0 (2018-08-28)

- Map overzoom [#393](https://github.com/mapsforge/vtm/issues/393)
- Buildings overzoom [#503](https://github.com/mapsforge/vtm/issues/503)
- Labels / symbols overzoom [#544](https://github.com/mapsforge/vtm/pull/544)
- S3DB layer [#475](https://github.com/mapsforge/vtm/pull/475)
- `vtm-mvt` module with MVT tile decoder [#481](https://github.com/mapsforge/vtm/pull/481)
- Nextzen MVT / GeoJSON vector tiles [#498](https://github.com/mapsforge/vtm/issues/498)
- OpenMapTiles MVT vector tiles [#482](https://github.com/mapsforge/vtm/issues/482)
- Location texture renderer [#547](https://github.com/mapsforge/vtm/issues/547)
- Render themes: symbols on lines [#495](https://github.com/mapsforge/vtm/issues/495)
- Render themes: styles improvements [#479](https://github.com/mapsforge/vtm/pull/479)
- Internal render themes improvements [#488](https://github.com/mapsforge/vtm/pull/488)
- Map view roll [#474](https://github.com/mapsforge/vtm/pull/474)
- Physical fling and fling on rotation / scale [#497](https://github.com/mapsforge/vtm/pull/497) [#499](https://github.com/mapsforge/vtm/pull/499)
  - `Parameters.ANIMATOR2`
- Scale factor for short vertices calculation [#537](https://github.com/mapsforge/vtm/issues/537)
  - `Parameters.CUSTOM_COORD_SCALE`
- Polygon symbols default disabled [#405](https://github.com/mapsforge/vtm/issues/405)
  - `Parameters.POLY_SYMBOL`
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
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.10.0)

## Version 0.9.2 (2018-01-04)

- Gradle fix transitive dependencies [#433](https://github.com/mapsforge/vtm/issues/433)
- libGDX 1.9.8 [#464](https://github.com/mapsforge/vtm/issues/464)
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.9.2)

## Version 0.9.1 (2017-12-29)

- LwHttp engine fix http headers [#460](https://github.com/mapsforge/vtm/issues/460)
- S3DBLayer renamed to S3DBTileLayer [#452](https://github.com/mapsforge/vtm/issues/452)
- Minor improvements and bug fixes
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
  - `Parameters.POLY_LABEL`
- `vtm-theme-comparator` module [#387](https://github.com/mapsforge/vtm/issues/387)
- Feature parameters [#403](https://github.com/mapsforge/vtm/issues/403)
- `vtm-android-gdx` module enhancements [#435](https://github.com/mapsforge/vtm/issues/435)
- Gradle 4 / Android plugin 3 support [#433](https://github.com/mapsforge/vtm/issues/433)
- libGDX 1.9.7 [#434](https://github.com/mapsforge/vtm/issues/434)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.9.0)

## Version 0.8.0 (2017-07-19)

- Real time (SVG) texture atlas [#63](https://github.com/mapsforge/vtm/issues/63)
  - `Parameters.TEXTURE_ATLAS`
- Marker clustering [#312](https://github.com/mapsforge/vtm/issues/312)
- Osmagray theme [#300](https://github.com/mapsforge/vtm/issues/300)
- Symbol rotation [#294](https://github.com/mapsforge/vtm/issues/294)
- Location renderer improvements [#317](https://github.com/mapsforge/vtm/issues/317)
- POT textures [#334](https://github.com/mapsforge/vtm/issues/334)
  - `Parameters.POT_TEXTURES`
- OkHttp external cache [#135](https://github.com/mapsforge/vtm/issues/135)
- Texture atlas improvements [#301](https://github.com/mapsforge/vtm/pull/301) [#304](https://github.com/mapsforge/vtm/pull/304)
- `vtm-json` module [#367](https://github.com/mapsforge/vtm/issues/367)
- Mapzen GeoJSON vector tiles [#55](https://github.com/mapsforge/vtm/issues/55)
- `vtm-ios-example` module [#326](https://github.com/mapsforge/vtm/issues/326)
- Handle layers enabled state [#342](https://github.com/mapsforge/vtm/issues/342)
- Fix coord scale short overflow [#343](https://github.com/mapsforge/vtm/issues/343)
- Mapsforge map read improvements [#357](https://github.com/mapsforge/vtm/issues/357) [#370](https://github.com/mapsforge/vtm/issues/370)
- Improve canvas DPI setting [#349](https://github.com/mapsforge/vtm/issues/349)
- OSM indoor layer enhancements [#366](https://github.com/mapsforge/vtm/issues/366)
- Gretty plugin at web modules [#338](https://github.com/mapsforge/vtm/issues/338)
- libGDX 1.9.6 [#333](https://github.com/mapsforge/vtm/issues/333)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.8.0)

## Version 0.7.0 (2017-02-26)

- Mapsforge multiple map files [#208](https://github.com/mapsforge/vtm/issues/208)
- New gestures implementation [#253](https://github.com/mapsforge/vtm/issues/253)
  - `Parameters.MAP_EVENT_LAYER2`
- Polygon label position enhancements [#80](https://github.com/mapsforge/vtm/issues/80)
- `vtm-web` modules update [#51](https://github.com/mapsforge/vtm/issues/51)
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
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.7.0)

## Version 0.6.0 (2016-10-28) - VTM revival

- Mapsforge maps **v4** support [#34](https://github.com/mapsforge/vtm/issues/34)
- Render theme SVG resources [#60](https://github.com/mapsforge/vtm/issues/60)
- Render theme styles [#93](https://github.com/mapsforge/vtm/issues/93)
- `vtm-ios` module update [#29](https://github.com/mapsforge/vtm/issues/29)
- Native libraries for all platforms [#14](https://github.com/mapsforge/vtm/issues/14)
- Line stipple and texture rendering [#105](https://github.com/mapsforge/vtm/issues/105)
- Group layer implementation [#99](https://github.com/mapsforge/vtm/issues/99)
- Layer groups implementation [#103](https://github.com/mapsforge/vtm/issues/103)
- Location renderer and layer [#171](https://github.com/mapsforge/vtm/issues/171)
- Map scale bar [#84](https://github.com/mapsforge/vtm/issues/84)
- Tile size based on scale factor [#183](https://github.com/mapsforge/vtm/issues/183)
  - `Parameters.CUSTOM_TILE_SIZE`
- libGDX layer gestures [#151](https://github.com/mapsforge/vtm/issues/151)
- Render theme area tessellation option [#37](https://github.com/mapsforge/vtm/issues/37)
- Render theme resources optional location prefixes [#66](https://github.com/mapsforge/vtm/issues/66)
- Render theme from input stream [#161](https://github.com/mapsforge/vtm/issues/161)
- Render theme from Android assets [#162](https://github.com/mapsforge/vtm/issues/162)
- Graphics API platform enhancements [#92](https://github.com/mapsforge/vtm/issues/92)
- GeoPoint & BoundingBox improvements [#201](https://github.com/mapsforge/vtm/issues/201) [#200](https://github.com/mapsforge/vtm/issues/200)
- `vtm-jts` module [#53](https://github.com/mapsforge/vtm/issues/53)
- `vtm-http` module [#140](https://github.com/mapsforge/vtm/issues/140)
- LWJGL desktop libGDX backend [#129](https://github.com/mapsforge/vtm/issues/129)
- Available on Maven Central
- SNAPSHOT builds publish to Sonatype OSSRH [#165](https://github.com/mapsforge/vtm/issues/165)
- libGDX 1.9.4 [#164](https://github.com/mapsforge/vtm/issues/164)
- Internal render themes various improvements [#41](https://github.com/mapsforge/vtm/issues/41)
- Minor improvements and bug fixes
- [Solved issues](https://github.com/mapsforge/vtm/issues?q=is%3Aclosed+milestone%3A0.6.0)
