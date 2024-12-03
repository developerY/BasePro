package com.ylabz.basepro.core.data.repository.Bluetooth

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo>
}