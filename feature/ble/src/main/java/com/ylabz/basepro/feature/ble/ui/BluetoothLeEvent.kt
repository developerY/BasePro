package com.ylabz.basepro.feature.ble.ui

sealed interface BluetoothLeEvent {
    object RequestEnableBluetooth : BluetoothLeEvent
    object StartScan : BluetoothLeEvent
    object StopScan : BluetoothLeEvent
    object FetchDevices : BluetoothLeEvent
    object RequestPermissions : BluetoothLeEvent
    object PermissionsGranted : BluetoothLeEvent
    object PermissionsDenied : BluetoothLeEvent
}

