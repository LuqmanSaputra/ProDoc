package com.prodoc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prodoc.model.ProjectStatus

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val projectId: String,

    val name: String,
    val category: String,
    val description: String,
    val status: ProjectStatus,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    val parentProjectId: String? = null,

    val isSynced: Boolean = false
)