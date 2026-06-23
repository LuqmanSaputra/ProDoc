package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val historyId: String,
    val projectId: String,
    val actionDescription: String,
    val timestamp: Long = System.currentTimeMillis()
)