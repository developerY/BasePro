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

# Remove all debug- and verbose-level log calls
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# Rules from missing_rules.txt for ashbike app (features.trips module classes)
-keep class com.ylabz.basepro.applications.bike.features.trips.domain.MarkRideAsSyncedUseCase { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.domain.SyncRideUseCase { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRouteKt { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel_HiltModules$KeyModule { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel_HiltModules_BindsModule_Binds_LazyMapKey { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel_HiltModules_KeyModule_Provide_LazyMapKey { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.components.MapPathScreenKt { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailScreenKt { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel_HiltModules$KeyModule { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey { *; }
-keep class com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey { *; }
-dontwarn com.ylabz.basepro.applications.bike.database.mapper.RideMappersKt
-dontwarn com.ylabz.basepro.core.ui.theme.ColorKt
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthEvent$Insert
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthEvent$LoadHealthData
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthEvent$RequestPermissions
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthEvent
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthSideEffect$BikeRideSyncedToHealth
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthSideEffect$LaunchPermissions
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthSideEffect
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthUiState$Error
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthUiState$Success
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthUiState