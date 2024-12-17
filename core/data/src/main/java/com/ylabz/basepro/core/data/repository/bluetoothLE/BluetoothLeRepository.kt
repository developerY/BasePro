package com.ylabz.basepro.core.data.repository.bluetoothLE

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo>
    suspend fun startScan()
    suspend fun stopScan()
}