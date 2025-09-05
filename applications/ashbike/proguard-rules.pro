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

# applications/ashbike/proguard-rules.pro

# Keep the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Hilt - Official rules to keep Hilt's generated code and entry points.
-keep class dagger.hilt.internal.aggregatedroot.codegen.*
-keep class hilt_aggregated_deps.*
-keep @dagger.hilt.android.HiltAndroidApp class * { <init>(); }
-keep class * extends android.app.Application {
    @dagger.hilt.android.HiltAndroidApp <init>();
}
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * {*;}
-keep class * extends androidx.lifecycle.SavedStateHandle {*;}

# Keep all ViewModels.
-keep class * extends androidx.lifecycle.ViewModel {
  <init>(...);
}

# Coroutines - Standard rules for Kotlin Coroutines.
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}