package com.cashierserviceapp.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.PageInfo
import com.cashierserviceapp.domain.models.QueryParams
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.PAGE_SIZE
import com.cashierserviceapp.utils.Paginator
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
 * Completed orders, a page at a time, grouped by the day they were taken in.
 *
 * Sections are rebuilt from the whole cache on each emission rather than appended to, so a day
 * split across a page boundary stays one section with one correct total.
 */
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

    /** Distinct from [isRefreshing]: this one shows a spinner under the list, not above it. */
    val isLoadingMore: StateFlow<Boolean>
        field = MutableStateFlow(false)

    /** Lives as long as the screen: the cache keeps emitting long after the first page settles. */
    private var observeJob: Job? = null
    private var pageJob: Job? = null

    private val paginator = Paginator<Int, PageInfo?>(
        initialKey = FIRST_PAGE,
        onLoadUpdated = { loading ->
            isLoadingMore.value = loading

            if (loading && historyState.value.data.isNullOrEmpty()) {
                historyState.value = Resource.Loading(historyState.value.data)
            }
        },
        onRequest = { page ->
            orderRepository.fetchOrderHistory(
                params = QueryParams(perPage = PAGE_SIZE, page = page),
                replaceCache = page == FIRST_PAGE,
            )
        },
        getNextKey = { page, _ -> page + 1 },
        onSuccess = { _, _ ->
            isRefreshing.value = false
            // Rows arrive through the cache, so this only settles the state — and it must: an
            // empty list writes nothing, and would otherwise leave the skeletons up forever.
            if (historyState.value !is Resource.Success) {
                historyState.value = Resource.Success(historyState.value.data.orEmpty())
            }
        },
        onError = { throwable ->
            // Keeps whatever is already on screen, so a failed page doesn't wipe a list the user
            // was reading.
            historyState.value = Resource.Error(throwable.message, historyState.value.data)
            isRefreshing.value = false
        },
        endReached = { _, pageInfo -> pageInfo?.hasNext != true },
    )

    init {
        observe()
        loadNextPage()
    }

    fun loadNextPage() {
        pageJob = viewModelScope.launch { paginator.loadNextItems() }
    }

    fun refresh() {
        if (isRefreshing.value) return

        isRefreshing.value = true
        pageJob?.cancel()
        paginator.reset()
        loadNextPage()
    }

    fun retry() {
        pageJob?.cancel()
        paginator.reset()
        historyState.value = Resource.Loading()
        loadNextPage()
    }

    private fun observe() {
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            orderRepository.observeOrderHistory().collect { orders ->
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val sections = groupOrdersByDay(orders, today)

                // An empty cache while the first page is still in flight is not an empty history.
                if (sections.isEmpty() && historyState.value is Resource.Loading) return@collect

                historyState.value = Resource.Success(sections)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
        pageJob?.cancel()
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
