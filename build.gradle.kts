buildscript {

    dependencies {
        classpath(BuildScripts.gradle)
        classpath(BuildScripts.hilt)
    }

}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id (PluginsDef.androidApplication) version Versions.appAndLibPlugin apply  false
    id (PluginsDef.androidLibrary) version  Versions.appAndLibPlugin apply false
    id (PluginsDef.kotlinAndroid) version Versions.kotlinAndroid apply false
    id (PluginsDef.daggerHilt) version Versions.hilt apply false
}