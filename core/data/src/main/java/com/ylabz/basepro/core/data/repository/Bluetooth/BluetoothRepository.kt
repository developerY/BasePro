package com.ylabz.basepro.core.data.repository.Bluetooth

interface BluetoothRepository {
    suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo>
}