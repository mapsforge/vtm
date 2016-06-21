import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;

public class JniBuilder {
	public static void main(String[] args) throws Exception {
		String[] headers = { ".", "libtess2/Include" };
		String[] sources = {
		        "gl/utils.c",
		        "libtess2/Source/bucketalloc.c",
		        "libtess2/Source/dict.c",
		        "libtess2/Source/geom.c",
		        "libtess2/Source/mesh.c",
		        "libtess2/Source/priorityq.c",
		        "libtess2/Source/sweep.c",
		        "libtess2/Source/tess.c",
				"libtess2/Source/org.oscim.utils.TessJNI.cpp"
		};

		String cflags = " -Wall -std=c99 -O2 -ffast-math";
		cflags += " -DNDEBUG"; /* disable debug in libtess2 */

		//BuildTarget win32home = BuildTarget
		//    .newDefaultTarget(TargetOs.Windows, false);
		//win32home.compilerPrefix = "";
		//win32home.buildFileName = "build-windows32home.xml";
		//win32home.excludeFromMasterBuildFile = true;
		//win32home.headerDirs = headers;
		//win32home.cIncludes = sources;
		//win32home.cFlags += cflags;
		//win32home.cppFlags += cflags;

		BuildTarget win32 = BuildTarget
		    .newDefaultTarget(TargetOs.Windows, false);
		win32.headerDirs = headers;
		win32.cIncludes = sources;
		win32.cFlags += cflags;
		win32.cppFlags += cflags;

		BuildTarget win64 = BuildTarget
		    .newDefaultTarget(TargetOs.Windows, true);
		win64.headerDirs = headers;
		win64.cIncludes = sources;
		win64.cFlags += cflags;
		win64.cppFlags += cflags;

		BuildTarget lin32 = BuildTarget
		    .newDefaultTarget(TargetOs.Linux, false);
		lin32.headerDirs = headers;
		lin32.cIncludes = sources;
		lin32.cFlags += cflags;
		lin32.cppFlags += cflags;

		BuildTarget lin64 = BuildTarget
		    .newDefaultTarget(TargetOs.Linux, true);
		lin64.headerDirs = headers;
		lin64.cIncludes = sources;
		lin64.cFlags += cflags;
		lin64.cppFlags += cflags;

		BuildTarget mac = BuildTarget
		    .newDefaultTarget(TargetOs.MacOsX, false);
		mac.headerDirs = headers;
		mac.cIncludes = sources;
		mac.cFlags += cflags;
		mac.cppFlags += cflags;
		mac.linkerFlags += " -framework CoreServices -framework Carbon";

		BuildTarget android = BuildTarget
		    .newDefaultTarget(TargetOs.Android, false);
		android.headerDirs = headers;
		android.cIncludes = sources;
		android.cFlags += cflags;
		android.cppFlags += cflags;
		android.linkerFlags += " -llog";

		BuildTarget ios = BuildTarget.newDefaultTarget(TargetOs.IOS, false);
		ios.headerDirs = headers;
		ios.cIncludes = sources;
		ios.cFlags += cflags;
		ios.cppFlags += cflags;

	//	new NativeCodeGenerator().generate();

		new AntScriptGenerator()
		    .generate(new BuildConfig("vtm-jni"),
//		              android,
//		              lin64,
//		              lin32,
//		              mac,
		              ios,
		              //win32home, 
//		              win32,
		              win64
		    );

		// BuildExecutor.executeAnt("jni/build-windows32home.xml", "-v clean");
		// BuildExecutor.executeAnt("jni/build-linux64.xml", "-v");
		// BuildExecutor.executeAnt("jni/build.xml", "pack-natives -v");
	}
}
