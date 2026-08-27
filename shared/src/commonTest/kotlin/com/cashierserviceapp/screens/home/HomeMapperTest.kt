package com.cashierserviceapp.screens.home

import com.cashierserviceapp.domain.models.Order
import com.cashierserviceapp.domain.models.OrderStatus
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The queue is trimmed *before* it is mapped, so the trim has to pick the right rows on its own —
 * a sort that stopped agreeing with [AttentionRow.daysWaiting] would silently drop the oldest
 * orders, which are the ones the screen exists to surface.
 */
class HomeMapperTest {

    private val today = LocalDate(2026, 8, 27)

    /** Twelve orders, one per day, newest first — the order `OrderDao` hands them over in. */
    private val cached = (1..12).map { day ->
        Order(
            id = "id-$day",
            status = OrderStatus.IN_PROGRESS,
            customerName = "Customer $day",
            orderCode = "SV-$day",
            createdAt = "2026-08-${day + 10}T09:00:00Z",
            totalCost = 0,
            itemsCount = 1,
        )
    }.reversed()

    @Test
    fun keepsTheLongestWaitingOrdersOldestFirst() {
        val attention = cached.toHomeSnapshot(today).attention

        assertEquals(ATTENTION_PREVIEW_COUNT, attention.size)
        assertEquals(List(ATTENTION_PREVIEW_COUNT) { "id-${it + 1}" }, attention.map { it.id })
        // id-1 was taken in on 11 Aug, so it has waited 16 days; the trim stops at id-8, 9 days.
        assertEquals((16 downTo 9).toList(), attention.map { it.daysWaiting })
    }

    @Test
    fun countsTheWholeCacheNotTheTrimmedList() {
        assertEquals(12, cached.toHomeSnapshot(today).orderCount)
        assertEquals(97, cached.toHomeSnapshot(today, totalCount = 97).orderCount)
        assertEquals(0, emptyList<Order>().toHomeSnapshot(today).orderCount)
    }
}
