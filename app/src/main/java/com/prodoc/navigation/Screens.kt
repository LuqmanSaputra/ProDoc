package com.prodoc.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable object Auth : Screens
    @Serializable object Dashboard : Screens
    @Serializable data class ProjectDetail(val projectId: String) : Screens
    @Serializable data class MaterialDetail(val materialId: String) : Screens
    @Serializable data class LogicDetail(val logicId: String) : Screens
    @Serializable data class DiagramDetail(val diagramId: String) : Screens
}