package com.ylabz.basepro.camera.ui

sealed interface CamEvent {
    object LoadData : CamEvent
    object OnRetry : CamEvent
    data class AddItem(val name: String, val description: String, val imgPath: String) : CamEvent
    data class DeleteItem(val itemId: Int) : CamEvent
    object DeleteAll : CamEvent
    data class OnItemClicked(val itemId: Int) : CamEvent
}