package com.cashierserviceapp.screens.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val detailState: StateFlow<Resource<OrderDetailUiModel>>
        field = MutableStateFlow<Resource<OrderDetailUiModel>>(Resource.Loading())

    private var loadedOrderId: String? = null
    private var fetchJob: Job? = null

    /**
     * Takes the id as an argument rather than through construction, so the screen can use the plain
     * `metroViewModel()` factory instead of an assisted one. Repeat calls for the same id are
     * ignored, which makes it safe to drive from a `LaunchedEffect`.
     */
    fun load(orderId: String) {
        if (loadedOrderId == orderId && detailState.value !is Resource.Error) return

        loadedOrderId = orderId
        fetch(orderId)
    }

    fun retry() {
        val orderId = loadedOrderId ?: return
        if (fetchJob?.isActive == true) return

        detailState.value = Resource.Loading()
        fetch(orderId)
    }

    private fun fetch(orderId: String) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            orderRepository.getOrderDetail(orderId)
                .fold(
                    onSuccess = { detail ->
                        detailState.value = Resource.Success(detail.toUiModel())
                    },
                    onFailure = { exception ->
                        detailState.value = Resource.Error(exception.message)
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}
