package com.prodoc.domain.hierarchy

import com.prodoc.data.local.entity.ProjectEntity
import com.prodoc.data.local.entity.MaterialEntity
import com.prodoc.data.local.entity.LogicEntity
import com.prodoc.data.local.entity.DiagramEntity
import java.util.ArrayDeque

class HierarchyService {

    fun validateHierarchy(
        allProjects: List<ProjectEntity>,
        targetProjectId: String,
        newParentProjectId: String?
    ): HierarchyValidationResult {
        if (newParentProjectId != null && targetProjectId == newParentProjectId) {
            return HierarchyValidationResult(
                isValid = false,
                errorMessage = "Project dan Project Utama tidak boleh sama."
            )
        }

        if (newParentProjectId == null) {
            return HierarchyValidationResult(isValid = true, errorMessage = null)
        }

        val projectMap = allProjects.associateBy { it.projectId }

        if (!projectMap.containsKey(newParentProjectId)) {
            return HierarchyValidationResult(
                isValid = false,
                errorMessage = "Tidak dapat memilih project ini sebagai Project Utama."
            )
        }

        var currentId = newParentProjectId
        val visitedIds = mutableSetOf<String>()

        while (currentId != null) {
            if (!visitedIds.add(currentId)) {
                return HierarchyValidationResult(
                    isValid = false,
                    errorMessage = "Data project tidak lengkap karena Project Utama tidak ditemukan."
                )
            }

            if (currentId == targetProjectId) {
                return HierarchyValidationResult(
                    isValid = false,
                    errorMessage = "Project yang dipilih merupakan Subproject dari project ini, sehingga tidak dapat dijadikan Project Utama."
                )
            }

            val currentProject = projectMap[currentId] ?: return HierarchyValidationResult(
                isValid = false,
                errorMessage = "Project Utama tidak ditemukan."
            )
            currentId = currentProject.parentProjectId
        }

        return HierarchyValidationResult(isValid = true, errorMessage = null)
    }

    fun buildCascadeDeletePlan(
        targetProjectId: String,
        allProjects: List<ProjectEntity>,
        allMaterials: List<MaterialEntity>,
        allLogics: List<LogicEntity>,
        allDiagrams: List<DiagramEntity>
    ): CascadeDeletePlan {
        val affectedProjectIds = getHierarchyProjectIds(targetProjectId, allProjects)

        val projectIdsToDelete = allProjects
            .filter { it.projectId in affectedProjectIds }
            .map { it.projectId }

        val materialIdsToDelete = allMaterials
            .filter { it.projectId in affectedProjectIds }
            .map { it.materialId }

        val logicIdsToDelete = allLogics
            .filter { it.projectId in affectedProjectIds }
            .map { it.logicId }

        val diagramIdsToDelete = allDiagrams
            .filter { it.projectId in affectedProjectIds }
            .map { it.diagramId }

        return CascadeDeletePlan(
            projectIdsToDelete = projectIdsToDelete,
            materialIdsToDelete = materialIdsToDelete,
            logicIdsToDelete = logicIdsToDelete,
            diagramIdsToDelete = diagramIdsToDelete
        )
    }

    fun calculateProjectSummary(
        targetProjectId: String,
        allProjects: List<ProjectEntity>,
        allMaterials: List<MaterialEntity>,
        allLogics: List<LogicEntity>,
        allDiagrams: List<DiagramEntity>
    ): HierarchySummary {
        val affectedProjectIds = getHierarchyProjectIds(targetProjectId, allProjects)

        val totalSubProjects = (affectedProjectIds.size - 1).coerceAtLeast(0)

        var totalMaterials = 0
        var totalLogics = 0
        var totalDiagrams = 0
        var totalMaterialCost = 0.0

        allMaterials.forEach { material ->
            if (material.projectId in affectedProjectIds) {
                totalMaterials++
                totalMaterialCost += material.totalPrice
            }
        }

        allLogics.forEach { logic ->
            if (logic.projectId in affectedProjectIds) {
                totalLogics++
            }
        }

        allDiagrams.forEach { diagram ->
            if (diagram.projectId in affectedProjectIds) {
                totalDiagrams++
            }
        }

        return HierarchySummary(
            totalSubProjects = totalSubProjects,
            totalMaterials = totalMaterials,
            totalLogics = totalLogics,
            totalDiagrams = totalDiagrams,
            totalMaterialCost = totalMaterialCost
        )
    }

    private fun getHierarchyProjectIds(
        rootProjectId: String,
        allProjects: List<ProjectEntity>
    ): Set<String> {
        val parentToChildrenMap = allProjects
            .filter { it.parentProjectId != null }
            .groupBy { it.parentProjectId!! }

        val resultSet = mutableSetOf<String>()
        val stack = ArrayDeque<String>()

        stack.push(rootProjectId)

        while (!stack.isEmpty()) {
            val currentId = stack.pop()
            if (resultSet.add(currentId)) {
                val children = parentToChildrenMap[currentId]
                children?.forEach { child ->
                    stack.push(child.projectId)
                }
            }
        }

        return resultSet
    }
}