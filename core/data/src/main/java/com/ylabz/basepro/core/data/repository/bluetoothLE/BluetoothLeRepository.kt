package com.ylabz.basepro.core.data.repository.bluetoothLE

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.ScanState
import kotlinx.coroutines.flow.StateFlow

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?>
    val scanState: StateFlow<ScanState>
    suspend fun startScan()
    suspend fun stopScan()
}