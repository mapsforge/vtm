### iOS implementation

RoboVm needs the native libs / frameworks to create a build.

Create a copy task into your iOS-**build.gradle** and add the dependencies.

```groovy


configurations { natives }

    dependencies {
        compile ........
        
        //vtm
        compile "org.mapsforge:vtm-ios:[CURRENT-VERSION]"
        natives "org.mapsforge:vtm-ios:[CURRENT-VERSION]:natives"
    }

// called every time gradle gets executed, takes the native dependencies of
// the natives configuration, and extracts them to the proper build/ folders
// so they get packed with the IPA.
task copyNatives() {
    file("build/native/").mkdirs();
    configurations.natives.files.each { jar ->
        def outputDir = null
        if (jar.name.endsWith("natives.jar")) outputDir = file("build/native/")
        if (outputDir != null) {
            copy {
                from zipTree(jar)
                into outputDir
            }
        }
    }
}


```

Now you can configure your `robovm.xml` to implement the vtm-natives and the SVG-Framework.

```xml
<libs>
    <lib>z</lib>
    <lib>build/native/libvtm-jni.a</lib>  <!--vtm native -->
</libs>
<frameworkPaths>
    <path>build/native</path>  <!--SVGgh framework path -->
</frameworkPaths>
<frameworks>
    <framework>SVGgh</framework> <!--SVGgh framework name -->
    <framework>UIKit</framework>
    <framework>OpenGLES</framework>
    <framework>QuartzCore</framework>
    <framework>CoreGraphics</framework>
    <framework>OpenAL</framework>
    <framework>AudioToolbox</framework>
    <framework>AVFoundation</framework>
</frameworks>
```

Remember the implementation of a iOS framework is possible since iOS 8.
So we must set the min iOS-Version at `Info.plist.xml`.

```xml
<dict>
    <key>MinimumOSVersion</key>
    <string>8.0</string>
    ...
```
