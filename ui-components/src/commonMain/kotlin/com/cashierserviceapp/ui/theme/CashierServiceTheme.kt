package com.cashierserviceapp.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode

private object NoIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        object : DelegatableNode, DrawModifierNode, Modifier.Node() {
            override fun ContentDrawScope.draw() {
                drawContent()
            }
        }

    override fun equals(other: Any?): Boolean = other === NoIndication
    override fun hashCode(): Int = 0
}

val LocalColors = compositionLocalOf<Colors> {
    error("CashierServiceTheme must be part of the call hierarchy to provide colors")
}

val LocalShapes = compositionLocalOf<Shapes> {
    error("CashierServiceTheme must be part of the call hierarchy to provide shapes")
}

val LocalTypography = compositionLocalOf<Typography> {
    error("CashierServiceTheme must be part of the call hierarchy to provide typography")
}

object CashierServiceTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = LocalShapes.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object LocalAppTheme {
    val current: Boolean @Composable get

    @Composable
    infix fun provides(value: Boolean?): ProvidedValue<*>
}

@Composable
fun CashierServiceTheme(
    colors: Colors = if (isSystemInDarkTheme()) CashierServiceDarkColors else CashierServiceLightColors,
    rippleEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalShapes provides CashierServiceShapes,
        LocalTypography provides CashierServiceTypography,
        LocalIndication provides if (rippleEnabled) NoIndication else NoIndication,
        LocalAppTheme provides colors.isDark
    ) {
        content()
    }
}