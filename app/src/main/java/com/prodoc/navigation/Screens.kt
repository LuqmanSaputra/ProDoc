package com.prodoc.navigation

import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable data object Auth : Screens()
    @Serializable data object Dashboard : Screens()
    @Serializable data class ProjectDetail(val projectId: String) : Screens()
}
