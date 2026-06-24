package com.prodoc.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.entity.*
import com.prodoc.model.ProjectStatus
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class DashboardProjectItem(
    val entity: ProjectEntity,
    val subProjectCount: Int,
    val materialCount: Int,
    val logicCount: Int,
    val diagramCount: Int
)

class DashboardViewModel(
    private val repository: ProjectRepository,
    database: ProDocDatabase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<ProjectStatus?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAllProjectsRaw(),
        repository.getAllMaterialsRaw(),
        repository.getAllLogicsRaw(),
        repository.getAllDiagramsRaw(),
        _searchQuery,
        _selectedStatus,
        database.syncQueueDao().getSyncQueueCountFlow(),
        _isLoading
    ) { flowsArray ->
        @Suppress("UNCHECKED_CAST")
        val allProjects = flowsArray[0] as List<ProjectEntity>
        @Suppress("UNCHECKED_CAST")
        val allMaterials = flowsArray[1] as List<MaterialEntity>
        @Suppress("UNCHECKED_CAST")
        val allLogics = flowsArray[2] as List<LogicEntity>
        @Suppress("UNCHECKED_CAST")
        val allDiagrams = flowsArray[3] as List<DiagramEntity>

        val query = flowsArray[4] as String
        val status = flowsArray[5] as ProjectStatus?
        val syncCount = flowsArray[6] as Int
        val loading = flowsArray[7] as Boolean

        val filteredProjects = allProjects.filter { project ->
            val matchQuery = project.name.contains(query, ignoreCase = true) ||
                    project.category.contains(query, ignoreCase = true)

            val matchStatus = status == null || project.status == status

            val matchVisibility = if (query.isBlank()) project.parentProjectId == null else true

            matchQuery && matchStatus && matchVisibility
        }

        val projectsWithCounters = filteredProjects.map { project ->
            DashboardProjectItem(
                entity = project,
                subProjectCount = allProjects.count { it.parentProjectId == project.projectId },
                materialCount = allMaterials.count { it.projectId == project.projectId },
                logicCount = allLogics.count { it.projectId == project.projectId },
                diagramCount = allDiagrams.count { it.projectId == project.projectId }
            )
        }

        DashboardUiState(
            projects = projectsWithCounters,
            searchQuery = query,
            selectedStatusFilter = status,
            unSyncedCount = syncCount,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) { _searchQuery.value = newQuery }
    fun onStatusFilterChanged(status: ProjectStatus?) { _selectedStatus.value = status }

    fun addNewTestProject(name: String, category: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mockProject = ProjectEntity(
                    projectId = UUID.randomUUID().toString(),
                    name = name,
                    category = category,
                    description = description,
                    status = ProjectStatus.DRAFT,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                repository.insertProject(mockProject)
            } catch (e: Exception) {
                Log.e("DashboardVM", "Gagal menambahkan project utama: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class DashboardViewModelFactory(
    private val repository: ProjectRepository,
    private val database: ProDocDatabase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}