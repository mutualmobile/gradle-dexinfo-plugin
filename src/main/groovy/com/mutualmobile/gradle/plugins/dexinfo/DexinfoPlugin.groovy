package com.mutualmobile.gradle.plugins.dexinfo

import org.gradle.api.Plugin
import org.gradle.api.Project

class DexinfoPlugin implements Plugin<Project> {

    final static String GROUP_NAME = 'Dexinfo'

    void apply(Project project) {
        applyExtensions(project);
        applyTasks(project)
    }

    static void applyExtensions(final Project project) {
        project.extensions.create('dexinfo', DexinfoPluginExtension, project)
    }

    static void applyTasks(final Project project) {
        project.afterEvaluate {
            project.android.getApplicationVariants().each { variant ->
                String varname = "dexinfo" + toCamelCase("${variant.name}")
                project.task(varname, type: DexinfoTask, group: GROUP_NAME) { task ->
                    task.variantName = variant.name
                    task.dirName = variant.dirName
                }
            }
        }
    }

    static String toCamelCase(String str) {
        if (!str || str.isAllWhitespace()) {
            return ''
        }
        return str.capitalize()
    }
}
