package com.prodoc

import android.app.Application
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.domain.hierarchy.HierarchyService
import com.prodoc.repository.*
import java.util.concurrent.TimeUnit
import com.prodoc.data.remote.SyncWorker
import androidx.work.*

class ProDocApplication : Application() {

    val database: ProDocDatabase by lazy {
        ProDocDatabase.getDatabase(this)
    }

    val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl(
            database = database,
            projectDao = database.projectDao(),
            materialDao = database.materialDao(),
            logicDao = database.logicDao(),
            diagramDao = database.diagramDao(),
            historyDao = database.historyDao(),
            syncQueueDao = database.syncQueueDao(),
            hierarchyService = HierarchyService()
        )
    }

    override fun onCreate() {
        super.onCreate()
        setupAutomaticCloudSync()
    }

    private fun setupAutomaticCloudSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ProDocCloudSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}