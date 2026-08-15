package com.cashierserviceapp.screens.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
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

    /** Which device is mid-update, so its row can show progress without blocking the rest. */
    val updatingItemId: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    /** Surfaced separately from [detailState]: a failed update shouldn't blank the loaded order. */
    val updateError: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    private var loadedOrderId: String? = null
    private var fetchJob: Job? = null
    private var updateJob: Job? = null

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

    /**
     * Moves one device to [status].
     *
     * Reloads the whole order afterwards rather than patching the item in place: the server
     * recalculates that item's final cost, and the order's overall status is derived from every
     * device, so a local edit would only be right by coincidence.
     */
    fun setItemStatus(itemId: String, status: OrderStatus) {
        if (updateJob?.isActive == true) return

        val orderId = loadedOrderId ?: return

        updateJob = viewModelScope.launch {
            updatingItemId.value = itemId
            updateError.value = null

            orderRepository.updateOrderItem(itemId, UpdateOrderItemRequest(status = status))
                .fold(
                    onSuccess = { fetch(orderId).join() },
                    onFailure = { exception -> updateError.value = exception.message }
                )

            updatingItemId.value = null
        }
    }

    fun clearUpdateError() {
        updateError.value = null
    }

    private fun fetch(orderId: String): Job {
        fetchJob?.cancel()

        val job = viewModelScope.launch {
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

        fetchJob = job
        return job
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
        updateJob?.cancel()
    }
}
