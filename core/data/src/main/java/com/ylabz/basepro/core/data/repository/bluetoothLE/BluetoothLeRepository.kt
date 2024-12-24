package com.ylabz.basepro.core.data.repository.bluetoothLE

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevices(): StateFlow<BluetoothDeviceInfo?>
    suspend fun startScan()
    suspend fun stopScan()
}