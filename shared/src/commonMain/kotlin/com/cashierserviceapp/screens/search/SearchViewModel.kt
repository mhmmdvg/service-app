package com.cashierserviceapp.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.QueryParams
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.screens.home.AttentionRow
import com.cashierserviceapp.screens.home.toAttentionRow
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

/**
 * Searching every order, in progress or finished, through `GET /orders`.
 *
 * The server does the matching, not the client. It reaches the whole archive rather than whatever
 * the queue happens to hold, and it matches on customer phone as well as name and order code —
 * neither of which a local filter over the in-progress list could do. The debounce is what keeps
 * that affordable: one request per pause in typing, not one per keystroke.
 */
@OptIn(FlowPreview::class)
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
        viewModelScope.launch {
            query
                .debounce(400.milliseconds)
                .map { it.trim() }
                .distinctUntilChanged()
                .collect { term ->
                    // Blank is the screen's resting state, not a query for everything — asking the
                    // server would page in the entire archive to sit behind the hint text.
                    if (term.isEmpty()) clear() else onSearch(QueryParams(term))
                }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun retry() {
        val term = query.value.trim()
        if (term.isEmpty()) return

        onSearch(QueryParams(term))
    }

    private fun clear() {
        fetchJob?.cancel()
        ordersState.value = Resource.Success(emptyList())
        results.value = emptyList()
    }

    private fun onSearch(params: QueryParams) {
        fetchJob?.cancel()

        // Back to skeletons: the rows on screen answer the previous term, not this one.
        ordersState.value = Resource.Loading()

        fetchJob = viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            orderRepository.searchOrders(params).fold(
                onSuccess = { orders ->
                    // Kept newest first as the server sent them, not re-sorted by wait time:
                    // results span finished work, where "waiting longest" means nothing.
                    val rows = orders.map { it.toAttentionRow(today) }
                    ordersState.value = Resource.Success(rows)
                    results.value = rows
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
