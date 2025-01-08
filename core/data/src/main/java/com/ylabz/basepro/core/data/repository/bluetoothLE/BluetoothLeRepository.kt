package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothLeRepository {
    suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?>
    //suspend fun deviceCharateristic(deviceService: DeviceService, deviceCharacteristic: DeviceCharacteristic): StateFlow<GattCharacteristicValue>
    val scanState: StateFlow<ScanState>
    val gattConnectionState: StateFlow<GattConnectionState>
    val gattCharacteristicList: StateFlow<List<GattCharacteristicValue>>
    val gattServicesList: StateFlow<List<DeviceService>>
    suspend fun startScan()
    suspend fun stopScan()
    suspend fun connectToDevice()
    //fun getCachedGatt(): BluetoothGatt?
    //suspend fun readCharacteristic(characteristic: BluetoothGattCharacteristic): Boolean
    //suspend fun writeCharacteristic(characteristic: BluetoothGattCharacteristic): Boolean
    //suspend fun enableNotifications(characteristic: BluetoothGattCharacteristic): Boolean
    //val notificationFlow: Flow<Pair<String, String>> // Emits characteristic UUID and value when notified
    //fun cacheServicesAndCharacteristics(gatt: BluetoothGatt)
    suspend fun readAllCharacteristics()
}