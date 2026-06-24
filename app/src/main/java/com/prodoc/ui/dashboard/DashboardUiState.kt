package com.prodoc.ui.dashboard

import com.prodoc.model.ProjectStatus

data class DashboardUiState(
    val projects: List<DashboardProjectItem> = emptyList(),
    val searchQuery: String = "",
    val selectedStatusFilter: ProjectStatus? = null,
    val unSyncedCount: Int = 0,
    val isLoading: Boolean = false
)