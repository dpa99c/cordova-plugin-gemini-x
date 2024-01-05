#!/usr/bin/env node

'use strict';

const fs = require('fs');
const path = require("path");

const appBuildGradleRelativePath = 'platforms/android/app/build.gradle';
const kotlinAndroidExtensions = "apply plugin: 'kotlin-android-extensions'";


module.exports = function(context){
    const appBuildGradleAbsolutePath = path.join(context.opts.projectRoot, appBuildGradleRelativePath);
    const appBuildGradle = fs.readFileSync(appBuildGradleAbsolutePath, 'utf8');

    // Remove kotlin-android-extensions plugin if it exists since it is not compatible with the new Android Gradle Plugin 8+
    if (appBuildGradle.includes(kotlinAndroidExtensions)) {
        const newAppBuildGradle = appBuildGradle.replace(kotlinAndroidExtensions, '');
        fs.writeFileSync(appBuildGradleAbsolutePath, newAppBuildGradle, 'utf8');
    }

};
