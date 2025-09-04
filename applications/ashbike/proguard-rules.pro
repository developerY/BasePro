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

# Keep the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Keep your app's data/model classes, which are often used with reflection.
-keep class com.ylabz.basepro.applications.bike.database.** { *; }
-keep class com.ylabz.basepro.core.model.** { *; }

# Hilt - These are the official rules to keep Hilt's generated code.
-keep class dagger.hilt.internal.aggregatedroot.codegen.*
-keep class hilt_aggregated_deps.*
-keep @dagger.hilt.android.HiltAndroidApp class * { <init>(); }
-keep class * extends android.app.Application {
    @dagger.hilt.android.HiltAndroidApp <init>();
}
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * {*;}
-keep class * extends androidx.lifecycle.SavedStateHandle {*;}

# This is a broader rule for ViewModels that is often helpful.
-keep class * extends androidx.lifecycle.ViewModel {
  <init>(...);
}

# Coroutines - Keep internal classes used by Kotlin Coroutines.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# OkHttp / Retrofit / Apollo - If you are using these for networking.
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-keep class com.apollographql.** { *; }