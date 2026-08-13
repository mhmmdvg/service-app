package com.cashierserviceapp.screens.addorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.repositories.OrderRepository
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class AddOrderViewModel(
    private val orderRepository: OrderRepository,
) : ViewModel() {
    val form: StateFlow<AddOrderForm>
        field = MutableStateFlow(AddOrderForm())

    val step: StateFlow<AddOrderStep>
        field = MutableStateFlow(AddOrderStep.entries.first())

    val submitState: StateFlow<Resource<CreateOrderResponse>?>
        field = MutableStateFlow<Resource<CreateOrderResponse>?>(null)

    private var submitJob: Job? = null

    fun update(transform: (AddOrderForm) -> AddOrderForm) {
        form.value = transform(form.value)
        // The failure belonged to the values that produced it; editing makes it stale.
        if (submitState.value is Resource.Error) submitState.value = null
    }

    /** Advances a step, or submits when there's nowhere left to go. */
    fun next() {
        val current = step.value
        if (!form.value.isValid(current)) return

        if (current.isLast) submit() else step.value = current.next()
    }

    /** Returns true if it moved back, false when already on the first step. */
    fun back(): Boolean {
        val current = step.value
        if (current.isFirst) return false

        step.value = current.previous()
        return true
    }

    private fun submit() {
        if (submitJob?.isActive == true) return

        submitJob = viewModelScope.launch {
            submitState.value = Resource.Loading()
            orderRepository.createOrder(form.value.toRequest())
                .fold(
                    onSuccess = { response -> submitState.value = Resource.Success(response) },
                    onFailure = { exception -> submitState.value = Resource.Error(exception.message) }
                )
        }
    }

    /**
     * Clears the flow back to a blank first step. The cover this lives in isn't a nav entry, so the
     * ViewModel outlives it — without this the next intake would open onto the previous one's
     * half-filled form.
     */
    fun reset() {
        submitJob?.cancel()
        submitJob = null
        form.value = AddOrderForm()
        step.value = AddOrderStep.entries.first()
        submitState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        submitJob?.cancel()
    }
}
