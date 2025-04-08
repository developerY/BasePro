package com.ylabz.basepro.core.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ylabz.basepro.core.database.converter.Converters

@Entity(tableName = "basepro_table")
@TypeConverters(Converters::class)
data class BaseProEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val todoId: Int = 0,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "image_path") val imgPath: String? = null // Store image path as a String
    // @ColumnInfo(name = "timestamp") val timestamp : ZonedDateTime = ZonedDateTime.now(),
    // @ColumnInfo(name = "date") val timestamp: kotlinx.datetime.LocalDateTime? = null,
    // kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    /*@ColumnInfo(name = "lat") val lat: Double = 0.0,
    @ColumnInfo(name = "lon") val lon: Double = 0.0,
    @ColumnInfo(name = "alarmOn") val alarmOn: Boolean = false,
    @ColumnInfo(name = "completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "image_path") val imgPath: Uri? = null,
    @ColumnInfo(name = "audio_path") val audioPath: Uri? = null*/

)

data class BaseProUpdate(
    val id: Int,
    //val alarmOn: Boolean
)