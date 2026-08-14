package com.cashierserviceapp.screens.order

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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class OrderViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val orderState: StateFlow<Resource<List<OrderRow>>>
        field = MutableStateFlow<Resource<List<OrderRow>>>(Resource.Loading())

    val isRefreshing: StateFlow<Boolean>
        field = MutableStateFlow(false)

    private var fetchJob: Job? = null

    init {
        load()
    }

    fun refresh() {
        if (fetchJob?.isActive == true) return

        isRefreshing.value = true
        load()
    }

    fun retry() {
        if (fetchJob?.isActive == true) return

        orderState.value = Resource.Loading()
        load()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            // Resolved once per load so every row's "Today"/"Yesterday" is measured against the
            // same day, rather than each row asking the clock separately.
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            orderRepository.getOrders()
                .fold(
                    onSuccess = { response ->
                        orderState.value = Resource.Success(response.toOrderRows(today))
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
