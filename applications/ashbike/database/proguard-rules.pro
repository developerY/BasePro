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

# Rules from missing_rules.txt for ashbike app (database module classes)
-keep class com.ylabz.basepro.applications.bike.database.BikeRideDao { *; }
-keep class com.ylabz.basepro.applications.bike.database.BikeRideEntity { *; }
-keep class com.ylabz.basepro.applications.bike.database.BikeRideRepo { *; }
-keep class com.ylabz.basepro.applications.bike.database.RideLocationEntity { *; }
-keep class com.ylabz.basepro.applications.bike.database.RideWithLocations { *; }
-keep class com.ylabz.basepro.applications.bike.database.di.DataStoreModule_ProvideDataStoreFactory { *; }
-keep class com.ylabz.basepro.applications.bike.database.di.DatabaseModule_ProvideBikeRideDBFactory { *; }
-keep class com.ylabz.basepro.applications.bike.database.di.DatabaseModule_ProvideBikeRideDaoFactory { *; }
-keep class com.ylabz.basepro.applications.bike.database.di.DatabaseModule_ProvideRealBikeRideRepositoryFactory { *; }
-keep class com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository { *; }
-keep class com.ylabz.basepro.applications.bike.database.repository.DataStoreAppSettingsRepository { *; }
-keep class com.ylabz.basepro.applications.bike.database.repository.DataStoreUserProfileRepository { *; }
-keep class com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository { *; }
