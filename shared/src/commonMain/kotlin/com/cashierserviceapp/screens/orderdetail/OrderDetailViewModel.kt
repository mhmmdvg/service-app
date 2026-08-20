package com.cashierserviceapp.screens.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.*
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AssistedInject
class OrderDetailViewModel(
    private val orderRepository: OrderRepository,
    @Assisted private val orderId: String,
) : ViewModel() {
    val detailState: StateFlow<Resource<OrderDetailUiModel>>
        field = MutableStateFlow<Resource<OrderDetailUiModel>>(Resource.Loading())

    /** Which device is mid-update, so its row can show progress without blocking the rest. */
    val updatingItemId: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    /** Surfaced separately from [detailState]: a failed update shouldn't blank the loaded order. */
    val updateError: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    private var fetchJob: Job? = null
    private var updateJob: Job? = null

    init {
        fetch()
    }


    fun retry() {
        if (fetchJob?.isActive == true) return

        detailState.value = Resource.Loading()
        fetch()
    }

    /**
     * Moves one device to [status], settling its price at the same time.
     *
     * [serviceFee] is null whenever the sheet had nothing new to say about the price — the request
     * then omits the field, so a fee that was already agreed survives a plain status change.
     *
     * Reloads the whole order afterwards rather than patching the item in place: the server
     * recalculates that item's final cost, and the order's overall status is derived from every
     * device, so a local edit would only be right by coincidence.
     */
    fun setItemStatus(itemId: String, status: OrderStatus, serviceFee: Long? = null) {
        if (updateJob?.isActive == true) return


        updateJob = viewModelScope.launch {
            updatingItemId.value = itemId
            updateError.value = null

            val request = UpdateOrderItemRequest(status = status, serviceFee = serviceFee)

            orderRepository.updateOrderItem(itemId, request)
                .fold(
                    onSuccess = { fetch().join() },
                    onFailure = { exception -> updateError.value = exception.message }
                )

            updatingItemId.value = null
        }
    }

    fun clearUpdateError() {
        updateError.value = null
    }

    private fun fetch(): Job {
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

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    fun interface Factory : ManualViewModelAssistedFactory {
        fun create(orderId: String): OrderDetailViewModel
    }
}
