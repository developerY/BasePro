package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

sealed class PhotoDoDetailEvent {
    data object OnAddPhotoClicked : PhotoDoDetailEvent() // <-- ADD THIS LINE
    // Example: data class OnAddPhotoClicked(val photoUri: String) : PhotoDoDetailEvent()
}