package com.cashierserviceapp.screens.orderdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.OrderStatus
import com.cashierserviceapp.domain.models.UpdateOrderItemRequest
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.Resource
import com.cashierserviceapp.utils.formatRupiah
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
     * The change lands on screen before the request does — marking the last device completed
     * finishes the order, and waiting out a PATCH and a refetch to see that makes a tap which
     * already worked feel like it didn't. The refetch still wins; a failure puts the snapshot back.
     */
    fun setItemStatus(itemId: String, status: OrderStatus, serviceFee: Long? = null) {
        if (updateJob?.isActive == true) return

        // Exactly what is on screen now — the thing to restore if the write is refused.
        val snapshot = detailState.value.data

        snapshot?.let { detail ->
            val patched = detail.items.map { item ->
                if (item.id != itemId) return@map item

                // A null fee means the sheet said nothing about the price, so what was agreed
                // stands. The cost follows the server's recalculateFinalCost: fee plus every part.
                val fee = serviceFee ?: item.serviceFee
                val cost = (fee ?: 0L) + item.parts.sumOf { it.subtotal }

                item.copy(
                    status = status,
                    serviceFee = fee,
                    serviceFeeLabel = fee?.let { formatRupiah(it) },
                    finalCost = cost,
                    totalLabel = formatRupiah(cost),
                )
            }

            detailState.value = Resource.Success(detail.copy(items = patched))
        }

        updateJob = viewModelScope.launch {
            updatingItemId.value = itemId
            updateError.value = null

            val request = UpdateOrderItemRequest(status = status, serviceFee = serviceFee)

            orderRepository.updateOrderItem(itemId, request)
                .fold(
                    onSuccess = { fetch().join() },
                    onFailure = { exception ->
                        // Roll back before surfacing the error, so the banner and the rows agree.
                        snapshot?.let { detailState.value = Resource.Success(it) }
                        updateError.value = exception.message
                    }
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
