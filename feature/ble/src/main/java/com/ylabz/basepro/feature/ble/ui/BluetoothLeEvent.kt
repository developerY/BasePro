package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue

sealed interface BluetoothLeEvent {
    object RequestEnableBluetooth : BluetoothLeEvent
    object StartScan : BluetoothLeEvent
    object StopScan : BluetoothLeEvent
    object FetchDevices : BluetoothLeEvent
    object RequestPermissions : BluetoothLeEvent
    object PermissionsGranted : BluetoothLeEvent
    object PermissionsDenied : BluetoothLeEvent
    object ConnectToSensorTag : BluetoothLeEvent
    object GattCharacteristicList : BluetoothLeEvent

    // Add this new event for battery level reading
    object ReadCharacteristics : BluetoothLeEvent


    //object FetchGattServices : BluetoothLeEvent

    // Read characteristic value
    /*data class GetCharacteristicValue(
        val service: DeviceService,
        val characteristic: DeviceCharacteristic
    ) : BluetoothLeEvent*/

    /* Optional: Enable notification
    data class EnableNotification(
        val service: DeviceService,
        val characteristic: DeviceCharacteristic
    ) : BluetoothLeEvent

    // Optional: Write characteristic value
    data class WriteCharacteristicValue(
        val service: DeviceService,
        val characteristic: DeviceCharacteristic,
        val value: String
    ) : BluetoothLeEvent*/
}

