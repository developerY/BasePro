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

# Rules from missing_rules.txt for ashbike app (features.settings module classes)
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiRouteKt { *; }
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiState$Success { *; }
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiState { *; }
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel { *; }
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel_HiltModules$KeyModule { *; }
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey { *; }
-keep class com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey { *; }
-dontwarn com.ylabz.basepro.applications.bike.database.ProfileData
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeEvent
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeRouteKt
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthRouteKt
-dontwarn com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
-dontwarn com.ylabz.basepro.feature.nfc.ui.NfcUiState
-dontwarn com.ylabz.basepro.feature.nfc.ui.components.NfcScanScreenKt
-dontwarn com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreenKt
-dontwarn com.ylabz.basepro.feature.qrscanner.ui.QRCodeScannerScreenKt