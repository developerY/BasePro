package com.ylabz.basepro.feature.ble.ui

sealed interface BluetoothLeEvent {
    object FetchDevices : BluetoothLeEvent
}

