package com.cashierserviceapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.models.QueryParams
import com.cashierserviceapp.domain.repositories.AuthRepository
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.PAGE_SIZE
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class HomeViewModel(
    private val orderRepository: OrderRepository,
    authRepository: AuthRepository,
) : ViewModel() {
    val homeState: StateFlow<Resource<HomeSnapshot>>
        field = MutableStateFlow<Resource<HomeSnapshot>>(Resource.Loading())

    /** Null while no QR lookup is in flight or showing. */
    val trackingState: StateFlow<Resource<OrderTracking>?>
        field = MutableStateFlow<Resource<OrderTracking>?>(null)

    /** Drives the greeting. Seeded from the session so it's there on the first frame. */
    val userName: StateFlow<String?> = authRepository.currentUser
        .map { it?.name }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            authRepository.currentUser.value?.name
        )

    private var fetchJob: Job? = null
    private var trackJob: Job? = null

    /** The queue's true size, which the cache can't report once it only holds the pages loaded. */
    private var totalCount: Int? = null

    init {
        observe()
        fetchFirstPage()
    }

    fun retry() {
        homeState.value = Resource.Loading()
        fetchFirstPage()
    }

    /** Looks up the token from a scanned QR code, or one typed in by hand. */
    fun track(qrToken: String) {
        if (qrToken.isBlank() || trackJob?.isActive == true) return

        trackJob = viewModelScope.launch {
            trackingState.value = Resource.Loading()
            orderRepository.trackOrder(qrToken)
                .fold(
                    onSuccess = { tracking -> trackingState.value = Resource.Success(tracking) },
                    onFailure = { exception ->
                        trackingState.value = Resource.Error(exception.message)
                    }
                )
        }
    }

    fun dismissTracking() {
        trackJob?.cancel()
        trackingState.value = null
    }

    /**
     * Tops the cache up rather than replacing it. The Order screen owns the queue and pages through
     * it; if Home replaced the cache too, it would cut that list back to one page while its
     * paginator asked for page 5 — a gap in the middle. Orders finished on this device still leave
     * the queue at once, because the detail screen writes their status straight to the row.
     */
    private fun fetchFirstPage() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            orderRepository.fetchOrders(
                params = QueryParams(perPage = PAGE_SIZE, page = FIRST_PAGE),
                replaceCache = false,
            ).fold(
                onSuccess = { pageInfo ->
                    totalCount = pageInfo?.total
                    // Republishes with the new total, and settles the state at all — an empty queue
                    // writes nothing, and would otherwise leave the skeletons up forever.
                    val snapshot = homeState.value.data ?: HomeSnapshot()
                    homeState.value = Resource.Success(snapshot.copy(totalCount = totalCount))
                },
                onFailure = { exception ->
                    // Keeps whatever is on screen, so a failed refresh doesn't wipe the queue.
                    homeState.value = Resource.Error(
                        message = exception.message,
                        data = homeState.value.data
                    )
                }
            )
        }
    }

    private fun observe() {
        viewModelScope.launch {
            orderRepository.observeOrders().collect { orders ->
                // Resolved per emission so every row's "Today"/"Yesterday" is measured against the
                // same day, rather than each row asking the clock separately.
                val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

                // An empty cache while the first page is still in flight is not an empty queue.
                if (orders.isEmpty() && homeState.value is Resource.Loading) return@collect

                homeState.value = Resource.Success(orders.toHomeSnapshot(today, totalCount))
            }
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
