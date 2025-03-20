package com.ylabz.basepro.feature.nfc.ui

import android.nfc.NfcEvent

sealed class NfcReadEventOld {
    object EnableNfcRead : NfcReadEventOld()
    object StartScan : NfcReadEventOld()
    object Retry : NfcReadEventOld()
}

sealed class NfcReadEvent {
    object StartScan : NfcReadEvent()
    object StopScan : NfcReadEvent()
    object Retry : NfcReadEvent()
    object EnableNfc : NfcReadEvent()
    data class UpdateWriteText(val text: String) : NfcReadEvent()
    object StartWrite : NfcReadEvent()
    object StopWrite : NfcReadEvent()
}
