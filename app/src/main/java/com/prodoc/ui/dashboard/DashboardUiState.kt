package com.prodoc.ui.dashboard

import com.prodoc.data.local.entity.ProjectEntity
import com.prodoc.model.ProjectStatus

data class DashboardUiState(
    val projects: List<ProjectEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedStatusFilter: ProjectStatus? = null,
    val unSyncedCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)