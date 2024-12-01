package com.ylabz.basepro.core.data.repository.ble

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo>
}