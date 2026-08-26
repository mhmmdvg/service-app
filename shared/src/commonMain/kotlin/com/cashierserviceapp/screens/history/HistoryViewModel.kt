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
import kotlinx.coroutines.flow.collectLatest
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

    /** Lives as long as the screen: the cache keeps emitting long after the first load settles. */
    private var fetchJob: Job? = null
    private var refreshJob: Job? = null

    init {
        load()
    }

    /**
     * Refreshes without re-subscribing — [fetchJob] is still collecting, so the new rows reach the
     * screen through it the moment the response is written to the cache. All this owns is the
     * spinner and the failure.
     *
     * Guarding on [isRefreshing] rather than on [fetchJob], which now never completes.
     */
    fun refresh() {
        if (isRefreshing.value) return

        isRefreshing.value = true
        refreshJob = viewModelScope.launch {
            orderRepository.refreshOrderHistory().onFailure { exception ->
                historyState.value = Resource.Error(
                    message = exception.message,
                    data = historyState.value.data
                )
            }

            isRefreshing.value = false
        }
    }

    fun retry() {
        historyState.value = Resource.Loading()
        load()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            orderRepository.getOrderHistory().collectLatest { result ->
                result.fold(
                    onSuccess = { orders ->
                        historyState.value = Resource.Success(groupOrdersByDay(orders, today))
                    },
                    onFailure = { exception ->
                        // Only the refresh failed; whatever the cache emitted still stands.
                        historyState.value = Resource.Error(
                            message = exception.message,
                            data = historyState.value.data
                        )
                    }
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
        refreshJob?.cancel()
    }
}
