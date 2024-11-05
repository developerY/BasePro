package com.ylabz.basepro.core.database.mapper

import com.ylabz.basepro.core.database.BaseProEntity

typealias BasePro = BaseProEntity

fun BaseProEntity.toBasePro(): BasePro {
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
typealias BaseProEntity = BasePro

fun BasePro.toBaseProEntity(): BaseProEntity {
    return this
    /*return BaseProEntity(
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