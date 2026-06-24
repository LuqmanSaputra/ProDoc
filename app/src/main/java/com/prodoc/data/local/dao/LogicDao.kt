package com.prodoc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.prodoc.data.local.entity.LogicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogicDao {
    @Query("SELECT * FROM logics WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getLogicsByProject(projectId: String): Flow<List<LogicEntity>>

    @Query("SELECT * FROM logics WHERE logicId = :logicId LIMIT 1")
    suspend fun getLogicById(logicId: String): LogicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogic(logic: LogicEntity)

    @Update
    suspend fun updateLogic(logic: LogicEntity)

    @Delete
    suspend fun deleteLogic(logic: LogicEntity)

    @Query("SELECT * FROM logics")
    fun getAllLogicsFlow(): Flow<List<LogicEntity>>
}