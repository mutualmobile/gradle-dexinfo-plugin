# Android Dexinfo Plugin

This plugin adds a new gradle task to print out the dex method count of your Android projects without having to install
separate tools.


KNOWN ISSUES
------------


## Examples

An example projects can be found in [/examples](examples).

## Usage

Modify your **modules** build.gradle file, not the project's root build.gradle file.

Add the androlate plug buildscript.dependencies section

    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath('com.mutualmobile.gradle.plugins.dexinfo:dexinfo:0.1')
        }
    }

Apply the plugin after the 'com.android.application' or 'com.android.library' plugins

    apply plugin: 'com.android.application'
    apply plugin: 'com.mutualmobile.gradle.plugins.dexinfo'

Optionally add the following section to your build.gradle file to specify options

    dexinfo {
        maxDepth 2
        packageFilter "org.xmlpull"
    }

Where:

    includeClasses (boolean)
        Treat classes as packages and provide per-class method counts. One use-case is for protocol buffers where all
        generated code in a package ends up in a single class

    maxDepth (int)
        Limit how far into package paths (or inner classes, with includeClasses) counts should be reported for.

    packageFilter (String)
        Only consider methods whose fullly qualified name starts with this prefix.

    filter (String: all|defined_only|referenced_only)
        Whether to count all methods (the default), just those defined in the input file, or just those that are
        referenced in it. Note that referenced methods count against the 64K method limit too.


Build your apk

    > ./gradlew -q assembleDebug

Run the dexinfo task

    > ./gradlew -q dexinfoDebug

