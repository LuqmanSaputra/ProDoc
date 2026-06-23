package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prodoc.model.QAStatus

@Entity(tableName = "qa_reviews")
data class QAReviewEntity(
    @PrimaryKey
    val reviewId: String,
    val itemId: String,
    val itemType: String,
    val status: QAStatus,
    val rejectionReason: String? = null,
    val reviewerId: String,
    val reviewedAt: Long = System.currentTimeMillis()
)