### Desktop

To run the desktop samples can use the Gradle `run` task.

e.g. for `vtm-playground` can change the [mainClassName](../vtm-playground/build.gradle) to run each sample and pass args:
```groovy
./gradlew :vtm-playground:run -Pargs=/path/to/map
```

To create a standalone executable jar, add [`fatjar` task](../vtm-theme-comparator/build.gradle#L35) to your [build gradle](../vtm-playground/build.gradle) and adapt the main class, then run:
```groovy
./gradlew :vtm-playground:fatjar
```
The jar file can be found in `build/libs` folder. Depending on the main class pass args on execution via command line: 
```
java -jar vtm-playground-master-SNAPSHOT-jar-with-dependencies.jar /path/to/map
```
