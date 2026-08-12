package com.cashierserviceapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.Post
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.domain.repositories.PostRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class HomeViewModel(
    private val orderRepository: OrderRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    val orderState: StateFlow<List<Order>>
        field = MutableStateFlow<List<Order>>(emptyList())

    val postState: StateFlow<List<Post>>
        field = MutableStateFlow<List<Post>>(emptyList())

    private var fetchJob: Job? = null
    private var postFetchJob: Job? = null

    init {
        load()
        loadPosts()
    }

    private fun load() {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            orderRepository.getOrders()
                .fold(
                    onSuccess = { response ->
                        println("response $response")
                        orderState.value = response
                    },
                    onFailure = { exception ->
                        println("Check Error $exception")
                    }
                )
        }
    }

    private fun loadPosts() {
        postFetchJob?.cancel()

        postFetchJob = viewModelScope.launch {
            postRepository.getPosts()
                .fold(
                    onSuccess = { response ->
                        postState.value = response
                        println("Posts loaded: ${response.size}")
                    },
                    onFailure = { exception ->
                        println("Posts failed: $exception")
                    }
                )
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
        postFetchJob?.cancel()
    }
}