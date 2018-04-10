package com.labuda.tempmonitor.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.labuda.tempmonitor.ServerType
import java.util.*

@Entity(tableName = "devices")
data class Device(
        @PrimaryKey
        var address: String,

        var name: String,

        @ColumnInfo(name = "server_type")
        var serverType: ServerType,

        @ColumnInfo(name = "date_added")
        var dateAdded : Date
)