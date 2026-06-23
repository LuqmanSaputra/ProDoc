package com.prodoc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.prodoc.data.local.entity.DiagramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagramDao {
    @Query("SELECT * FROM diagrams WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getDiagramsByProject(projectId: String): Flow<List<DiagramEntity>>

    @Query("SELECT * FROM diagrams WHERE diagramId = :diagramId LIMIT 1")
    suspend fun getDiagramById(diagramId: String): DiagramEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagram(diagram: DiagramEntity)

    @Update
    suspend fun updateDiagram(diagram: DiagramEntity)

    @Delete
    suspend fun deleteDiagram(diagram: DiagramEntity)
}