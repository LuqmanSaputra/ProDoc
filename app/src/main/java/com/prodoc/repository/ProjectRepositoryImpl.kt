package com.prodoc.repository

import androidx.room.withTransaction
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.dao.*
import com.prodoc.data.local.entity.*
import com.prodoc.domain.hierarchy.HierarchyService
import com.prodoc.domain.hierarchy.HierarchySummary
import com.prodoc.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.UUID

class ProjectRepositoryImpl(
    private val database: ProDocDatabase,
    private val projectDao: ProjectDao,
    private val materialDao: MaterialDao,
    private val logicDao: LogicDao,
    private val diagramDao: DiagramDao,
    private val historyDao: HistoryDao,
    private val syncQueueDao: SyncQueueDao,
    private val hierarchyService: HierarchyService
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
        val allProjects = projectDao.getAllProjectsFlow().first()
        val validation = hierarchyService.validateHierarchy(
            allProjects = allProjects,
            targetProjectId = project.projectId,
            newParentProjectId = project.parentProjectId
        )
        if (!validation.isValid) {
            throw IllegalArgumentException(validation.errorMessage ?: "Validasi hierarki gagal.")
        }

        projectDao.insertProject(project)
        val logMsg = if (project.parentProjectId == null) "Project utama '${project.name}' dibuat" else "Sub project '${project.name}' ditambahkan"
        logHistoryAutomated(project.projectId, logMsg)
        enqueueSyncAutomated("projects", project.projectId, "INSERT")
    }

    override suspend fun updateProject(project: ProjectEntity) {
        val allProjects = projectDao.getAllProjectsFlow().first()
        val validation = hierarchyService.validateHierarchy(
            allProjects = allProjects,
            targetProjectId = project.projectId,
            newParentProjectId = project.parentProjectId
        )
        if (!validation.isValid) {
            throw IllegalArgumentException(validation.errorMessage ?: "Validasi hierarki gagal.")
        }

        projectDao.insertProject(project.copy(isSynced = false))
        logHistoryAutomated(project.projectId, "Informasi project '${project.name}' diubah")
        enqueueSyncAutomated("projects", project.projectId, "UPDATE")
    }

    override suspend fun deleteProject(project: ProjectEntity) {
        val allProjects = projectDao.getAllProjectsFlow().first()
        val allMaterials = materialDao.getAllMaterialsFlow().first()
        val allLogics = logicDao.getAllLogicsFlow().first()
        val allDiagrams = diagramDao.getAllDiagramsFlow().first()

        val deletePlan = hierarchyService.buildCascadeDeletePlan(
            targetProjectId = project.projectId,
            allProjects = allProjects,
            allMaterials = allMaterials,
            allLogics = allLogics,
            allDiagrams = allDiagrams
        )

        val projectMap = allProjects.associateBy { it.projectId }
        val materialMap = allMaterials.associateBy { it.materialId }
        val logicMap = allLogics.associateBy { it.logicId }
        val diagramMap = allDiagrams.associateBy { it.diagramId }

        database.withTransaction {
            deletePlan.materialIdsToDelete.forEach { id ->
                materialMap[id]?.let { mat ->
                    materialDao.deleteMaterial(mat)
                    logHistoryAutomated(mat.projectId, "Material '${mat.name}' dihapus otomatis via cascade")
                    enqueueSyncAutomated("materials", mat.materialId, "DELETE")
                }
            }

            deletePlan.logicIdsToDelete.forEach { id ->
                logicMap[id]?.let { log ->
                    logicDao.deleteLogic(log)
                    logHistoryAutomated(log.projectId, "Logic '${log.name}' diaktifkan untuk hapus via cascade")
                    enqueueSyncAutomated("logics", log.logicId, "DELETE")
                }
            }

            deletePlan.diagramIdsToDelete.forEach { id ->
                diagramMap[id]?.let { diag ->
                    diagramDao.deleteDiagram(diag)
                    logHistoryAutomated(diag.projectId, "Diagram '${diag.name}' dibersihkan via cascade")
                    enqueueSyncAutomated("diagrams", diag.diagramId, "DELETE")
                }
            }

            deletePlan.projectIdsToDelete.forEach { id ->
                projectMap[id]?.let { proj ->
                    projectDao.deleteProject(proj)
                    if (proj.projectId == project.projectId) {
                        enqueueSyncAutomated("projects", proj.projectId, "DELETE")
                    } else {
                        logHistoryAutomated(project.projectId, "Sub-proyek '${proj.name}' ikut musnah via kaskade")
                        enqueueSyncAutomated("projects", proj.projectId, "DELETE")
                    }
                }
            }
        }
    }

    override suspend fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        val project = projectDao.getProjectById(projectId) ?: return
        val updated = project.copy(status = newStatus, updatedAt = System.currentTimeMillis(), isSynced = false)
        projectDao.insertProject(updated)
        logHistoryAutomated(projectId, "Status project '${newStatus.name}' diubah")
        enqueueSyncAutomated("projects", projectId, "UPDATE")
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

    override fun getAllProjectsRaw(): Flow<List<ProjectEntity>> =
        projectDao.getAllProjectsFlow()

    override fun getAllMaterialsRaw(): Flow<List<MaterialEntity>> =
        materialDao.getAllMaterialsFlow()

    override fun getAllLogicsRaw(): Flow<List<LogicEntity>> =
        logicDao.getAllLogicsFlow()

    override fun getAllDiagramsRaw(): Flow<List<DiagramEntity>> =
        diagramDao.getAllDiagramsFlow()

    override fun getProjectSummary(targetProjectId: String): Flow<HierarchySummary> =
        combine(
            projectDao.getAllProjectsFlow(),
            materialDao.getAllMaterialsFlow(),
            logicDao.getAllLogicsFlow(),
            diagramDao.getAllDiagramsFlow()
        ) { allProjects, allMaterials, allLogics, allDiagrams ->
            hierarchyService.calculateProjectSummary(
                targetProjectId = targetProjectId,
                allProjects = allProjects,
                allMaterials = allMaterials,
                allLogics = allLogics,
                allDiagrams = allDiagrams
            )
        }
}