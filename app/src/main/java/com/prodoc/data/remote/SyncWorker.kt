package com.prodoc.data.remote

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.prodoc.ProDocApplication
import com.prodoc.data.local.entity.*
import com.prodoc.model.ProjectStatus
import com.prodoc.model.QAStatus
import kotlinx.coroutines.tasks.await

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val app = applicationContext as ProDocApplication
        val database = app.database
        val syncQueueDao = database.syncQueueDao()
        val firestore = FirebaseFirestore.getInstance()

        try {

            val queueItems = syncQueueDao.getFullQueue()
            if (queueItems.isNotEmpty()) {
                Log.d("ProDocSync", "Proses sinkron untuk ${queueItems.size} item...")
                for (item in queueItems) {
                    try {
                        val collectionRef = firestore.collection(item.tableName)
                        when (item.operation) {
                            "INSERT", "UPDATE" -> {
                                when (item.tableName) {
                                    "projects" -> {
                                        val project = database.projectDao().getProjectById(item.recordId)
                                        project?.let { collectionRef.document(it.projectId).set(it).await() }
                                    }
                                    "materials" -> {
                                        val material = database.materialDao().getMaterialById(item.recordId)
                                        material?.let { collectionRef.document(it.materialId).set(it).await() }
                                    }
                                    "logics" -> {
                                        val logic = database.logicDao().getLogicById(item.recordId)
                                        logic?.let { collectionRef.document(it.logicId).set(it).await() }
                                    }
                                    "diagrams" -> {
                                        val diagram = database.diagramDao().getDiagramById(item.recordId)
                                        diagram?.let { collectionRef.document(it.diagramId).set(it).await() }
                                    }
                                }
                            }
                            "DELETE" -> {
                                collectionRef.document(item.recordId).delete().await()
                            }
                        }
                        syncQueueDao.removeByRecord(item.recordId, item.tableName)
                    } catch (e: Exception) {
                        Log.e("ProDocSync", "Gagal push item ${item.recordId}: ${e.localizedMessage}")
                    }
                }
            }

            Log.d("ProDocSync", "Mulai sinkron dari Cloud Firestore...")

            val remoteProjects = firestore.collection("projects").get().await()
            for (doc in remoteProjects.documents) {
                val statusStr = doc.getString("status") ?: "DRAFT"
                val project = ProjectEntity(
                    projectId = doc.id,
                    name = doc.getString("name") ?: "",
                    category = doc.getString("category") ?: "",
                    description = doc.getString("description") ?: "",
                    status = try { ProjectStatus.valueOf(statusStr) } catch (_: Exception) { ProjectStatus.DRAFT },
                    parentProjectId = doc.getString("parentProjectId"),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                    updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                )
                database.projectDao().insertProject(project)
            }

            val remoteMaterials = firestore.collection("materials").get().await()
            for (doc in remoteMaterials.documents) {
                val qaStr = doc.getString("qaStatus") ?: "DRAFT"

                val rawQuantity = doc.getDouble("quantity")
                val rawPrice = doc.getDouble("price")

                val isOldSchema = rawPrice != null && rawQuantity == null

                val finalQuantity = if (isOldSchema) 1.0 else (rawQuantity ?: 0.0)
                val finalUnitPrice = if (isOldSchema) rawPrice else (doc.getDouble("unitPrice") ?: 0.0)
                val unitStr = if (isOldSchema) "PCS" else (doc.getString("unit") ?: "PCS")

                val material = MaterialEntity(
                    materialId = doc.id,
                    projectId = doc.getString("projectId") ?: "",
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    quantity = finalQuantity,
                    unit = try { MaterialUnit.valueOf(unitStr) } catch (_: Exception) { MaterialUnit.OTHER },
                    unitPrice = finalUnitPrice,
                    photoUrl = doc.getString("photoUrl"),
                    qaStatus = try { QAStatus.valueOf(qaStr) } catch (_: Exception) { QAStatus.DRAFT },
                    rejectionReason = doc.getString("rejectionReason")
                )
                database.materialDao().insertMaterial(material)
            }

            val remoteLogics = firestore.collection("logics").get().await()
            for (doc in remoteLogics.documents) {
                val qaStr = doc.getString("qaStatus") ?: "DRAFT"
                val logic = LogicEntity(
                    logicId = doc.id,
                    projectId = doc.getString("projectId") ?: "",
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    configText = doc.getString("configText") ?: "",
                    qaStatus = try { QAStatus.valueOf(qaStr) } catch (_: Exception) { QAStatus.DRAFT },
                    rejectionReason = doc.getString("rejectionReason")
                )
                database.logicDao().insertLogic(logic)
            }

            val remoteDiagrams = firestore.collection("diagrams").get().await()
            for (doc in remoteDiagrams.documents) {
                val qaStr = doc.getString("qaStatus") ?: "DRAFT"
                val diagram = DiagramEntity(
                    diagramId = doc.id,
                    projectId = doc.getString("projectId") ?: "",
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    photoUrl = doc.getString("photoUrl") ?: "",
                    pdfFilePath = doc.getString("pdfFilePath"),
                    drawioFilePath = doc.getString("drawioFilePath"),
                    qaStatus = try { QAStatus.valueOf(qaStr) } catch (_: Exception) { QAStatus.DRAFT },
                    rejectionReason = doc.getString("rejectionReason")
                )
                database.diagramDao().insertDiagram(diagram)
            }

            Log.d("ProDocSync", "Sinkronisasi berhasil!")
            return Result.success()

        } catch (e: Exception) {
            Log.e("ProDocSync", "Tidak ada koneksi, sinkronisasi ditunda: ${e.localizedMessage}")
            return Result.retry()
        }
    }
}