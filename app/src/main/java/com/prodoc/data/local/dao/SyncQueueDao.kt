package com.prodoc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prodoc.data.local.entity.SyncQueueEntity

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY timestamp ASC")
    suspend fun getFullQueue(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(syncItem: SyncQueueEntity)

    @Delete
    suspend fun dequeue(syncItem: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE recordId = :recordId AND tableName = :tableName")
    suspend fun removeByRecord(recordId: String, tableName: String)

    @Query("SELECT COUNT(*) FROM sync_queue")
    fun getSyncQueueCountFlow(): kotlinx.coroutines.flow.Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncQueue: SyncQueueEntity)
}