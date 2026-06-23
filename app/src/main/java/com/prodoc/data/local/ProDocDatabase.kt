package com.prodoc.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.prodoc.data.local.dao.*
import com.prodoc.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ProjectEntity::class,
        MaterialEntity::class,
        LogicEntity::class,
        DiagramEntity::class,
        QAReviewEntity::class,
        HistoryEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class ProDocDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun materialDao(): MaterialDao
    abstract fun logicDao(): LogicDao
    abstract fun diagramDao(): DiagramDao
    abstract fun qaReviewDao(): QAReviewDao
    abstract fun historyDao(): HistoryDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: ProDocDatabase? = null

        fun getDatabase(context: Context): ProDocDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProDocDatabase::class.java,
                    "prodoc_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}