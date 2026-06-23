package com.prodoc.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.entity.ProjectEntity
import com.prodoc.model.ProjectStatus
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class DashboardViewModel(
    private val repository: ProjectRepository,
    database: ProDocDatabase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<ProjectStatus?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.getAllMainProjects(),
        _searchQuery,
        _selectedStatus,
        database.syncQueueDao().getSyncQueueCountFlow(),
        _isLoading
    ) { projectList, query, status, syncCount, loading ->

        val filteredProjects = projectList.filter { project ->
            val matchQuery = project.name.contains(query, ignoreCase = true) ||
                    project.category.contains(query, ignoreCase = true)
            val matchStatus = status == null || project.status == status
            matchQuery && matchStatus
        }

        DashboardUiState(
            projects = filteredProjects,
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
                Log.e("DashboardVM", "Gagal menambahkan proyek: ${e.localizedMessage}", e)
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