# RenderTheme

This article describes how to use XML-based render-themes to style maps.

## Introduction

A render-theme is an XML file which contains rules and rendering instructions. Such files can be used to customize the visual style of the rendered map. The vtm map library comes with built-in render-themes similar to the [Osmarender](http://wiki.openstreetmap.org/wiki/Osmarender) style. External render-theme files are also supported and can be activated via the `map.setTheme(ThemeLoader.load(File))` method at runtime.

Syntax and semantics of render-theme files are similar but not identical to [Osmarender rules](http://wiki.openstreetmap.org/wiki/Osmarender/Rules). A formal render-theme description exists as an *XML schema document*, it can be found in the [repository](https://github.com/mapsforge/vtm/blob/master/resources/rendertheme.xsd).

It is always recommended to study the [default](https://github.com/mapsforge/vtm/blob/master/vtm-themes/resources/assets/vtm/default.xml) built-in render theme.

## Rules

A rule element `<m/>` (_match_) has several attributes to specify which map elements the rule matches. Non of them is required.

|**Attribute**|**Valid values**|**Description**|**Default**|
|-------------|----------------|---------------|------------|
|e|<ul><li>node</li><li>way</li><li>any</li></ul>|Defines which map element type will be matched.|*any*|
|k|[string](http://www.w3.org/TR/xmlschema-2/#string)|The key of the tile source tag. <ul><li>A vertical bar "`\|`" can be used to specify multiple keys.</li>|any key|
|v|[string](http://www.w3.org/TR/xmlschema-2/#string)|The value of the tile source tag. <ul><li>A vertical bar "`\|`" can be used to specify multiple values.</li><li>A minus sign "`-`" matches when either key is not present or excludes the other values after "`\|`". It never works alone.</li><li>A tilde "`~`" matches when either key is not present or matches if the map element does not have a tag with the specified key.</li>|any value|
|closed|<ul><li>yes</li><li>no</li><li>any</li></ul>|Defines which ways will be matched. A way is considered as closed if its first node and its last node are equal.|*any*|
|select|<ul><li>first</li><li>when-matched</li><li>any</li></ul>|<ul><li>Only add the first matching sub-rule in this rule section (the others are ignored)</li><li>Select all matches of the enclosing rule section</li><li>Select all (whether it was matched or not)</li></ul>|*any*|
|zoom-min|[unsigned byte](http://www.w3.org/TR/xmlschema-2/#unsignedByte)|The minimum zoom level on which the rule will be matched. It cannot be larger than `zoom-appear` in [tag-mapping](https://github.com/mapsforge/mapsforge/blob/master/mapsforge-map-writer/src/main/config/tag-mapping.xml).|*0*|
|zoom-max|[unsigned byte](http://www.w3.org/TR/xmlschema-2/#unsignedByte)|The maximum zoom level on which the rule will be matched.|*127*|

Rules can be nested to any level of depth. This can be used to define rendering instructions which depend on multiple rules. It may also be used to structure complex declarations and to avoid redundancy:

```xml
<m e="way" closed="no">
    <m k="highway" v="motorway">
        <m k="tunnel" v="true|yes">
            …
        </m>
        <m k="tunnel" v="~|no|false">
            …
        </m>
    </m>
</m>
```


Here is an example how to use the `select` attribute. The symbol is shown. The **bold** caption isn't visible cause only the first matched sub-rule is considered. The _italic_ caption is displayed as it was selected by the first sub-rule.

```xml
<m k="railway" v="halt|tram_stop" zoom-min="15" select="first">
    <m v="tram_stop">
        <symbol src="assets:symbols/transport/tram_stop.svg" />
    </m>
    <m v="tram_stop">
        <caption style="bold" dy="20" fill="#af3a3a" k="name" size="12"/>
    </m>
    <m select="when-matched">
        <caption style="italic" dy="-20" fill="#222222" k="name" size="12"/>
    </m>
</m>
```

## Rendering Instructions

A rendering instruction specifies how a map element is drawn. Each rule element might include any number of rendering instructions. Except for labels and symbols, all rendering instructions are drawn on the map in the order of their definition.

At the moment, the following rendering instructions are available:

 - `area`
 - `caption`
 - `circle`
 - `line`
 - `outline`
 - `lineSymbol`
 - `text`
 - `extrusion`
 - `symbol`

## Header Elements
 
The following header elements can be used:
 - `map-background`: a color value to set the color of a blank tile. This should not be used to set the color of the sea or land. Default is `#FFFFFF`.
 - `map-background-outside`: a color value to set the color of the background of a map outside the map area. Effectively everything outside the map area will be overwritten by this color. For transparent layers, the color value will be ignored, but the outside area will be erased to transparent.
 - `base-stroke-width`: set the basic width of strokes. Default is `1`.
 - `base-text-scale`: set the overall text scale. Default is `1`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rendertheme xmlns="http://opensciencemap.org/rendertheme" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://opensciencemap.org/rendertheme https://raw.githubusercontent.com/mapsforge/vtm/master/resources/rendertheme.xsd" version="1" map-background="#FFFCFA" map-background-outside="#DDDDDD">
    …
</rendertheme>
```

## Texts and Captions

The most common attributes for textual styling:

|**Attribute**|**Valid values**|**Description**|**Default**|
|-------------|----------------|---------------|-----------|
|font-family|*default*, *default_bold*, *monospace*, *sans_serif*, *serif*, *thin*, *light*, *medium*, *black*, *condensed*|Set font family|*default*|
|style|*bold*, *bold_italic*, *italic*, *normal*|Set font style|*normal*|
|size|non negative [float](https://www.w3.org/TR/xmlschema-2/#float)|Set font size|*0*|
|fill|[color](https://www.w3.org/TR/css-color-4)|Set fill color|*#000000*|
|stroke|[color](https://www.w3.org/TR/css-color-4)|Set stroke color|*#000000*|
|stroke-width|non negative [float](https://www.w3.org/TR/xmlschema-2/#float)|Set stroke width|*0*|
|dy|[float](https://www.w3.org/TR/xmlschema-2/#float)|Offset in vertical direction|*0*|

## Styles

There are different possibilities to simplify and accelerate map styling or changing styles.

### Style patterns

If you want to use a specific style multiple times you not have to rewrite it for each text, line, area or symbol rule.
If you define a style set an `id` and use it with `use` in your rendering instructions:
 - `style-text`
 - `style-line`
 - `style-area`
 - `style-symbol`
 
This example styles all areas with the_residential_ style, which haven't the `highway` or `building` key.
 
```xml
<style-area fade="11" fill="#e8e7e3" id="residential" />

<m closed="yes" e="way" k="highway|building" v="~">
    <m v="residential|commercial|retail|farmyard">
        <area use="residential" />
    </m>
</m>
```

### Stylemenus

The **stylemenu** allows you to selectively turn rules on and off, in effect defining multiple layers in a map that can be controlled individually. 

To make use of style menus, you will first need to group the elements of your map into categories by attaching a **cat** element to them.

```xml
<m cat="areas" e="way" k="landuse" v="landfill|quarry" zoom-min="14">
```

The same cat tag can be given to as many elements as you like, but each element can only have one cat tag.
However, this does not limit your ability to group elements, as in the style definitions, multiple categories can be combined into one layer. 

You can think about the cat tag as a switch that switches all elements in the rendertheme below it either on or off. If a category is turned off, all elements below it become invisible. If a category is turned on, all elements below become visible (unless they are themselves controlled via a category which is turned off).

Layers are combinations of categories that can be toggled on and off together and other layers (via **overlay**) that can be toggled on and off individually. In general, categories are not visible to a map user, but layers are. To make layers more user friendly, they can be named with the name tag in various languages.

```xml
<layer id="public_transport" visible="true">
    <name lang="de" value="Öffentlicher Verkehr"/>
    <name lang="en" value="Public Transport"/>
    <name lang="es" value="Transporte público"/>
    <name lang="fr" value="Transport public"/>
    <cat id="public_transport"/>
    <cat id="rail"/>
</layer>
```

A set of layer definitions makes up a style:

```xml
<stylemenu id="r4menu" defaultvalue="terrain" defaultlang="en">
    <layer id="shopping" visible="true">
        <name lang="de" value="Shopping"/>
        <name lang="en" value="Shopping"/>
        <name lang="es" value="Tiendas"/>
        <name lang="fr" value="Shopping"/>
        <cat id="shopping"/>
    </layer>

    <layer id="terrain" visible="true">
        <name lang="de" value="Topographischer Hintergrund"/>
        <name lang="en" value="Topographic Colours"/>
        <name lang="es" value="Colores topográficos"/>
        <name lang="fr" value="Couleurs topographiques"/>
        <cat id="topo"/>
    </layer>
</stylemenu>
```

The **visible** attribute is meant to indicate which of the layers are visible in the user interface. Layers where visible is set to false should not be seen by a user, and are thus useful to build base-layers of common categories from which then user-visible layers can be inherited, like this:

```xml
<layer id="base">
    <cat id="roads"/>
    <cat id="waterbodies"/>
    <cat id="landuse"/>
    <cat id="places"/>
    <overlay id="emergency"/>
    <overlay id="food"/>
</layer>

<layer id="simple" parent="base" visible="true">
    <name lang="de" value="Auto"/>
    <name lang="en" value="Driving"/>
    <name lang="es" value="Conducción"/>
    <name lang="fr" value="Conduite"/>
    <cat id="transport"/>
    <cat id="barrier"/>
    <cat id="driving"/>
    <overlay id="parking"/>
    <overlay id="shopping"/>
</layer>

<layer id="standard" parent="base" visible="true">
    <name lang="de" value="Stadt"/>
    <name lang="en" value="City"/>
    <name lang="es" value="City"/>
    <name lang="fr" value="Ville"/>
    <cat id="areas"/>
    <overlay id="tourism"/>
    <overlay id="sports"/>
    <overlay id="amenities"/>
    <overlay id="buildings"/>
    <overlay id="public_transport"/>
    <overlay id="accommodation"/>
    <overlay id="shopping"/>
</layer>
```

To turn layers on by default, add the **enabled=true** attribute. In this case, buildings should be by default visible, while parking related elements not:

```xml
<layer id="parking">
    <name lang="de" value="Parkplätze"/>
    <name lang="en" value="Parking"/>
    <name lang="es" value="Aparcamiento"/>
    <name lang="fr" value="Parking"/>
    <cat id="parking"/>
</layer>

<layer enabled="true" id="buildings">
    <name lang="de" value="Gebäude"/>
    <name lang="en" value="Buildings"/>
    <name lang="es" value="Edificios"/>
    <name lang="fr" value="Bâtiments"/>
    <cat id="buildings"/>
</layer>
```

#### Map Integration

The [Playground](https://github.com/mapsforge/vtm/blob/master/vtm-playground/src/org/oscim/test/MapsforgeStyleTest.java) and [Android](https://github.com/mapsforge/vtm/blob/master/vtm-android-example/src/org/oscim/android/test/MapsforgeStyleActivity.java) Samples app have a completely worked style menu.
When a rendertheme is parsed, the style menu definitons are parsed and a callback is made into the application giving it access to the stylemenu definitions. 

On Android, the stylemenus can thus be converted into standard Android settings, providing a seamless integration of the rendertheme definitions that do not require changes every time the rendertheme changes. There is an example on how to do this in the Samples app of Mapsforge, see the [Settings.java](https://github.com/mapsforge/mapsforge/blob/master/mapsforge-samples-android/src/main/java/org/mapsforge/samples/android/Settings.java) file in Mapsforge repository.

## Priorities

Labels and icons are drawn in order of priority. The default (and highest) priority is 0, so anything with a priority higher than 0 will only be drawn if the space is not yet taken up or has a lower priority value than its colliding elements.

_VTM priorities have the opposite meaning to the Mapsforge render-theme priorities._

```xml
<m e="node" k="place" v="town" zoom-min="8">
    <caption priority="2" k="name" style="bold" size="14" fill="#333380" stroke="#FFFFFF" stroke-width="2.0"/>
</m>
<m e="node" k="place" v="city" zoom-min="6" zoom-max="6">
    <caption priority="0" k="name" style="bold" size="11" fill="#333380" stroke="#FFFFFF" stroke-width="2.0"/>
</m>
```

## Symbols

Symbols can be either defined in the raster PNG format or as vector graphics in SVG format. VTM uses libraries for [Android](https://github.com/BigBadaboom/androidsvg) and [Java](https://github.com/blackears/svgSalamander) that support a large subset of the [Tiny SVG](http://www.w3.org/TR/SVGTiny12/index.html) specification.

### SVG Scaling

SVG resources can be automatically scaled to accommodate different device resolutions.

The options available are:

- If no size is given, a svg is rendered to a 20x20px multiplied by the device scale factor and any user adjustments.
- symbol-percent: the svg is rendered as a percentage size of its default size. This is the best way to make certain svg smaller or bigger than others.
- symbol-width and/or symbol-height: additional parameters give the absolute pixel size for the symbol, again adjusted by the scale factors. If only one dimension is set, the other is calculated from aspect ratio.

### SVG Icon Design 

If you want to know how to design your own icons, you can have a look at Google's [Material Design](https://material.io/design/iconography/system-icons.html) conventions.
To keep SVG sizes down, it is advisable to make use of the reuse facilities within SVG files and to remove any unneccessary comments and metadata.
