package com.cashierserviceapp.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
sealed interface TopLevelRoute : AppRoute

/**
 * A route presented modally over the whole app — including the bottom navigation — instead of
 * being pushed onto a backstack. The equivalent of SwiftUI's `fullScreenCover`.
 */
@Serializable
sealed interface CoverRoute : AppRoute

@Serializable
@SerialName("Home")
data object HomeScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Order")
data object OrderScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("AddOrder")
data object AddOrderScreen : CoverRoute

@Serializable
@SerialName("History")
data object HistoryScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Settings")
data object SettingsScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Login")
data object LoginScreen : AppRoute

/** Pushed rather than top-level: search is somewhere you go and come back from, not a tab. */
@Serializable
@SerialName("Search")
data object SearchScreen : AppRoute

/**
 * The first route carrying an argument — hence a data class, not an object. It's serialized into
 * the saved backstack, so [orderId] survives process death along with the rest of the stack.
 */
@Serializable
@SerialName("OrderDetail")
data class OrderDetailScreen(val orderId: String) : AppRoute