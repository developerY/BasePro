package com.ylabz.basepro.core.data.repository.Bluetooth

import android.companion.BluetoothDeviceFilter
import android.content.IntentSender
import java.util.UUID

interface CompanionDeviceRepository {
    fun createDeviceFilter(namePattern: String?, serviceUuid: UUID?): BluetoothDeviceFilter
    fun associateDevice(
        namePattern: String?,
        serviceUuid: UUID?,
        onDeviceFound: (IntentSender) -> Unit,
        onFailure: (String) -> Unit
    )
}
