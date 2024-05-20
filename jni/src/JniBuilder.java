import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.utils.Architecture;
import com.badlogic.gdx.utils.Os;

public class JniBuilder {
    public static void main(String[] args) throws Exception {
        String[] headers = {".", "libtess2/Include"};
        String[] sources = {
                "gl/utils.c",
                "libtess2/Source/bucketalloc.c",
                "libtess2/Source/dict.c",
                "libtess2/Source/geom.c",
                "libtess2/Source/mesh.c",
                "libtess2/Source/priorityq.c",
                "libtess2/Source/sweep.c",
                "libtess2/Source/tess.c",
        };

        String cflags = " -Wall -O2 -ffast-math";
        cflags += " -DNDEBUG"; /* disable debug in libtess2 */

        //BuildTarget win32home = BuildTarget.newDefaultTarget(Os.Windows, Architecture.Bitness._32);
        //win32home.compilerPrefix = "";
        //win32home.buildFileName = "build-windows32home.xml";
        //win32home.excludeFromMasterBuildFile = true;
        //win32home.headerDirs = headers;
        //win32home.cIncludes = sources;
        //win32home.cFlags += cflags;
        //win32home.cppFlags += cflags;

        /*BuildTarget win32 = BuildTarget.newDefaultTarget(Os.Windows, Architecture.Bitness._32);
        win32.headerDirs = headers;
        win32.cIncludes = sources;
        win32.cFlags += cflags;
        win32.cppFlags += cflags;*/

        /*BuildTarget win64 = BuildTarget.newDefaultTarget(Os.Windows, Architecture.Bitness._64);
        win64.headerDirs = headers;
        win64.cIncludes = sources;
        win64.cFlags += cflags;
        win64.cppFlags += cflags;*/

        /*BuildTarget lin32 = BuildTarget.newDefaultTarget(Os.Linux, Architecture.Bitness._32);
        lin32.headerDirs = headers;
        lin32.cIncludes = sources;
        lin32.cFlags += cflags;
        lin32.cppFlags += cflags;*/

        /*BuildTarget lin64 = BuildTarget.newDefaultTarget(Os.Linux, Architecture.Bitness._64);
        lin64.headerDirs = headers;
        lin64.cIncludes = sources;
        lin64.cFlags += cflags;
        lin64.cppFlags += cflags;*/

        /*BuildTarget mac = BuildTarget.newDefaultTarget(Os.MacOsX, Architecture.Bitness._32);
        mac.headerDirs = headers;
        mac.cIncludes = sources;
        mac.cFlags += cflags;
        mac.cppFlags += cflags;
        mac.linkerFlags += " -framework CoreServices -framework Carbon";*/

        BuildTarget android = BuildTarget.newDefaultTarget(Os.Android, Architecture.Bitness._32);
        android.headerDirs = headers;
        android.cIncludes = sources;
        android.cFlags += cflags;
        android.cppFlags += cflags;
        android.linkerFlags += " -llog";

        /*BuildTarget ios = BuildTarget.newDefaultTarget(Os.IOS, Architecture.Bitness._32);
        ios.headerDirs = headers;
        ios.cIncludes = sources;
        ios.cFlags += cflags;
        ios.cppFlags += cflags;*/

        new NativeCodeGenerator().generate();

        new AntScriptGenerator()
                .generate(new BuildConfig("vtm-jni"),
                        android
                        //lin64,
                        //lin32,
                        //mac,
                        //ios,
                        //win32home,
                        //win32,
                        //win64
                );

        BuildExecutor.executeAnt("jni/build-android32.xml", "compile-natives");
        // BuildExecutor.executeAnt("jni/build-windows32home.xml", "-v clean");
        // BuildExecutor.executeAnt("jni/build-linux64.xml", "-v");
        // BuildExecutor.executeAnt("jni/build.xml", "pack-natives -v");
    }
}
