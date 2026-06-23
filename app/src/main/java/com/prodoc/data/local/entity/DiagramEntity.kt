package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prodoc.model.QAStatus

@Entity(tableName = "diagrams")
data class DiagramEntity(
    @PrimaryKey
    val diagramId: String,
    val projectId: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val pdfFilePath: String? = null,
    val drawioFilePath: String? = null,
    val qaStatus: QAStatus = QAStatus.DRAFT,
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)