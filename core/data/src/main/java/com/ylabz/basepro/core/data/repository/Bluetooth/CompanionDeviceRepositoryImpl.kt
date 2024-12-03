package com.ylabz.basepro.core.data.repository.Bluetooth

import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentSender
import android.os.ParcelUuid
import java.util.UUID
import java.util.regex.Pattern
import javax.inject.Inject

class CompanionDeviceRepositoryImpl @Inject constructor(
    private val context: Context
) : CompanionDeviceRepository {

    override fun createDeviceFilter(namePattern: String?, serviceUuid: UUID?): BluetoothDeviceFilter {
        val builder = BluetoothDeviceFilter.Builder()

        namePattern?.let {
            builder.setNamePattern(Pattern.compile(it))
        }

        serviceUuid?.let {
            builder.addServiceUuid(ParcelUuid(it), null)
        }

        return builder.build()
    }

    override fun associateDevice(
        namePattern: String?,
        serviceUuid: UUID?,
        onDeviceFound: (IntentSender) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val companionDeviceManager =
            context.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager

        val deviceFilter = createDeviceFilter(namePattern, serviceUuid)

        val associationRequest = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .setSingleDevice(true) // Only one device should match
            .build()

        companionDeviceManager.associate(
            associationRequest,
            object : CompanionDeviceManager.Callback() {
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    onDeviceFound(chooserLauncher)
                }

                override fun onFailure(error: CharSequence?) {
                    onFailure(error?.toString() ?: "Unknown error")
                }
            },
            null // Use the main thread
        )
    }
}
