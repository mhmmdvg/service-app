package com.cashierserviceapp.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute

@Serializable
sealed interface TopLevelRoute : AppRoute

@Serializable
@SerialName("Home")
data object HomeScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Order")
data object OrderScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("History")
data object HistoryScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Settings")
data object SettingsScreen : AppRoute, TopLevelRoute

@Serializable
@SerialName("Login")
data object LoginScreen : AppRoute