package com.simperium;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SimperiumPlugin implements Plugin<Project> {

    public void apply(Project project) {

        project.extensions.create("simperium", SimperiumPluginExtension)

    }



}