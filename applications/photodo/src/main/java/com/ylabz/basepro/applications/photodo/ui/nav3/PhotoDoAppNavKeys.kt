package com.ylabz.basepro.applications.photodo.ui.nav3

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object PhotoDoHomeSectionKey : NavKey
@Serializable data object PhotoDoListSectionKey : NavKey
@Serializable data object PhotoDoSettingsSectionKey : NavKey

// Keys for the List/Detail flow within the PhotoDoListSection
@Serializable data object PhotoDoListContentKey : NavKey
@Serializable data class PhotoDoItemDetailKey(val itemId: String) : NavKey
