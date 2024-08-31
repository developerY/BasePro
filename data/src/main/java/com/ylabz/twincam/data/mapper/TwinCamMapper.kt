package com.ylabz.twincam.data.mapper

import com.ylabz.twincam.data.TwinCamEntity

typealias TwinCam = TwinCamEntity

fun TwinCamEntity.toTwinCam(): TwinCam {
    return this
    /*return TwinCam(
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
typealias TwinCamEntity = TwinCam

fun TwinCam.toTwinCamEntity(): TwinCamEntity {
    return this
    /*return TwinCamEntity(
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