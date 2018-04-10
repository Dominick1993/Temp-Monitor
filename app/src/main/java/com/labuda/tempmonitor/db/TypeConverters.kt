package com.labuda.tempmonitor.db

import android.arch.persistence.room.TypeConverter
import com.labuda.tempmonitor.ServerType
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun serverTypeToString(value: ServerType?): String? {
        return value?.name
    }

    @TypeConverter
    fun fromString(value: String?): ServerType? {
        return if (value == null) null else ServerType.valueOf(value)
    }


}