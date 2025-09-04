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

# Rules from missing_rules.txt for ashbike app (core.data module classes)
-keep class com.ylabz.basepro.core.data.api.interfaces.MapsAPI { *; }
-keep class com.ylabz.basepro.core.data.api.interfaces.YelpAPI { *; }
-keep class com.ylabz.basepro.core.data.di.AlarmModule_ProvideAlarmRepositoryFactory { *; }
-keep class com.ylabz.basepro.core.data.di.AppModule_ProvideContextFactory { *; }
-keep class com.ylabz.basepro.core.data.di.BLEModule_ProvideBluetoothAdapterFactory { *; }
-keep class com.ylabz.basepro.core.data.di.HealthModule_ProvideHealthSessionManagerFactory { *; }
-keep class com.ylabz.basepro.core.data.di.NetworkModule_BindsMapsAPIFactory { *; }
-keep class com.ylabz.basepro.core.data.di.NetworkModule_BindsYelpAPIFactory { *; }
-keep class com.ylabz.basepro.core.data.di.NetworkModule_ProvideApolloClientFactory { *; }
-keep class com.ylabz.basepro.core.data.repository.alarm.AlarmRepository { *; }
-keep class com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepImpl { *; }
-keep class com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository { *; }
-keep class com.ylabz.basepro.core.data.repository.nfc.NfcRepository { *; }
-keep class com.ylabz.basepro.core.data.repository.nfc.NfcRepositoryImpl { *; }
-keep class com.ylabz.basepro.core.data.repository.travel.DrivingPtsRepImp { *; }
-keep class com.ylabz.basepro.core.data.repository.travel.DrivingPtsRepository { *; }
-keep class com.ylabz.basepro.core.data.repository.travel.LocationRepository { *; }
-keep class com.ylabz.basepro.core.data.repository.travel.LocationRepositoryImpl { *; }
-keep class com.ylabz.basepro.core.data.repository.weather.WeatherRepo { *; }
-keep class com.ylabz.basepro.core.data.repository.weather.WeatherRepoImpl { *; }
-keep class com.ylabz.basepro.core.data.service.health.HealthSessionManager { *; }
