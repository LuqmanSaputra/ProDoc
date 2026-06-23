package com.prodoc.data.local

import androidx.room.TypeConverter
import com.prodoc.model.ProjectStatus
import com.prodoc.model.QAStatus

class Converters {

    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String {
        return status.name
    }

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus {
        return try {
            ProjectStatus.valueOf(value)
        } catch (_: IllegalArgumentException) {
            ProjectStatus.DRAFT
        }
    }

    @TypeConverter
    fun fromQAStatus(status: QAStatus): String {
        return status.name
    }

    @TypeConverter
    fun toQAStatus(value: String): QAStatus {
        return try {
            QAStatus.valueOf(value)
        } catch (_: IllegalArgumentException) {
            QAStatus.DRAFT
        }
    }
}