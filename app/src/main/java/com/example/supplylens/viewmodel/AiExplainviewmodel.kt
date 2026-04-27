package com.supplylens.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supplylens.app.data.model.KpiData
import com.supplylens.app.data.model.Scenario
import com.supplylens.app.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiExplainViewModel @Inject constructor(
    private val repository: SupplyRepository
) : ViewModel() {

    private val _explanation = MutableStateFlow<String?>(null)
    val explanation = _explanation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedScenarioId = MutableStateFlow<String?>(null)
    val selectedScenarioId = _selectedScenarioId.asStateFlow()

    fun explain(scenario: Scenario, kpis: KpiData) {
        _selectedScenarioId.value = scenario.id
        viewModelScope.launch {
            _isLoading.value = true
            _explanation.value = null
            try {
                _explanation.value = repository.getGeminiExplanation(scenario, kpis)
            } catch (e: Exception) {
                _explanation.value = "Error generating explanation: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}