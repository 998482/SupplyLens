package com.supplylens.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supplylens.app.data.model.*
import com.supplylens.app.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val kpis: KpiData? = null,
    val shipments: List<Shipment> = emptyList(),
    val alerts: List<AlertItem> = emptyList(),
    val scenarios: List<Scenario> = emptyList(),
    val activeScenarioIds: Set<String> = emptySet(),
    val affectedShipmentIds: Set<String> = emptySet(),
    val affectedNodeIds: Set<String> = emptySet(),
    val simulatedKpis: KpiData? = null,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: SupplyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val data = repository.loadDashboard()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        kpis      = data.kpis,
                        alerts    = data.alerts,
                        shipments = data.shipments,
                        scenarios = data.scenarios
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun toggleScenario(scenarioId: String) {
        val current = _uiState.value.activeScenarioIds.toMutableSet()
        if (scenarioId in current) {
            current.remove(scenarioId)
        } else {
            current.add(scenarioId)
        }

        _uiState.update { it.copy(activeScenarioIds = current) }

        if (current.isEmpty()) {
            _uiState.update {
                it.copy(
                    simulatedKpis      = null,
                    affectedShipmentIds = emptySet(),
                    affectedNodeIds    = emptySet()
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.simulate(current.toList())
                _uiState.update {
                    it.copy(
                        simulatedKpis       = result.projectedKpis,
                        affectedShipmentIds = result.impactedShipmentIds.toSet(),
                        affectedNodeIds     = result.affectedNodes.toSet()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Simulation failed")
                }
            }
        }
    }
}