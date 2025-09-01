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
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.ylabz.basepro.applications.bike.database.BikeRideDao
-dontwarn com.ylabz.basepro.applications.bike.database.BikeRideEntity
-dontwarn com.ylabz.basepro.applications.bike.database.BikeRideRepo
-dontwarn com.ylabz.basepro.applications.bike.database.RideLocationEntity
-dontwarn com.ylabz.basepro.applications.bike.database.RideWithLocations
-dontwarn com.ylabz.basepro.applications.bike.database.di.DataStoreModule_ProvideDataStoreFactory
-dontwarn com.ylabz.basepro.applications.bike.database.di.DatabaseModule_ProvideBikeRideDBFactory
-dontwarn com.ylabz.basepro.applications.bike.database.di.DatabaseModule_ProvideBikeRideDaoFactory
-dontwarn com.ylabz.basepro.applications.bike.database.di.DatabaseModule_ProvideRealBikeRideRepositoryFactory
-dontwarn com.ylabz.basepro.applications.bike.database.repository.AppSettingsRepository
-dontwarn com.ylabz.basepro.applications.bike.database.repository.DataStoreAppSettingsRepository
-dontwarn com.ylabz.basepro.applications.bike.database.repository.DataStoreUserProfileRepository
-dontwarn com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
-dontwarn com.ylabz.basepro.applications.bike.features.main.ui.BikeUiRouteKt
-dontwarn com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel
-dontwarn com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.main.ui.BikeViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.main.ui.WeatherUseCase
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiRouteKt
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiState$Success
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsUiState
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.settings.ui.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.trips.domain.MarkRideAsSyncedUseCase
-dontwarn com.ylabz.basepro.applications.bike.features.trips.domain.SyncRideUseCase
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.TripsUIRouteKt
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.TripsViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.components.MapPathScreenKt
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailScreenKt
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.applications.bike.features.trips.ui.components.RideDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.core.data.api.interfaces.MapsAPI
-dontwarn com.ylabz.basepro.core.data.api.interfaces.YelpAPI
-dontwarn com.ylabz.basepro.core.data.di.AlarmModule_ProvideAlarmRepositoryFactory
-dontwarn com.ylabz.basepro.core.data.di.AppModule_ProvideContextFactory
-dontwarn com.ylabz.basepro.core.data.di.BLEModule_ProvideBluetoothAdapterFactory
-dontwarn com.ylabz.basepro.core.data.di.HealthModule_ProvideHealthSessionManagerFactory
-dontwarn com.ylabz.basepro.core.data.di.NetworkModule_BindsMapsAPIFactory
-dontwarn com.ylabz.basepro.core.data.di.NetworkModule_BindsYelpAPIFactory
-dontwarn com.ylabz.basepro.core.data.di.NetworkModule_ProvideApolloClientFactory
-dontwarn com.ylabz.basepro.core.data.repository.alarm.AlarmRepository
-dontwarn com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepImpl
-dontwarn com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
-dontwarn com.ylabz.basepro.core.data.repository.nfc.NfcRepository
-dontwarn com.ylabz.basepro.core.data.repository.nfc.NfcRepositoryImpl
-dontwarn com.ylabz.basepro.core.data.repository.travel.DrivingPtsRepImp
-dontwarn com.ylabz.basepro.core.data.repository.travel.DrivingPtsRepository
-dontwarn com.ylabz.basepro.core.data.repository.travel.LocationRepository
-dontwarn com.ylabz.basepro.core.data.repository.travel.LocationRepositoryImpl
-dontwarn com.ylabz.basepro.core.data.repository.weather.WeatherRepo
-dontwarn com.ylabz.basepro.core.data.repository.weather.WeatherRepoImpl
-dontwarn com.ylabz.basepro.core.data.service.health.HealthSessionManager
-dontwarn com.ylabz.basepro.core.ui.BikeScreen$HomeBikeScreen
-dontwarn com.ylabz.basepro.core.ui.BikeScreen$RideDetailScreen
-dontwarn com.ylabz.basepro.core.ui.BikeScreen$SettingsBikeScreen
-dontwarn com.ylabz.basepro.core.ui.BikeScreen$TripBikeScreen
-dontwarn com.ylabz.basepro.core.ui.NavigationCommand$To
-dontwarn com.ylabz.basepro.core.ui.NavigationCommand$ToTab
-dontwarn com.ylabz.basepro.core.ui.NavigationCommand
-dontwarn com.ylabz.basepro.core.ui.theme.AshBikeThemeKt
-dontwarn com.ylabz.basepro.core.util.Logging
-dontwarn com.ylabz.basepro.feature.alarm.ui.AlarmViewModel
-dontwarn com.ylabz.basepro.feature.alarm.ui.AlarmViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.alarm.ui.AlarmViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.alarm.ui.AlarmViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeViewModel
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.ble.ui.BluetoothLeViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.camera.ui.CamViewModel
-dontwarn com.ylabz.basepro.feature.camera.ui.CamViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.camera.ui.CamViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.camera.ui.CamViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthViewModel
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.heatlh.ui.HealthViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.listings.ui.ListViewModel
-dontwarn com.ylabz.basepro.feature.listings.ui.ListViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.listings.ui.ListViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.listings.ui.ListViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.maps.ui.MapViewModel
-dontwarn com.ylabz.basepro.feature.maps.ui.MapViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.maps.ui.MapViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.maps.ui.MapViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.nfc.ui.NfcViewModel
-dontwarn com.ylabz.basepro.feature.nfc.ui.NfcViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.nfc.ui.NfcViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.nfc.ui.NfcViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopEvent$FindCafesInArea
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopEvent
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopUIState$Success
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopUIState
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.places.ui.CoffeeShopViewModel_HiltModules_KeyModule_Provide_LazyMapKey
-dontwarn com.ylabz.basepro.feature.weather.ui.WeatherViewModel
-dontwarn com.ylabz.basepro.feature.weather.ui.WeatherViewModel_HiltModules$KeyModule
-dontwarn com.ylabz.basepro.feature.weather.ui.WeatherViewModel_HiltModules_BindsModule_Binds_LazyMapKey
-dontwarn com.ylabz.basepro.feature.weather.ui.WeatherViewModel_HiltModules_KeyModule_Provide_LazyMapKey