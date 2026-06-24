package com.prodoc.ui.project.detail

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.entity.DiagramEntity
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiagramDetailViewModel(
    private val database: ProDocDatabase,
    private val diagramId: String
) : ViewModel() {

    private val _diagram = MutableStateFlow<DiagramEntity?>(null)
    val diagram: StateFlow<DiagramEntity?> = _diagram

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadDiagramDetail()
    }

    fun loadDiagramDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = database.diagramDao().getDiagramById(diagramId)
                _diagram.value = result
            } catch (e: Exception) {
                Log.e("DiagramDetailVM", "Gagal memuat data diagram: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class DiagramDetailViewModelFactory(
    @Suppress("UNUSED_PARAMETER") repository: ProjectRepository,
    private val database: ProDocDatabase,
    private val diagramId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiagramDetailViewModel::class.java)) {
            return DiagramDetailViewModel(database, diagramId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}