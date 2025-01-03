package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.bluetooth.BluetoothDevice
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import kotlinx.coroutines.flow.StateFlow

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?>
    val scanState: StateFlow<ScanState>
    val gattConnectionState: StateFlow<GattConnectionState>
    suspend fun startScan()
    suspend fun stopScan()
    suspend fun connectToDevice()
    suspend fun readBatteryLevel(): StateFlow<Int?>
}