package com.ylabz.twincam.cam.ui

sealed class CamEvent {
    object LoadData : CamEvent()
    data class AddItem(val name: String) : CamEvent()
    data class DeleteItem(val itemId: Int) : CamEvent()
    data class OnItemClicked(val itemId: Int) : CamEvent()
    object OnRetry : CamEvent()
}
