package com.cashierserviceapp.ui.utils

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import kotlinx.coroutines.flow.MutableStateFlow

internal class PreviewInteractionSource(interaction: Interaction) : MutableInteractionSource {
    override val interactions = MutableStateFlow(interaction)
    override suspend fun emit(interaction: Interaction) { }
    override fun tryEmit(interaction: Interaction): Boolean = true

    companion object {
        val Hovered =
            PreviewInteractionSource(HoverInteraction.Enter())
        val Pressed =
            PreviewInteractionSource(FocusInteraction.Focus())
    }
}