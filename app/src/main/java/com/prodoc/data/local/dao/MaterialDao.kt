package com.prodoc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.prodoc.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getMaterialsByProject(projectId: String): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE materialId = :materialId LIMIT 1")
    suspend fun getMaterialById(materialId: String): MaterialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Update
    suspend fun updateMaterial(material: MaterialEntity)

    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)

    @Query("SELECT * FROM materials")
    fun getAllMaterialsFlow(): Flow<List<MaterialEntity>>
}