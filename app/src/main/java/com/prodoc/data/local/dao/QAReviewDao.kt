package com.prodoc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prodoc.data.local.entity.QAReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QAReviewDao {
    @Query("SELECT * FROM qa_reviews WHERE itemId = :itemId ORDER BY reviewedAt DESC")
    fun getReviewsByItem(itemId: String): Flow<List<QAReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: QAReviewEntity)
}