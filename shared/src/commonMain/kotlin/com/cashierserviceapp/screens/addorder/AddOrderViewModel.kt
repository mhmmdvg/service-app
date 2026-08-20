package com.cashierserviceapp.screens.addorder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashierserviceapp.domain.models.CreateOrderResponse
import com.cashierserviceapp.domain.models.SparePart
import com.cashierserviceapp.domain.repositories.SparePartRepository
import com.cashierserviceapp.domain.usecases.addorder.AddOrderValidators
import com.cashierserviceapp.domain.usecases.addorder.CreateOrderUseCase
import com.cashierserviceapp.domain.usecases.corevalidation.Validation
import com.cashierserviceapp.domain.usecases.corevalidation.ValidationError
import com.cashierserviceapp.domain.usecases.corevalidation.errorOrNull
import com.cashierserviceapp.domain.usecases.corevalidation.isValid
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
class AddOrderViewModel(
    private val sparePartRepository: SparePartRepository,
    private val createOrder: CreateOrderUseCase,
    private val validators: AddOrderValidators,
) : ViewModel() {
    /* Form State */
    val formState: StateFlow<AddOrderFormState>
        field = MutableStateFlow(AddOrderFormState())

    val step: StateFlow<AddOrderStep>
        field = MutableStateFlow(AddOrderStep.entries.first())

    val submitState: StateFlow<Resource<CreateOrderResponse>?>
        field = MutableStateFlow<Resource<CreateOrderResponse>?>(null)

    /** The parts catalogue, loaded once when the flow opens so the picker is instant. */
    val catalogue: StateFlow<Resource<List<SparePart>>>
        field = MutableStateFlow<Resource<List<SparePart>>>(Resource.Loading())

    private var submitJob: Job? = null
    private var catalogueJob: Job? = null

    init {
        loadCatalogue()
    }

    fun loadCatalogue() {
        if (catalogueJob?.isActive == true) return

        catalogueJob = viewModelScope.launch {
            catalogue.value = Resource.Loading()
            sparePartRepository.getSpareParts()
                .fold(
                    onSuccess = { parts -> catalogue.value = Resource.Success(parts) },
                    onFailure = { exception -> catalogue.value = Resource.Error(exception.message) }
                )
        }
    }

    fun onAddOrderEvent(event: AddOrderFormEvent) {
        when (event) {
            is AddOrderFormEvent.NameChanged -> {
                formState.update { it.copy(name = event.name, nameError = null) }
                clearError()
            }

            // Trimmed on the way in, like the login form: the anchored regex in ValidateEmail
            // rejects the stray trailing space soft keyboards like to append.
            is AddOrderFormEvent.EmailChanged -> {
                formState.update { it.copy(email = event.email.trim(), emailError = null) }
                clearError()
            }

            is AddOrderFormEvent.PhoneChanged -> {
                formState.update { it.copy(phone = event.phone.trim(), phoneError = null) }
                clearError()
            }

            is AddOrderFormEvent.AddressChanged -> {
                formState.update { it.copy(address = event.address) }
                clearError()
            }

            is AddOrderFormEvent.DeviceSaved -> {
                formState.update { current ->
                    val known = current.devices.any { it.localId == event.device.localId }

                    current.copy(
                        devices = if (known) {
                            current.devices.map {
                                if (it.localId == event.device.localId) event.device else it
                            }
                        } else {
                            current.devices + event.device
                        }
                    )
                }
                clearError()
            }

            is AddOrderFormEvent.DeviceRemoved -> {
                formState.update { current ->
                    current.copy(devices = current.devices.filterNot { it.localId == event.localId })
                }
                clearError()
            }
        }
    }

    // --- devices ---------------------------------------------------------------------------

    /**
     * A blank device for the sheet to edit. Deliberately *not* added to the form: an unfinished
     * device would otherwise sit in the list behind the sheet, and be left there by a crash or a
     * kill mid-edit. It joins the form when [AddOrderFormEvent.DeviceSaved] says so.
     */
    fun newDevice(): DeviceDraft = DeviceDraft(localId = newLocalId())

    /** Handed to the part picker so drafts it builds get an id from the same sequence. */
    fun newPartLocalId(): String = newLocalId()

    // --- validation ------------------------------------------------------------------------

    /** The server's rules for one device, for the sheet that edits it. */
    fun validateDevice(device: DeviceDraft): Validation =
        validators.validateDevice.execute(device.brand, device.model, device.complaint)

    /**
     * Whether [step] is filled in enough to leave. Drives the Next button, so it stays a quiet
     * yes/no — the messages only appear once [next] is actually pressed.
     */
    fun isStepComplete(state: AddOrderFormState, step: AddOrderStep): Boolean = when (step) {
        AddOrderStep.CUSTOMER -> state.name.isNotBlank()
        // At least one device, and every one of them complete — a half-filled second device would
        // otherwise be sent as a real item.
        AddOrderStep.DEVICE ->
            state.devices.isNotEmpty() && state.devices.all { validateDevice(it).isValid }
    }

    // --- steps -----------------------------------------------------------------------------

    fun next() {
        val current = step.value

        if (current == AddOrderStep.CUSTOMER && !validateCustomer()) return
        if (!isStepComplete(formState.value, current)) return

        if (current.isLast) submit() else step.value = current.next()
    }

    /** Returns true if it moved back, false when already on the first step. */
    fun back(): Boolean {
        val current = step.value
        if (current.isFirst) return false

        step.value = current.previous()
        return true
    }

    /**
     * Runs the full customer rules, not just the Next-button check: this is where a malformed
     * phone or email gets caught, both of which are optional enough to leave blank.
     */
    private fun validateCustomer(): Boolean {
        val state = formState.value

        val nameError = validators.validateRequired
            .execute(state.name, ValidationError.NameBlank)
            .errorOrNull
        val phoneError = validators.validatePhoneNumber
            .execute(state.phone, required = false)
            .errorOrNull
        val emailError = validators.validateEmail
            .execute(state.email, required = false)
            .errorOrNull

        formState.update {
            it.copy(nameError = nameError, phoneError = phoneError, emailError = emailError)
        }

        return nameError == null && phoneError == null && emailError == null
    }

    // --- submit ----------------------------------------------------------------------------

    private fun submit() {
        if (submitJob?.isActive == true) return

        submitJob = viewModelScope.launch {
            submitState.value = Resource.Loading()

            createOrder.execute(formState.value.toIntake())
                .fold(
                    onSuccess = { response -> submitState.value = Resource.Success(response) },
                    onFailure = { exception -> submitState.value = Resource.Error(exception.message) }
                )
        }
    }

    fun reset() {
        submitJob?.cancel()
        submitJob = null
        formState.value = AddOrderFormState()
        step.value = AddOrderStep.entries.first()
        submitState.value = null
    }

    /** Drops the error banner once the user starts correcting their input. */
    private fun clearError() {
        if (submitState.value is Resource.Error) submitState.value = null
    }

    override fun onCleared() {
        super.onCleared()
        submitJob?.cancel()
        catalogueJob?.cancel()
    }
}

private var localIdCounter = 0

/** Unique within one run of the flow, which is all a list key needs. */
private fun newLocalId(): String = "draft-${localIdCounter++}"
