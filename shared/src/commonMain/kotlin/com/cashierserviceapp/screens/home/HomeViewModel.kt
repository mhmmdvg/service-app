package com.cashierserviceapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.Post
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.domain.repositories.PostRepository
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class HomeViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {
    val orderState: StateFlow<Resource<List<Order>>>
        field = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())

    private var fetchJob: Job? = null

    init {
        load()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            orderRepository.getOrders()
                .fold(
                    onSuccess = { response ->
                        orderState.value = Resource.Success(response)
                    },
                    onFailure = { exception ->
                        orderState.value = Resource.Error(exception.message)
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}