package com.ylabz.basepro.core.data.repository.bluetoothLE

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import kotlinx.coroutines.flow.StateFlow

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?>
    val scanState: StateFlow<ScanState>
    val gattConnectionState: StateFlow<GattConnectionState>
    val gattCharacteristicList: StateFlow<List<GattCharacteristicValue>>
    val gattServicesList: StateFlow<List<DeviceService>>
    suspend fun startScan(scanAll: Boolean) // Added scanAll parameter
    suspend fun stopScan()
    suspend fun connectToDevice()
    suspend fun readAllCharacteristics()
}