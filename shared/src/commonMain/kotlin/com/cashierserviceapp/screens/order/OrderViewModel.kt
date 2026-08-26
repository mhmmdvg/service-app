package com.cashierserviceapp.screens.order

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
 * The in-progress queue, a page at a time.
 *
 * This screen owns the queue's cache: its first page replaces what's there, so a refresh drops
 * orders the server no longer returns. Home deliberately only appends — see [com.cashierserviceapp
 * .screens.home.HomeViewModel] — because two screens both replacing a shared cache would let one
 * truncate pages the other had already loaded, leaving a gap in the middle of this list.
 */
@ContributesIntoMap(AppScope::class)
@ViewModelKey
class OrderViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val orderState: StateFlow<Resource<List<OrderRow>>>
        field = MutableStateFlow<Resource<List<OrderRow>>>(Resource.Loading())

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

            // The very first page is the screen's own loading state, not a footer spinner.
            if (loading && orderState.value.data.isNullOrEmpty()) {
                orderState.value = Resource.Loading(orderState.value.data)
            }
        },
        onRequest = { page ->
            orderRepository.fetchOrders(
                params = QueryParams(perPage = PAGE_SIZE, page = page),
                // Only the first page may drop what's cached; the rest add to it.
                replaceCache = page == FIRST_PAGE,
            )
        },
        getNextKey = { page, _ -> page + 1 },
        onSuccess = { _, _ ->
            isRefreshing.value = false
            // The rows arrive through the cache, so this only has to settle the state itself —
            // and it must, because an empty list writes nothing and would otherwise leave the
            // screen on its skeletons forever. Nothing to show is still an answer.
            if (orderState.value !is Resource.Success) {
                orderState.value = Resource.Success(orderState.value.data.orEmpty())
            }
        },
        onError = { throwable ->
            // Keeps whatever is already on screen, so a failed page doesn't wipe the list.
            orderState.value = Resource.Error(throwable.message, orderState.value.data)
            isRefreshing.value = false
        },
        // A page info the server didn't send is treated as the end rather than paging forever.
        endReached = { _, pageInfo -> pageInfo?.hasNext != true },
    )

    init {
        observe()
        loadNextPage()
    }

    /** Called as the list nears its end; the paginator drops the call if one is already in flight. */
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
        orderState.value = Resource.Loading()
        loadNextPage()
    }

    private fun observe() {
        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            orderRepository.observeOrders().collect { orders ->
                // Resolved per emission so every row's label is measured against the same day.
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                val rows = orders.toOrderRows(today)

                // An empty cache while the first page is still in flight is not an empty queue.
                if (rows.isEmpty() && orderState.value is Resource.Loading) return@collect

                orderState.value = Resource.Success(rows)
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
