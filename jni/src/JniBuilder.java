/*
 * Copyright 2018 Longri
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.jnigen.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import org.apache.commons.cli.*;
import org.oscim.utils.NativePacker;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Longri on 18.12.2017.
 */
public class JniBuilder {

    static boolean all;
    static boolean linux32Build;
    static boolean linux64Build;
    static boolean win32Build;
    static boolean win64Build;
    static boolean mac64Build;
    static boolean mac32Build;
    static boolean androidBuild;
    static boolean iOsBuild;
    static boolean linux32BuildOk;
    static boolean linux64BuildOk;
    static boolean win32BuildOk;
    static boolean win64BuildOk;
    static boolean mac64BuildOk;
    static boolean mac32BuildOk;
    static boolean androidBuildOk;
    static boolean iOsBuildOk;

    public static void main(String[] args) throws Exception {
        CommandLine cmd = getCommandLine(args);

        boolean buildSystemIsWin = System.getProperty("os.name").startsWith("Windows");

        final FileDescriptor projectPath = new FileDescriptor("../");
        final FileDescriptor jniDescriptor = new FileDescriptor("jni");
        final FileDescriptor objDescriptor = new FileDescriptor("obj");
        final FileDescriptor buildLibsPath = projectPath.child("libs");
        FileDescriptor buildDescriptor = new FileDescriptor("build/classes/java/main");

        File jniPath = jniDescriptor.file().getAbsoluteFile();
        String jniPathString = jniPath.getAbsolutePath();

        File buildPath = buildDescriptor.file().getAbsoluteFile();
        String buildPathString = buildPath.getAbsolutePath();


        //cleanup before compile
        jniDescriptor.deleteDirectory();
        objDescriptor.deleteDirectory();
        buildLibsPath.deleteDirectory();


        String[] headers = {".", "libtess2/Include"};

        String cFlags = " -Wall -O2 -ffast-math";
        cFlags += " -DNDEBUG"; /* disable debug in libtess2 */

        // generate native code
        new NativeCodeGenerator().generate("src", buildPathString, jniPathString);


        //copy c/c++ src to 'jni' folder
        for (String headerPath : new String[]{"nativeSrc/libtess2/Include", "nativeSrc/libtess2/Source"
                , "nativeSrc/gl", "nativeSrc/rectpack2D"}) {
            FileDescriptor fd = new FileDescriptor(headerPath);
            FileDescriptor[] list = fd.list();
            for (FileDescriptor descriptor : list) {
                descriptor.copyTo(jniDescriptor.child(descriptor.name()));
            }
        }

        //generate build scripts
        all = cmd.hasOption("all");
        linux32Build = cmd.hasOption("linux32");
        linux64Build = cmd.hasOption("linux64");
        win32Build = cmd.hasOption("win32");
        win64Build = cmd.hasOption("win64");
        mac64Build = cmd.hasOption("mac64");
        mac32Build = cmd.hasOption("mac32");
        androidBuild = cmd.hasOption("android");
        iOsBuild = cmd.hasOption("ios32");
        linux32BuildOk = false;
        linux64BuildOk = false;
        win32BuildOk = false;
        win64BuildOk = false;
        mac64BuildOk = false;
        mac32BuildOk = false;
        androidBuildOk = false;
        iOsBuildOk = false;

        Array<BuildTarget> targets = new Array<>();


        if (all || win64Build) {
            BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
            if (buildSystemIsWin) win64.compilerSuffix = ".exe";
            win64.cFlags += cFlags;
            win64.cppFlags += cFlags + " -std=c++11";
            win64.headerDirs = headers;
            targets.add(win64);
        }

        if (all || win32Build) {
            BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
            win32.compilerPrefix = "";
            if (buildSystemIsWin) win32.compilerSuffix = ".exe";
            win32.headerDirs = headers;
            win32.cFlags += cFlags;
            win32.cppFlags += cFlags;
            targets.add(win32);
        }

        if (all || mac64Build) {
            BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
            mac64.compilerPrefix = "";
            mac64.compilerSuffix = "";
            mac64.headerDirs = headers;
            mac64.cFlags += cFlags;
            targets.add(mac64);
        }

        if (all || mac32Build) {
            BuildTarget mac32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, false);
            mac32.compilerPrefix = "";
            mac32.compilerSuffix = "";
            mac32.headerDirs = headers;
            mac32.cFlags += cFlags;
            targets.add(mac32);
        }

