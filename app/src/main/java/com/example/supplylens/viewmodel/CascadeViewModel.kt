
package com.supplylens.app.viewmodel

import androidx.lifecycle.ViewModel
import com.supplylens.app.data.model.CascadeNode
import com.supplylens.app.data.repository.SupplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CascadeMapViewModel @Inject constructor(
    private val repository: SupplyRepository
) : ViewModel() {

    private val _nodes = MutableStateFlow(repository.getMockNodes())
    val nodes = _nodes.asStateFlow()

    fun applyAffected(affectedIds: Set<String>) {
        _nodes.value = repository.getMockNodes().map { node ->
            node.copy(isAffected = node.id in affectedIds)
        }
    }
}