package com.labuda.tempmonitor.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.labuda.tempmonitor.db.entities.Device

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices WHERE address = :address")
    fun getDeviceByAddress(address: String) : Device

    @Query("SELECT * FROM devices")
    fun getAll() : List<Device>

    @Insert
    fun insert(device: Device)

    @Delete
    fun remove(device: Device)
}