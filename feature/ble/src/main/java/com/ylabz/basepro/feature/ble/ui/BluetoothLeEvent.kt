package com.ylabz.basepro.feature.ble.ui

sealed interface BluetoothLeEvent {
    object RequestEnableBluetooth : BluetoothLeEvent
    /*object ScanStarted : BluetoothLeEvent
    object ScanStopped : BluetoothLeEvent*/
    object FetchDevices : BluetoothLeEvent
    object RequestPermissions : BluetoothLeEvent
    object PermissionsGranted : BluetoothLeEvent
    object PermissionsDenied : BluetoothLeEvent
}

