To compile the native code of VTM for the respective platforms, you need an installed compiler.

The creation of the native code is based on the tool'jnigen' of LibGdx.

An overview how'jnigen' works can be found in the wiki of LibGdx.
https://github.com/libgdx/libgdx/wiki/jnigen

To compile the code can use the Gradle run task.
```groovy
'./gradlew :jni:run' 
```

The platform to compile is described in the section
```groovy
run {
    args '-linux32', '-linux64', '-win64', '-win32'
}
```
on the [build.gradle](../jni/build.gradle) of the ':jni' module.

The following options are possible:
```groovy
usage: Vtm native builder
 -a,--all         compile for all platforms
    --android     compile for Android
    --ios32       compile for iOs
    --linux32     compile for linux 32 bit
    --linux64     compile for linux 64 bit
    --mac32       compile for mac 32 bit
    --mac64       compile for mac 64 bit
    --win32       compile for windows 32 bit
    --win64       compile for windows 64 bit
```

If the -android option was selected, the path to the Android-NDK must also be specified.
```groovy
'-android', '-ndk=/home/default/android-ndk-r17'
```

The resulting native shared libraries are copied to the corresponding modules after successful compilation.

