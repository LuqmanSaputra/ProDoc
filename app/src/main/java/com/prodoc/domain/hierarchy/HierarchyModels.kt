package com.prodoc.domain.hierarchy

data class HierarchyValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

data class CascadeDeletePlan(
    val projectIdsToDelete: List<String>,
    val materialIdsToDelete: List<String>,
    val logicIdsToDelete: List<String>,
    val diagramIdsToDelete: List<String>
)

data class HierarchySummary(
    val totalSubProjects: Int,
    val totalMaterials: Int,
    val totalLogics: Int,
    val totalDiagrams: Int,
    val totalMaterialCost: Double
)
