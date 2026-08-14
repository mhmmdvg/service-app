package com.cashierserviceapp.di

import com.cashierserviceapp.storage.createSettings
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
object JvmBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(): ObservableSettings = createSettings()
}