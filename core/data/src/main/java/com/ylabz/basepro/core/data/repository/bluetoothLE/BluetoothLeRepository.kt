package com.ylabz.basepro.core.data.repository.bluetoothLE

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo>
    suspend fun startScan()
    suspend fun stopScan()
}