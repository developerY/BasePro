package com.ylabz.basepro.core.model.alarm

import kotlinx.serialization.Serializable

//@kotlinx. serialization. InternalSerializationApi
@Serializable
data class ProAlarm(
    val id: Int,
    val timeInMillis: Long,
    val message: String
)
