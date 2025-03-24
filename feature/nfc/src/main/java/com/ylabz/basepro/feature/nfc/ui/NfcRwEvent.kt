package com.ylabz.basepro.feature.nfc.ui

sealed class NfcReadEventOld {
    object EnableNfcRead : NfcReadEventOld()
    object StartScan : NfcReadEventOld()
    object Retry : NfcReadEventOld()
}

sealed class NfcRwEvent {
    object StartScan : NfcRwEvent()
    object StopScan : NfcRwEvent()
    object Retry : NfcRwEvent()
    object EnableNfc : NfcRwEvent()
    data class UpdateWriteText(val text: String) : NfcRwEvent()
    object StartWrite : NfcRwEvent()
    object StopWrite : NfcRwEvent()
}
