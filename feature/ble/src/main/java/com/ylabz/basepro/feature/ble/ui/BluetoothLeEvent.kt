package com.ylabz.basepro.feature.ble.ui

sealed interface BluetoothLeEvent {
    object FetchDevices : BluetoothLeEvent
    object FetchClassicDevices : BluetoothLeEvent // New event for Classic Bluetooth

    object RequestPermissions : BluetoothLeEvent
    object PermissionsGranted : BluetoothLeEvent
    object PermissionsDenied : BluetoothLeEvent
}

