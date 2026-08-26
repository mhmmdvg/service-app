package com.cashierserviceapp.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** How many items from the end of the list the next page is asked for. */
private const val DEFAULT_LOOKAHEAD = 3

/**
 * Calls [onLoadMore] as the list nears its end, so the next page is fetched before the user
 * arrives at the bottom.
 *
 * Measured against `totalItemsCount` rather than a row count the caller passes in, because these
 * lists aren't only rows: spacers, sticky headers and the footer spinner are all items too, and a
 * list whose sections change shape would otherwise trigger at the wrong place.
 *
 * Firing repeatedly is expected and harmless — a fling crosses the threshold many times, and the
 * paginator behind this drops any call made while a request is already in flight or after the last
 * page has arrived. That is deliberately the paginator's job, not this composable's: only it knows
 * whether there is anything left to ask for.
 */
@Composable
fun LoadMoreOnScroll(
    state: LazyListState,
    lookahead: Int = DEFAULT_LOOKAHEAD,
    onLoadMore: () -> Unit,
) {
    // Kept current so the effect doesn't have to restart every time the caller recomposes.
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)

    LaunchedEffect(state, lookahead) {
        snapshotFlow {
            val lastVisible = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            val total = state.layoutInfo.totalItemsCount

            total > 0 && lastVisible >= total - 1 - lookahead
        }
            .distinctUntilChanged()
            .collect { reachedEnd -> if (reachedEnd) currentOnLoadMore() }
    }
}
