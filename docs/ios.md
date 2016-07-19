###Implement Exemple:

RoboVm needs the native libs/frameworks for create a build!
Copy this files from vtm-ios-0.6.0-SNAPSHOT-natives.jar into a temp folder!

Create a copy task into your <b>build.gradle</b>

```java
task copyFrameWorks(type: Copy) {
    from(zipTree("./libs/vtm-ios-0.6.0-SNAPSHOT-natives.jar"))
    into("${buildDir}/native")
}


tasks.withType(org.gradle.api.tasks.compile.JavaCompile) {
    compileTask -> compileTask.dependsOn copyFrameWorks
}
```

Now you can configure your robovm.xml to implement the vtm-natives and the SVG-Framework

```
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

Remember, the implementation of a iOS- framework is possible since iOS 8!
So we must set the min iOS-Version at Info.plist.xml!

```
<dict>
    <key>MinimumOSVersion</key>
    <string>8.0</string>
    ...
```