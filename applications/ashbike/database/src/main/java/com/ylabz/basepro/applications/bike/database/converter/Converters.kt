package com.ylabz.basepro.applications.bike.database.converter

import android.net.Uri
import androidx.room.TypeConverter
import androidx.core.net.toUri

//import kotlinx.datetime.LocalDateTime

class Converters {

    /*@TypeConverter
    fun fromTimestrap(dateString: String?): LocalDateTime? {
        return dateString?.let { LocalDateTime.parse(dateString) }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }*/

    @TypeConverter
    fun fromString(value: String?): Uri? {
        return if (value == null) null else value.toUri()
    }

    @TypeConverter
    fun toString(uri: Uri?): String? {
        return uri?.toString()
    }
}
