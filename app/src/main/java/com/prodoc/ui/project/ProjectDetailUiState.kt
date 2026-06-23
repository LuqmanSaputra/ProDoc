package com.prodoc.ui.project

import com.prodoc.data.local.entity.*

data class ProjectDetailUiState(
    val project: ProjectEntity? = null,
    val subProjects: List<ProjectEntity> = emptyList(),
    val materials: List<MaterialEntity> = emptyList(),
    val logics: List<LogicEntity> = emptyList(),
    val diagrams: List<DiagramEntity> = emptyList(),
    val historyLogs: List<HistoryEntity> = emptyList(),
    val currentTab: ProjectTab = ProjectTab.DETAIL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)