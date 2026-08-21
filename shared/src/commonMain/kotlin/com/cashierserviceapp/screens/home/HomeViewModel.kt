package com.cashierserviceapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.OrderTracking
import com.cashierserviceapp.domain.repositories.AuthRepository
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
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

    val isRefreshing: StateFlow<Boolean>
        field = MutableStateFlow(false)

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

        homeState.value = Resource.Loading()
        load()
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

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            orderRepository.getOrders().collectLatest { result ->
                result.fold(
                    onSuccess = { orders ->
                        homeState.value = Resource.Success(buildHomeSnapshot(orders, today))
                    },
                    onFailure = { exception ->
                        // Keeps whatever is on screen, so a failed refresh doesn't wipe the queue.
                        homeState.value = Resource.Error(
                            message = exception.message,
                            data = homeState.value.data
                        )
                    }
                )

                isRefreshing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
        trackJob?.cancel()
    }
}
