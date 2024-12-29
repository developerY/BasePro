package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo

sealed interface BluetoothLeEvent {
    object RequestEnableBluetooth : BluetoothLeEvent
    object StartScan : BluetoothLeEvent
    object StopScan : BluetoothLeEvent
    object FetchDevices : BluetoothLeEvent
    object RequestPermissions : BluetoothLeEvent
    object PermissionsGranted : BluetoothLeEvent
    object PermissionsDenied : BluetoothLeEvent
    //data class TiTagSensorDetected(val device: BluetoothDeviceInfo) : BluetoothLeEvent

}

