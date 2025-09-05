# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Rules from missing_rules.txt for ashbike app (features.main module classes)
# This rule keeps all classes within the .main feature package.
# This includes your ViewModel, UiRoute, UseCases, and any other related classes,
# preventing them from being removed by R8.
-keep class com.ylabz.basepro.applications.bike.features.main.** { *; }