        if (all || iOsBuild) {
            BuildTarget ios32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
            ios32.compilerPrefix = "";
            ios32.compilerSuffix = "";
            ios32.headerDirs = headers;
            ios32.cppFlags += " -stdlib=libc++";
            targets.add(ios32);
        }

        if (all || linux32Build) {
            BuildTarget linux32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
            linux32.compilerPrefix = "";
            linux32.compilerSuffix = "";
            linux32.headerDirs = headers;
            linux32.cFlags += cFlags;
            linux32.linkerFlags = "-shared -m32 -z execstack";
            targets.add(linux32);
        }

        if (all || linux64Build) {
            BuildTarget linux64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
            linux64.headerDirs = headers;
            linux64.linkerFlags = "-shared -m64 -z noexecstack";
            linux64.cFlags += cFlags;
            linux64.cExcludes = new String[]{"shell.c"};
            targets.add(linux64);
        }

        if (all || androidBuild) {

            if (!cmd.hasOption("ndk")) {
                throw new RuntimeException("If you wont to compile Android, you must set the Path to NDK");
            }

            BuildTarget android = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Android, false);
            android.headerDirs = headers;
            android.linkerFlags += " -llog";
            android.cFlags += cFlags;
            android.cppFlags += cFlags;
            android.ndkHome = cmd.getOptionValue("ndk");

