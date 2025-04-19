package com.ylabz.basepro.applications.bike.database.mapper

import com.ylabz.basepro.applications.bike.database.BikeProEntity


typealias BikePro = BikeProEntity

fun BikeProEntity.toBikePro(): BikePro {
    return this
    /*return BasePro(
        title = title,
        description = description,
        //@ColumnInfo(name = "timestamp") val timestamp : ZonedDateTime = ZonedDateTime.now(),
        timestamp = timestamp, //= Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        lat = lat,
        lon = lon,
        alarm_on = alarm_on,
        isCompleted = isCompleted,
        imgPath = imgPath,
        audioPath = audioPath,
        todoId = todoId
    )*/
}

// Extension function
typealias BaseProEntity = BikePro

fun BikePro.toBikeProEntity(): BaseProEntity {
    return this
    /*return BikeProEntity(
        title = title,
        description = description,
        //@ColumnInfo(name = "timestamp") val timestamp : ZonedDateTime = ZonedDateTime.now(),
        timestamp = timestamp, //= Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        lat = lat,
        lon = lon,
        alarm_on = alarm_on,
        isCompleted = isCompleted,
        imgPath = imgPath,
        audioPath = audioPath,
        todoId = todoId// auto created on insert
    )*/
}