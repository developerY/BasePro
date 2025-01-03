package com.ylabz.basepro.core.model.ble

sealed class GattConnectionState {
    object Disconnected : GattConnectionState()
    object Connecting : GattConnectionState()
    object Connected : GattConnectionState()
}
