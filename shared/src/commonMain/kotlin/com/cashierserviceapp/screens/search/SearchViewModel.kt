package com.cashierserviceapp.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.screens.home.AttentionRow
import com.cashierserviceapp.screens.home.search
import com.cashierserviceapp.screens.home.toRows
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

/**
 * Searching the in-progress list.
 *
 * The whole list is pulled once and filtered locally — the server has no search endpoint, and the
 * in-progress set is small by nature (it's the shop's current workload), so a round trip per
 * keystroke would be slower and worse.
 */
@ContributesIntoMap(AppScope::class)
@ViewModelKey
class SearchViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val ordersState: StateFlow<Resource<List<AttentionRow>>>
        field = MutableStateFlow<Resource<List<AttentionRow>>>(Resource.Loading())

    val query: StateFlow<String>
        field = MutableStateFlow("")

    val results: StateFlow<List<AttentionRow>>
        field = MutableStateFlow<List<AttentionRow>>(emptyList())

    private var fetchJob: Job? = null

    init {
        load()
    }

    fun onQueryChange(value: String) {
        query.value = value
        results.value = ordersState.value.data.orEmpty().search(value)
    }

    fun retry() {
        if (fetchJob?.isActive == true) return

        ordersState.value = Resource.Loading()
        load()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            orderRepository.getOrders()
                .fold(
                    onSuccess = { orders ->
                        val rows = orders.toRows(today)
                        ordersState.value = Resource.Success(rows)
                        // Anything typed while the list was still loading applies now.
                        results.value = rows.search(query.value)
                    },
                    onFailure = { exception ->
                        ordersState.value = Resource.Error(exception.message)
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
    }
}
