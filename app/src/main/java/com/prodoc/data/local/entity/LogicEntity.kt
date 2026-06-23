package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prodoc.model.QAStatus

@Entity(tableName = "logics")
data class LogicEntity(
    @PrimaryKey
    val logicId: String,
    val projectId: String,
    val name: String,
    val description: String,
    val configText: String,
    val photoUrl: String? = null,
    val qaStatus: QAStatus = QAStatus.DRAFT,
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)