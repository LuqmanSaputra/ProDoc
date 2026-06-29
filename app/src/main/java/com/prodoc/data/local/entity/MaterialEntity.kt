package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prodoc.model.QAStatus

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey
    val materialId: String,
    val projectId: String,
    val name: String,
    val description: String,
    val quantity: Double,
    val unit: MaterialUnit,
    val unitPrice: Double,
    val photoUrl: String? = null,
    val qaStatus: QAStatus = QAStatus.DRAFT,
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = quantity * unitPrice
}

enum class MaterialUnit {
    PCS, METER, CM, MM, ROLL, BOX, PACK, UNIT, LITER, KG, OTHER
}