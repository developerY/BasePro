package com.ylabz.basepro.feature.ble.ui

sealed interface BluetoothLeEvent {
    object FetchDevices : BluetoothLeEvent
    object RequestPermissions : BluetoothLeEvent
    object PermissionsGranted : BluetoothLeEvent
    object PermissionsDenied : BluetoothLeEvent
}

