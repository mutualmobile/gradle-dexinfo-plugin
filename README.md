# Android dexinfo plugin 

[ ![Download](https://api.bintray.com/packages/mutualmobile/Android/gradle-dexinfo-plugin/images/download.svg) ](https://bintray.com/mutualmobile/Android/gradle-dexinfo-plugin/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android%20dexinfo%20plugin-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/2148)

This Gradle plugin adds a new task to print out the dex method count of your Android projects without having to install separate tools.

Based on [https://github.com/mihaip/dex-method-counts](https://github.com/mihaip/dex-method-counts)

## Usage

Modify your build.gradle file to add a dependency to the plugin.

    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.mutualmobile.gradle.plugins:dexinfo:0.1.2'
        }
    }

Apply the plugin after the 'com.android.application' or 'com.android.library' plugins

    apply plugin: 'com.android.application'
    apply plugin: 'com.mutualmobile.gradle.plugins.dexinfo'

Build your apk

    > ./gradlew -q assembleDebug

Run the dexinfo task

    > ./gradlew -q dexinfoDebug

## Sample output

    <root>: 182 
    butterknife: 182 
      ButterKnife: 48 
         Action: 1 
         Finder: 22 
            1: 3 
            2: 3 
            3: 3 
         Finder[]: 1 
         Injector: 2 
      ImmutableList: 4 
      InjectView: 1 
      InjectViews: 1 
      OnCheckedChanged: 1 
      OnClick: 1 
      internal: 99 
         Binding: 1 
         ButterKnifeProcessor: 22 
         CollectionBinding: 12 
            Kind: 5 
            Kind[]: 1 
         DebouncingOnClickListener: 7 
            1: 2 
      ...
    
    Overall method count: 182 
      
    BUILD SUCCESSFUL

## Additional options

Add the following section to your build.gradle file to specify options

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
