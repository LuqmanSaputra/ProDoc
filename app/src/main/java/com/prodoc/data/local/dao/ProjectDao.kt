package com.prodoc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prodoc.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE parentProjectId IS NULL ORDER BY updatedAt DESC")
    fun getAllMainProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE parentProjectId = :parentId ORDER BY createdAt ASC")
    fun getSubProjectsByParent(parentId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE projectId = :projectId")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE projectId = :projectId")
    fun getProjectFlowById(projectId: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT * FROM projects")
    fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

}