package com.labuda.tempmonitor.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.labuda.tempmonitor.db.dao.DeviceDao
import com.labuda.tempmonitor.db.entities.Device

@Database(entities = [Device::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}