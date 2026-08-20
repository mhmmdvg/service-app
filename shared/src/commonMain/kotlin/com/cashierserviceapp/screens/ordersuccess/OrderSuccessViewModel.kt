package com.cashierserviceapp.screens.ordersuccess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.OrderDetail
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.screens.orderdetail.OrderDetailUiModel
import com.cashierserviceapp.screens.orderdetail.toUiModel
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AssistedInject
class OrderSuccessViewModel(
    private val orderRepository: OrderRepository,
    @Assisted private val orderId: String
) : ViewModel() {
    val detailState: StateFlow<Resource<OrderDetailUiModel>>
        field = MutableStateFlow<Resource<OrderDetailUiModel>>(Resource.Loading())

    private var fetchJob: Job? = null

    init {
        load()
    }

    fun retry() {
        if (fetchJob?.isActive == true) return

        detailState.value = Resource.Loading()
        load()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            orderRepository.getOrderDetail(orderId)
                .fold(
                    onSuccess = { detail ->
                        detailState.value = Resource.Success(detail.toUiModel())
                    },
                    onFailure = { exception ->
                        detailState.value = Resource.Error(exception.message ?: "Unknown error")
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(orderId: String): OrderSuccessViewModel
    }
}