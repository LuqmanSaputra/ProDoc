package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val syncId: Long = 0,
    val tableName: String,
    val recordId: String,
    val operation: String,
    val timestamp: Long = System.currentTimeMillis()
)