package com.ylabz.basepro.listings.ui

sealed class CamEvent {
    object LoadData : CamEvent()
    data class AddItem(val name: String) : CamEvent()
    data class DeleteItem(val itemId: Int) : CamEvent()
    data class OnItemClicked(val itemId: Int) : CamEvent()
    object DeleteAll: CamEvent()
    object OnRetry : CamEvent()
}
