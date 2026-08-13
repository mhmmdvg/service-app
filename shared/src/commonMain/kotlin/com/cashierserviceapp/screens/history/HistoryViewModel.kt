package com.cashierserviceapp.screens.history

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
class HistoryViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val historyState: StateFlow<Resource<List<HistorySection>>>
        field = MutableStateFlow<Resource<List<HistorySection>>>(Resource.Loading())

    /** Separate from [historyState] so a pull-to-refresh doesn't blank the list back to skeletons. */
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

        historyState.value = Resource.Loading()
        load()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            orderRepository.getOrderHistory()
                .fold(
                    onSuccess = { orders ->
                        historyState.value = Resource.Success(groupOrdersByDay(orders, today))
                    },
                    onFailure = { exception ->
                        // Keeps whatever is already on screen, so a failed refresh doesn't wipe
                        // a list the user was reading.
                        historyState.value = Resource.Error(
                            message = exception.message,
                            data = historyState.value.data
                        )
                    }
                )

            isRefreshing.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}
