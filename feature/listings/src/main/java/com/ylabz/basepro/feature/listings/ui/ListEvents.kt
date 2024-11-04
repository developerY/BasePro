package com.ylabz.basepro.listings.ui

sealed class ListEvent {
    object LoadData : ListEvent()
    data class AddItem(val name: String) : ListEvent()
    data class DeleteItem(val itemId: Int) : ListEvent()
    data class OnItemClicked(val itemId: Int) : ListEvent()
    object DeleteAll: ListEvent()
    object OnRetry : ListEvent()
}
