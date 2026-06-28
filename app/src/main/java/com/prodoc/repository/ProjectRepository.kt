package com.prodoc.repository

import com.prodoc.data.local.entity.*
import com.prodoc.domain.hierarchy.HierarchySummary
import com.prodoc.model.ProjectStatus
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {

    fun getAllMainProjects(): Flow<List<ProjectEntity>>
    fun getSubProjectsByParent(parentId: String): Flow<List<ProjectEntity>>
    suspend fun getProjectById(projectId: String): ProjectEntity?
    suspend fun insertProject(project: ProjectEntity)
    suspend fun updateProject(project: ProjectEntity)
    suspend fun deleteProject(project: ProjectEntity)
    suspend fun updateProjectStatus(projectId: String, newStatus: ProjectStatus)

    fun getMaterialsByProject(projectId: String): Flow<List<MaterialEntity>>
    suspend fun insertMaterial(material: MaterialEntity)
    suspend fun updateMaterial(material: MaterialEntity)
    suspend fun deleteMaterial(material: MaterialEntity)

    fun getLogicsByProject(projectId: String): Flow<List<LogicEntity>>
    suspend fun insertLogic(logic: LogicEntity)
    suspend fun updateLogic(logic: LogicEntity)
    suspend fun deleteLogic(logic: LogicEntity)

    fun getDiagramsByProject(projectId: String): Flow<List<DiagramEntity>>
    suspend fun insertDiagram(diagram: DiagramEntity)
    suspend fun updateDiagram(diagram: DiagramEntity)
    suspend fun deleteDiagram(diagram: DiagramEntity)

    fun getHistoryByProject(projectId: String): Flow<List<HistoryEntity>>
    suspend fun insertHistory(history: HistoryEntity)

    fun getProjectFlowById(projectId: String): Flow<ProjectEntity?>

    fun getAllProjectsRaw(): Flow<List<ProjectEntity>>
    fun getAllMaterialsRaw(): Flow<List<MaterialEntity>>
    fun getAllLogicsRaw(): Flow<List<LogicEntity>>
    fun getAllDiagramsRaw(): Flow<List<DiagramEntity>>

    fun getProjectSummary(targetProjectId: String): Flow<HierarchySummary>

    suspend fun getMaterialById(materialId: String): MaterialEntity?
    suspend fun getLogicById(logicId: String): LogicEntity?
    suspend fun getDiagramById(diagramId: String): DiagramEntity?

    fun getLocalUser(): Flow<UserEntity?>
    suspend fun saveUser(user: UserEntity)
    suspend fun clearUser()
    fun isUserLoggedIn(): Boolean
    suspend fun registerWithEmail(name: String, email: String, password: String): UserEntity
    suspend fun loginWithEmail(email: String, password: String): UserEntity

    suspend fun logout()
}