package com.mutualmobile.gradle.plugins.dexinfo

import info.persistent.dex.DexMethodCounts
import org.gradle.api.Project

class DexinfoPluginExtension {
    private final Project project

    def maxDepth
    def includeClasses
    def packageFilter
    def filter


            /*
    --filter=[all|defined_only|referenced_only]: Whether to count all methods (the default), just those defined in the input file, or just those that are referenced in it. Note that referenced methods count against the 64K method limit too.
    */
    public DexinfoPluginExtension(Project project) {
        this.project = project
        this.maxDepth = Integer.MAX_VALUE
        this.includeClasses = true
        this.packageFilter = ""
        this.filter = DexMethodCounts.Filter.ALL.toString();
    }
}
