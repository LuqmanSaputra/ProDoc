package com.prodoc.ui.project.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prodoc.data.local.ProDocDatabase
import com.prodoc.data.local.entity.LogicEntity
import com.prodoc.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LogicDetailViewModel(
    private val repository: ProjectRepository,
    private val logicId: String
) : ViewModel() {

    private val _logic = MutableStateFlow<LogicEntity?>(null)
    val logic: StateFlow<LogicEntity?> = _logic

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadLogicDetail()
    }

    fun loadLogicDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLogicById(logicId)
                _logic.value = result
            } catch (e: Exception) {
                Log.e("LogicDetailVM", "Gagal memuat data logic: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class LogicDetailViewModelFactory(
    private val repository: ProjectRepository,
    @Suppress("UNUSED_PARAMETER") private val database: ProDocDatabase,
    private val logicId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogicDetailViewModel::class.java)) {
            return LogicDetailViewModel(repository, logicId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}