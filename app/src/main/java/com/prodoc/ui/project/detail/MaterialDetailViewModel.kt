package com.prodoc.ui.project.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.entity.MaterialEntity
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaterialDetailViewModel(
    private val database: ProDocDatabase,
    private val materialId: String
) : ViewModel() {

    private val _material = MutableStateFlow<MaterialEntity?>(null)
    val material: StateFlow<MaterialEntity?> = _material

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMaterialDetail()
    }

    fun loadMaterialDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = database.materialDao().getMaterialById(materialId)
                _material.value = result
            } catch (e: Exception) {
                Log.e("MaterialDetailVM", "Gagal memuat data material: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class MaterialDetailViewModelFactory(
    @Suppress("UNUSED_PARAMETER") repository: ProjectRepository,
    private val database: ProDocDatabase,
    private val materialId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaterialDetailViewModel::class.java)) {
            return MaterialDetailViewModel(database, materialId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}