package com.prodoc.ui.project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.entity.*
import com.prodoc.model.ProjectStatus
import com.prodoc.model.QAStatus
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectDetailViewModel(
    private val repository: ProjectRepository,
    private val projectId: String,
    private val database: ProDocDatabase
) : ViewModel() {

    private val _currentTab = MutableStateFlow(ProjectTab.DETAIL)
    private val _isLoading = MutableStateFlow(false)

    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProjectDetailUiState> =
        combine(
            repository.getProjectFlowById(projectId),
            repository.getSubProjectsByParent(projectId),
            repository.getMaterialsByProject(projectId),
            repository.getLogicsByProject(projectId),
            repository.getDiagramsByProject(projectId),
            repository.getHistoryByProject(projectId),
            _currentTab,
            _isLoading
        ) { values ->
            ProjectDetailUiState(
                project = values[0] as ProjectEntity?,
                subProjects = values[1] as List<ProjectEntity>,
                materials = values[2] as List<MaterialEntity>,
                logics = values[3] as List<LogicEntity>,
                diagrams = values[4] as List<DiagramEntity>,
                historyLogs = values[5] as List<HistoryEntity>,
                currentTab = values[6] as ProjectTab,
                isLoading = values[7] as Boolean
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ProjectDetailUiState(isLoading = true)
            )

    fun onTabSelected(tab: ProjectTab) {
        _currentTab.value = tab
    }

    fun editSubProject(subProject: ProjectEntity, name: String, category: String, description: String) {
        viewModelScope.launch {
            try {
                val updatedSub = subProject.copy(
                    name = name,
                    category = category,
                    description = description,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateProject(updatedSub)

                repository.insertHistory(HistoryEntity(
                    historyId = UUID.randomUUID().toString(),
                    projectId = projectId,
                    actionDescription = "Sub-Project '${subProject.name}' telah di ubah '$name'",
                    timestamp = System.currentTimeMillis()
                ))
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal memperbarui sub-project: ${e.localizedMessage}")
            }
        }
    }

    fun deleteSubProject(subProject: ProjectEntity) {
        viewModelScope.launch {
            try {
                repository.deleteProject(subProject)

                repository.insertHistory(HistoryEntity(
                    historyId = UUID.randomUUID().toString(),
                    projectId = projectId,
                    actionDescription = "Sub-Project '${subProject.name}' dihapus dari sistem",
                    timestamp = System.currentTimeMillis()
                ))
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal menghapus sub-project: ${e.localizedMessage}")
            }
        }
    }

    fun addSubProject(name: String, category: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val subProject = ProjectEntity(
                    projectId = UUID.randomUUID().toString(),
                    parentProjectId = projectId,
                    name = name,
                    category = category,
                    description = description,
                    status = ProjectStatus.DRAFT,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                repository.insertProject(subProject)

                repository.insertHistory(HistoryEntity(
                    historyId = UUID.randomUUID().toString(),
                    projectId = projectId,
                    actionDescription = "Sub-Project '$name' ditambahkan ke dalam project ini",
                    timestamp = System.currentTimeMillis()
                ))
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal menambahkan sub-project: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMaterialToProject(name: String, description: String, price: Double) {
        viewModelScope.launch {
            try {
                val newMaterial = MaterialEntity(
                    materialId = UUID.randomUUID().toString(),
                    projectId = projectId,
                    name = name,
                    description = description,
                    price = price,
                    qaStatus = QAStatus.DRAFT
                )
                repository.insertMaterial(newMaterial)
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal menambahkan material: ${e.localizedMessage}", e)
            }
        }
    }

    fun editMaterial(material: MaterialEntity, newName: String, newDesc: String, newPrice: Double) {
        viewModelScope.launch {
            val updatedMaterial = material.copy(
                name = newName,
                description = newDesc,
                price = newPrice,
                qaStatus = QAStatus.DRAFT,
                rejectionReason = null
            )
            repository.updateMaterial(updatedMaterial)
            repository.insertHistory(HistoryEntity(
                historyId = UUID.randomUUID().toString(),
                projectId = projectId,
                actionDescription = "Material '${material.name}' diubah (Status reset ke DRAFT)",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun deleteMaterial(material: MaterialEntity) {
        viewModelScope.launch {
            repository.deleteMaterial(material)
            repository.insertHistory(HistoryEntity(
                historyId = UUID.randomUUID().toString(),
                projectId = projectId,
                actionDescription = "Material '${material.name}' dihapus",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun addLogicToProject(name: String, description: String, configText: String) {
        viewModelScope.launch {
            try {
                val newLogic = LogicEntity(
                    logicId = UUID.randomUUID().toString(),
                    projectId = projectId,
                    name = name,
                    description = description,
                    configText = configText,
                    qaStatus = QAStatus.DRAFT
                )
                repository.insertLogic(newLogic)
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal menambahkan logic script: ${e.localizedMessage}", e)
            }
        }
    }

    fun editLogic(logic: LogicEntity, newName: String, newDesc: String, newConfig: String) {
        viewModelScope.launch {
            val updatedLogic = logic.copy(
                name = newName,
                description = newDesc,
                configText = newConfig,
                qaStatus = QAStatus.DRAFT,
                rejectionReason = null
            )
            repository.updateLogic(updatedLogic)
            repository.insertHistory(HistoryEntity(
                historyId = UUID.randomUUID().toString(),
                projectId = projectId,
                actionDescription = "Logic '${logic.name}' diubah (Status reset ke DRAFT)",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun deleteLogic(logic: LogicEntity) {
        viewModelScope.launch {
            repository.deleteLogic(logic)
            repository.insertHistory(HistoryEntity(
                historyId = UUID.randomUUID().toString(),
                projectId = projectId,
                actionDescription = "Logic '${logic.name}' dihapus",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun addDiagramToProject(name: String, description: String, photoUrl: String, pdfPath: String?, drawioPath: String?) {
        viewModelScope.launch {
            try {
                val newDiagram = DiagramEntity(
                    diagramId = UUID.randomUUID().toString(),
                    projectId = projectId,
                    name = name,
                    description = description,
                    photoUrl = photoUrl,
                    pdfFilePath = pdfPath,
                    drawioFilePath = drawioPath,
                    qaStatus = QAStatus.DRAFT,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertDiagram(newDiagram)
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal menambahkan diagram: ${e.localizedMessage}", e)
            }
        }
    }

    fun editDiagram(diagram: DiagramEntity, newName: String, newDesc: String, newPhoto: String, newPdf: String?, newDrawio: String?) {
        viewModelScope.launch {
            val updatedDiagram = diagram.copy(
                name = newName,
                description = newDesc,
                photoUrl = newPhoto,
                pdfFilePath = newPdf,
                drawioFilePath = newDrawio,
                qaStatus = QAStatus.DRAFT,
                rejectionReason = null
            )
            repository.updateDiagram(updatedDiagram)
            repository.insertHistory(HistoryEntity(
                historyId = UUID.randomUUID().toString(),
                projectId = projectId,
                actionDescription = "Diagram '${diagram.name}' diubah (Status reset ke DRAFT)",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun deleteDiagram(diagram: DiagramEntity) {
        viewModelScope.launch {
            repository.deleteDiagram(diagram)
            repository.insertHistory(HistoryEntity(
                historyId = UUID.randomUUID().toString(),
                projectId = projectId,
                actionDescription = "Diagram '${diagram.name}' dihapus",
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    fun updateMaterialQaStatus(material: MaterialEntity, newStatus: QAStatus, reason: String? = null) {
        viewModelScope.launch {
            val updated = material.copy(qaStatus = newStatus, rejectionReason = reason)
            repository.updateMaterial(updated)
        }
    }

    fun updateLogicQaStatus(logic: LogicEntity, newStatus: QAStatus, reason: String? = null) {
        viewModelScope.launch {
            val updated = logic.copy(qaStatus = newStatus, rejectionReason = reason)
            repository.updateLogic(updated)
        }
    }

    fun updateDiagramQaStatus(diagram: DiagramEntity, newStatus: QAStatus, reason: String? = null) {
        viewModelScope.launch {
            val updated = diagram.copy(qaStatus = newStatus, rejectionReason = reason)
            repository.updateDiagram(updated)
        }
    }

    fun deleteCurrentProject(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val currentProject = uiState.value.project
                if (currentProject != null) {
                    val deleteSyncAction = SyncQueueEntity(
                        recordId = projectId,
                        tableName = "projects",
                        operation = "DELETE",
                        timestamp = System.currentTimeMillis()
                    )
                    database.syncQueueDao().enqueue(deleteSyncAction)
                    repository.deleteProject(currentProject)
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("ProjectDetailVM", "Gagal menghapus project: ${e.localizedMessage}", e)
            }
        }
    }
}

class ProjectDetailViewModelFactory(
    private val repository: ProjectRepository,
    private val projectId: String,
    private val database: ProDocDatabase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailViewModel::class.java)) {
            return ProjectDetailViewModel(repository, projectId, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}