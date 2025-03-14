### Desktop

To run the desktop samples can use the Gradle `run` task.

For `vtm-playground` can change the main class in `build.gradle` and pass args:
```
./gradlew :vtm-playground:run -Pargs=/path/to/theme,/path/to/hgt,/path/to/map1,/path/to/map2
```

To create a standalone executable jar, change the main class in `build gradle` and run:
```
./gradlew :vtm-playground:fatJar
```
The jar file can be found in `build/libs` folder. Depending on the main class, pass args on execution via command line:
```
java -jar vtm-playground-master-SNAPSHOT-jar-with-dependencies.jar /path/to/theme /path/to/hgt /path/to/map1 /path/to/map2
```
The theme file and SRTM hgt folder are optional arguments.

To change the libGDX backend can replace the dependency: `vtm-desktop-lwjgl` or `vtm-desktop-lwjgl3`.
