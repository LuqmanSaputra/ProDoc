package com.prodoc.repository

import com.prodoc.data.local.dao.*
import com.prodoc.data.local.entity.*
import com.prodoc.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao,
    private val materialDao: MaterialDao,
    private val logicDao: LogicDao,
    private val diagramDao: DiagramDao,
    private val historyDao: HistoryDao,
    private val syncQueueDao: SyncQueueDao
) : ProjectRepository {

    private suspend fun logHistoryAutomated(projectId: String, message: String) {
        val history = HistoryEntity(
            historyId = UUID.randomUUID().toString(),
            projectId = projectId,
            actionDescription = message,
            timestamp = System.currentTimeMillis()
        )
        historyDao.insertHistory(history)
    }

    private suspend fun enqueueSyncAutomated(tableName: String, recordId: String, operation: String) {
        val syncItem = SyncQueueEntity(
            tableName = tableName,
            recordId = recordId,
            operation = operation,
            timestamp = System.currentTimeMillis()
        )
        syncQueueDao.enqueue(syncItem)
    }

    override fun getAllMainProjects(): Flow<List<ProjectEntity>> =
        projectDao.getAllMainProjects()

    override fun getSubProjectsByParent(parentId: String): Flow<List<ProjectEntity>> =
        projectDao.getSubProjectsByParent(parentId)

    override suspend fun getProjectById(projectId: String): ProjectEntity? =
        projectDao.getProjectById(projectId)

    override suspend fun insertProject(project: ProjectEntity) {
        projectDao.insertProject(project)
        val logMsg = if (project.parentProjectId == null) "Project utama '${project.name}' dibuat" else "Sub project '${project.name}' ditambahkan"
        logHistoryAutomated(project.projectId, logMsg)
        enqueueSyncAutomated("projects", project.projectId, "INSERT")
    }

    override suspend fun updateProject(project: ProjectEntity) {
        projectDao.insertProject(project.copy(isSynced = false))
        logHistoryAutomated(project.projectId, "Informasi project '${project.name}' diubah")
        enqueueSyncAutomated("projects", project.projectId, "UPDATE")
    }

    override suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
        enqueueSyncAutomated("projects", project.projectId, "DELETE")
    }

    override suspend fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        val project = projectDao.getProjectById(projectId)
        if (project != null) {
            val updated = project.copy(status = newStatus, updatedAt = System.currentTimeMillis(), isSynced = false)
            projectDao.insertProject(updated)
            logHistoryAutomated(projectId, "Status project diubah menjadi ${newStatus.name}")
            enqueueSyncAutomated("projects", projectId, "UPDATE")
        }
    }

    override fun getMaterialsByProject(projectId: String): Flow<List<MaterialEntity>> =
        materialDao.getMaterialsByProject(projectId)

    override suspend fun insertMaterial(material: MaterialEntity) {
        materialDao.insertMaterial(material)
        logHistoryAutomated(material.projectId, "Material '${material.name}' ditambahkan")
        enqueueSyncAutomated("materials", material.materialId, "INSERT")
    }

    override suspend fun updateMaterial(material: MaterialEntity) {
        materialDao.updateMaterial(material)
        logHistoryAutomated(material.projectId, "Material '${material.name}' diubah")
        enqueueSyncAutomated("materials", material.materialId, "UPDATE")
    }

    override suspend fun deleteMaterial(material: MaterialEntity) {
        materialDao.deleteMaterial(material)
        logHistoryAutomated(material.projectId, "Material '${material.name}' dihapus")
        enqueueSyncAutomated("materials", material.materialId, "DELETE")
    }

    override fun getLogicsByProject(projectId: String): Flow<List<LogicEntity>> =
        logicDao.getLogicsByProject(projectId)

    override suspend fun insertLogic(logic: LogicEntity) {
        logicDao.insertLogic(logic)
        logHistoryAutomated(logic.projectId, "Logic '${logic.name}' ditambahkan")
        enqueueSyncAutomated("logics", logic.logicId, "INSERT")
    }

    override suspend fun updateLogic(logic: LogicEntity) {
        logicDao.updateLogic(logic)
        logHistoryAutomated(logic.projectId, "Logic '${logic.name}' diubah")
        enqueueSyncAutomated("logics", logic.logicId, "UPDATE")
    }

    override suspend fun deleteLogic(logic: LogicEntity) {
        logicDao.deleteLogic(logic)
        logHistoryAutomated(logic.projectId, "Logic '${logic.name}' dihapus")
        enqueueSyncAutomated("logics", logic.logicId, "DELETE")
    }

    override fun getDiagramsByProject(projectId: String): Flow<List<DiagramEntity>> =
        diagramDao.getDiagramsByProject(projectId)

    override suspend fun insertDiagram(diagram: DiagramEntity) {
        diagramDao.insertDiagram(diagram)
        logHistoryAutomated(diagram.projectId, "Diagram '${diagram.name}' ditambahkan")
        enqueueSyncAutomated("diagrams", diagram.diagramId, "INSERT")
    }

    override suspend fun updateDiagram(diagram: DiagramEntity) {
        diagramDao.updateDiagram(diagram)
        logHistoryAutomated(diagram.projectId, "Diagram '${diagram.name}' diubah")
        enqueueSyncAutomated("diagrams", diagram.diagramId, "UPDATE")
    }

    override suspend fun deleteDiagram(diagram: DiagramEntity) {
        diagramDao.deleteDiagram(diagram)
        logHistoryAutomated(diagram.projectId, "Diagram '${diagram.name}' dihapus")
        enqueueSyncAutomated("diagrams", diagram.diagramId, "DELETE")
    }

    override fun getHistoryByProject(projectId: String): Flow<List<HistoryEntity>> =
        historyDao.getHistoryByProject(projectId)

    override suspend fun insertHistory(history: HistoryEntity) {
        historyDao.insertHistory(history)
    }

    override fun getProjectFlowById(projectId: String): Flow<ProjectEntity?> =
        projectDao.getProjectFlowById(projectId)
}