            if (buildSystemIsWin) {
                android.ndkSuffix = ".cmd";
            }
            targets.add(android);
        }


        BuildConfig config = new BuildConfig("vtm-jni");
        new AntScriptGenerator().generate(config, targets);


        if (all || linux64Build)
            linux32BuildOk = BuildExecutor.executeAnt("build-linux32.xml", "-v", jniPath);
        if (all || linux64Build)
            linux64BuildOk = BuildExecutor.executeAnt("build-linux64.xml", "-v", jniPath);
        if (all || win32Build)
            win32BuildOk = BuildExecutor.executeAnt("build-windows32.xml", "-v", jniPath);
        if (all || win64Build)
            win64BuildOk = BuildExecutor.executeAnt("build-windows64.xml", "-v", jniPath);
        if (all || mac64Build)
            mac64BuildOk = BuildExecutor.executeAnt("build-macosx64.xml", "-v", jniPath);
        if (all || mac32Build)
            mac32BuildOk = BuildExecutor.executeAnt("build-macosx32.xml", "-v", jniPath);
        if (all || iOsBuild)
            iOsBuildOk = BuildExecutor.executeAnt("build-ios32.xml", "-v", jniPath);
        if (all || androidBuild)
            androidBuildOk = BuildExecutor.executeAnt("build-android32.xml", "-v", jniPath);

        copyNewLibs();

        //##############################################
        // Test native VTM
        //##############################################

        final AtomicBoolean WAIT = new AtomicBoolean(true);
        Thread testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runTest();
                //wait for c++ printf
                int cnt=0;
                while (cnt++<100){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                WAIT.set(false);
            }
        });
        testThread.start();

        while (WAIT.get()) {
            //wait for test
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //print result
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");

        System.out.println("#################################################################");
        System.out.println(" ");
        System.out.println("Compilation results:");
        if (androidBuild) {
            if (androidBuildOk) {
                System.out.println("New          compiled : Android");
            } else {
                System.out.println("Failed compilation of : Android");
            }
        }

        if (win32Build) {
            if (win32BuildOk) {
                System.out.println("New          compiled : Windows32");
            } else {
                System.out.println("Failed compilation of : Windows32");
            }
        }

        if (win64Build) {
            if (win64BuildOk) {
                System.out.println("New          compiled : Windows64");
            } else {
                System.out.println("Failed compilation of : Windows64");
            }
        }

        if (linux32Build) {
            if (linux32BuildOk) {
                System.out.println("New          compiled : linux32");
            } else {
                System.out.println("Failed compilation of : linux32");
            }
        }

        if (linux64Build) {
            if (linux64BuildOk) {
                System.out.println("New          compiled : linux64");
            } else {
                System.out.println("Failed compilation of : linux64");
            }
        }


        if (mac32Build) {
            if (mac32BuildOk) {
                System.out.println("New          compiled : mac32");
            } else {
                System.out.println("Failed compilation of : mac32");
            }
        }

        if (mac64Build) {
            if (mac64BuildOk) {
                System.out.println("New          compiled : mac64");
            } else {
                System.out.println("Failed compilation of : mac64");
            }
        }

        if (iOsBuild) {
            if (iOsBuildOk) {
                System.out.println("New          compiled : iOS");
            } else {
                System.out.println("Failed compilation of : iOS");
            }
        }

        //cleanup after compile and copy
        Thread cleanUpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jniDescriptor.deleteDirectory();
                objDescriptor.deleteDirectory();
                buildLibsPath.deleteDirectory();
            }
        });
        cleanUpThread.start();

    }

    private static void copyNewLibs() {
        final FileDescriptor libsPath = new FileDescriptor("../jni/libs/");

        //Android
        if (androidBuildOk) {
            final FileDescriptor androidTargetPath = new FileDescriptor("../vtm-android/natives/");
            androidBuildOk &= copy(libsPath, androidTargetPath.child("armeabi-v7a/lib"), "armeabi-v7a", true);
            androidBuildOk &= copy(libsPath, androidTargetPath.child("arm64-v8a/lib"), "arm64-v8a", true);
            androidBuildOk &= copy(libsPath, androidTargetPath.child("x86/lib"), "x86", true);
            androidBuildOk &= copy(libsPath, androidTargetPath.child("x86_64/lib"), "x86_64", true);
        }

        //Desktop
        final FileDescriptor desktopTargetPath = new FileDescriptor("../vtm-desktop/natives/");
        if (linux32BuildOk)
            linux32BuildOk &= copy(libsPath, desktopTargetPath.child("linux"), "linux32", false);
        if (linux64BuildOk)
            linux64BuildOk &= copy(libsPath, desktopTargetPath.child("linux"), "linux64", false);
        if (mac32BuildOk)
            mac32BuildOk &= copy(libsPath, desktopTargetPath.child("osx"), "macosx32", false);
        if (mac64BuildOk)
            mac64BuildOk &= copy(libsPath, desktopTargetPath.child("osx"), "macosx64", false);
        if (win64BuildOk)
            win64BuildOk &= copy(libsPath, desktopTargetPath.child("windows"), "windows64", false);
        if (win32BuildOk)
            win32BuildOk &= copy(libsPath, desktopTargetPath.child("windows"), "windows", false);

        //iOS
        final FileDescriptor iosTargetPath = new FileDescriptor("../vtm-ios/natives/");
        if (iOsBuildOk)
            iOsBuildOk &= copy(libsPath, iosTargetPath, "ios32", false);

        libsPath.deleteDirectory();

    }

    private static boolean copy(final FileDescriptor libsPath, final FileDescriptor precompiledLibsPath, String folder, boolean withFolder) {
        final FileDescriptor lib = libsPath.child(folder);
        if (folderExistAndNotEmpty(lib)) {
            if (withFolder) {
                lib.copyTo(precompiledLibsPath);
            } else {
                //copy only the files from folder
                final FileDescriptor[] list = lib.list();
                for (final FileDescriptor file : list) {
                    file.copyTo(precompiledLibsPath);
                }
            }
            return true;
        }
        return false;
    }

    private static boolean folderExistAndNotEmpty(final FileDescriptor folder) {
        if (!folder.exists() || !folder.isDirectory()) return false;
        return (folder.list().length > 0);
    }

    private static void runTest() {

        System.out.println("#################################################################");
        System.out.println("#########  Run Native Test  #####################################");
        System.out.println("#################################################################");

        int randomTestRecCount = 20;
        int maxTexSize = 600;


        //delete alt test folder
        FileHandle clear = new FileHandle("test");
        clear.deleteDirectory();
        loadLibrary();
        short[] valueArray = new short[randomTestRecCount * 7];


        // fill random test array
        int index = 0;
        for (int i = 0; i < randomTestRecCount; i++) {
            valueArray[index + 0] = (short) i; // index
            valueArray[index + 1] = 0; // x
            valueArray[index + 2] = 0; // y
            valueArray[index + 3] = (short) MathUtils.random(100, 300);// width
            valueArray[index + 4] = (short) MathUtils.random(100, 300); // height
            valueArray[index + 5] = 0;// flipped
            valueArray[index + 6] = 0;
            index += 7;
        }

        int[] result = NativePacker.packNative(valueArray, valueArray.length / 7,
                maxTexSize, false, false);



        int idx = 1;
        for (int i = 0; i < result[0]; i++) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Page[").append(i).append("] w=").append(result[idx++]).append(" h=").append(result[idx++]);

            System.out.println(stringBuilder.toString());
            index = 0;
            for (int j = 0; j < randomTestRecCount; j++) {
                if (valueArray[index + 6] == i) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("    ");
                    sb.append("rec index=").append(valueArray[index + 0]);
                    sb.append(" x=").append(valueArray[index + 1]);
                    sb.append(" y=").append(valueArray[index + 2]);
                    sb.append(" width=").append(valueArray[index + 3]);
                    sb.append(" height=").append(valueArray[index + 4]);
                    sb.append(" flipped=").append((valueArray[index + 5] > 0));
                    System.out.println(sb.toString());
                }
                index += 7;
            }
        }

    }

    private static String arrayToString(Object[] items) {

        if (items == null) return "NULL";

        if (items.length == 0) return "[]";
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < items.length; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    private static CommandLine getCommandLine(String[] args) {
        Options options = new Options();


        Option all = new Option("a", "all", false, "compile for all platforms");
        all.setRequired(false);
        options.addOption(all);

        Option mac64 = new Option(null, "mac64", false, "compile for mac 64 bit");
        mac64.setRequired(false);
        options.addOption(mac64);

        Option mac32 = new Option(null, "mac32", false, "compile for mac 32 bit");
        mac32.setRequired(false);
        options.addOption(mac32);

        Option linux32 = new Option(null, "linux32", false, "compile for linux 32 bit");
        linux32.setRequired(false);
        options.addOption(linux32);

        Option linux64 = new Option(null, "linux64", false, "compile for linux 64 bit");
        linux64.setRequired(false);
        options.addOption(linux64);

        Option win32 = new Option(null, "win32", false, "compile for windows 32 bit");
        win32.setRequired(false);
        options.addOption(win32);

        Option win64 = new Option(null, "win64", false, "compile for windows 64 bit");
        win64.setRequired(false);
        options.addOption(win64);

        Option ios32 = new Option(null, "ios32", false, "compile for iOs");
        ios32.setRequired(false);
        options.addOption(ios32);

        Option android = new Option(null, "android", false, "compile for Android");
        android.setRequired(false);
        options.addOption(android);

        Option androidNdk = new Option(null, "ndk", true, "path to Android NDK");
        androidNdk.setRequired(false);
        options.addOption(androidNdk);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Vtm native builder", options);

            System.exit(1);
            return null;
        }
        return cmd;
    }

    private static void loadLibrary() {
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        boolean isLinux = System.getProperty("os.name").contains("Linux");
        boolean isMac = System.getProperty("os.name").contains("Mac");
        boolean is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");

        String sharedLibName = "vtm-jni";

        if (isWindows) {
            if (!is64Bit)
                System.load(new File("../vtm-desktop/natives/windows/" + sharedLibName + ".dll").getAbsolutePath());
            else
                System.load(new File("../vtm-desktop/natives/windows/" + sharedLibName + "64.dll").getAbsolutePath());
        }
        if (isLinux) {
            if (!is64Bit) {
                System.load(new File("../vtm-desktop/natives/linux/lib" + sharedLibName + ".so").getAbsolutePath());
            } else {
                System.load(new File("../vtm-desktop/natives/linux/lib" + sharedLibName + "64.so").getAbsolutePath());
            }
        }
        if (isMac) {
            if (!is64Bit)
                System.load(new File("../vtm-desktop/natives/osx/lib" + sharedLibName + ".dylib").getAbsolutePath());
            else
                System.load(new File("../vtm-desktop/natives/osx/lib" + sharedLibName + "64.dylib").getAbsolutePath());
        }
    }

}
