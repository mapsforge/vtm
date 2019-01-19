### Web implementation

`./gradlew :vtm-web-app:farmRun` will run standalone web server at port 8080.

Then go to [http://localhost:8080/vtm-web-app](http://localhost:8080/vtm-web-app) in the web browser to see the map.

Hold right mouse button to change view direction.


#### Debugging GWT app

Using GWT SuperDevMode is the recommended way for development. Debugging in IDE itself is not possible.

For an introduction see [GWT](http://www.gwtproject.org/articles/superdevmode.html) and [libGDX](http://www.badlogicgames.com/wordpress/?p=3073) documentations.

- Serve the website as usual with `./gradlew :vtm-web-app:farmRun` command.

- The codeserver must be executed on another shell.
```
export _JAVA_OPTIONS="-Xmx1024m"
./gradlew :vtm-web-app:gwtSuperDev
```

- Open the link of codeserver: `The code server is ready at http://127.0.0.1:xxxx/`

- Drag the two bookmarklets to your browser's bookmark bar.

- Visit your [web page](http://localhost:8080/vtm-web-app) and click **Dev Mode On** to start development mode.

- Press **F12** to open the developer tools of your browser. Now you can debug your code under **Sources**.