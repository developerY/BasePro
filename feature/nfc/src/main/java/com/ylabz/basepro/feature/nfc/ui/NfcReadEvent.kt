package com.ylabz.basepro.feature.nfc.ui

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
}
