package com.mutualmobile.gradle.plugins.dexinfo

import com.android.dexdeps.DexData
import com.android.dexdeps.DexDataException
import info.persistent.dex.DexMethodCounts
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile

class DexinfoTask extends DefaultTask {

    /** Global instance of Dexinfo Extension */
    //DexinfoPluginExtension dexinfo

    def variant

    DexinfoPluginExtension dexinfo

    DexinfoTask() {
        super()
        this.description = 'Display information about the generated DEX file'
        this.dexinfo = project.dexinfo
    }


    @TaskAction
    void printDexInfo() {
        boolean first = true;
        String mOutputFormat = "brief" // "xml"
        boolean mJustClasses = false

        DexMethodCounts.Filter filter = DexMethodCounts.Filter.valueOf(this.dexinfo.filter)

        variant.outputs.each { output ->
            String fileName = output.outputFile.path;
            println("Processing " + fileName)

            DexMethodCounts.overallCount = 0;
            try {
                List<RandomAccessFile> dexFiles = openInputFiles(fileName);
                DexMethodCounts.Node packageTree = new DexMethodCounts.Node();
                for (RandomAccessFile dexFile : dexFiles) {
                    DexData dexData = new DexData(dexFile);
                    dexData.load();

                    DexMethodCounts.generate(
                            packageTree, dexData, dexinfo.includeClasses, dexinfo.packageFilter, dexinfo.maxDepth, filter);

/*
                    if (first) {
                        first = false;
                        Output.generateFirstHeader(fileName, mOutputFormat);
                    } else {
                        Output.generateHeader(fileName, mOutputFormat);
                    }

                    Output.generate(dexData, mOutputFormat, mJustClasses);
                    Output.generateFooter(mOutputFormat);
*/
                    dexFile.close();
                }

                packageTree.output("");


            } catch (IOException ioe) {
                throw new GradleScriptException(dde);
            } catch (DexDataException dde) {
                /* a message was already reported, just bail quietly */
                throw new GradleScriptException(dde);
            }

            println("")
            if (DexMethodCounts.overallCount >= (1024 * 64)) {
                println("*" * 80);
                println("Overall method count: " + DexMethodCounts.overallCount);
                println("*" * 80);
            } else {
                println("Overall method count: " + DexMethodCounts.overallCount);
            }

        }
    }

    /**
     * Opens an input file, which could be a .dex or a .jar/.apk with a
     * classes.dex inside.  If the latter, we extract the contents to a
     * temporary file.
     */
    List<RandomAccessFile> openInputFiles(String fileName) throws IOException {
        List<RandomAccessFile> dexFiles = new ArrayList<RandomAccessFile>();

        openInputFileAsZip(fileName, dexFiles);
        if (dexFiles.size() == 0) {
            File inputFile = new File(fileName);
            RandomAccessFile dexFile = new RandomAccessFile(inputFile, "r");
            dexFiles.add(dexFile);
        }

        return dexFiles;
    }

    /**
     * Tries to open an input file as a Zip archive (jar/apk) with a
     * "classes.dex" inside.
     */
    void openInputFileAsZip(String fileName, List<RandomAccessFile> dexFiles) throws IOException {
        ZipFile zipFile;

        // Try it as a zip file.
        try {
            zipFile = new ZipFile(fileName);
        } catch (FileNotFoundException fnfe) {
            // not found, no point in retrying as non-zip.
            System.err.println("Unable to open '" + fileName + "': " +
                    fnfe.getMessage());
            throw fnfe;
        } catch (ZipException ze) {
            // not a zip
            return;
        }

        // Open and add all files matching "classes.*\.dex" in the zip file.
        for (ZipEntry entry : Collections.list(zipFile.entries())) {
            if (entry.getName().matches("classes.*\\.dex")) {
                dexFiles.add(openDexFile(zipFile, entry));
            }
        }

        zipFile.close();
    }

    RandomAccessFile openDexFile(ZipFile zipFile, ZipEntry entry) throws IOException {
        // We know it's a zip; see if there's anything useful inside.  A
        // failure here results in some type of IOException (of which
        // ZipException is a subclass).
        InputStream zis = zipFile.getInputStream(entry);

        // Create a temp file to hold the DEX data, open it, and delete it
        // to ensure it doesn't hang around if we fail.
        File tempFile = File.createTempFile("dexdeps", ".dex");
        RandomAccessFile dexFile = new RandomAccessFile(tempFile, "rw");
        tempFile.delete();

        // Copy all data from input stream to output file.
        byte[] copyBuf = new byte[32768];
        int actual;

        while (true) {
            actual = zis.read(copyBuf);
            if (actual == -1)
                break;

            dexFile.write(copyBuf, 0, actual);
        }

        dexFile.seek(0);

        return dexFile;
    }
}
