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
 * count of rows would drift from the indices `visibleItemsInfo` reports the moment one is added.
 * The flip side is that [lookahead] counts items, so a list bracketed by two spacers reaches the
 * threshold roughly two rows sooner than the number suggests.
 *
 * Firing repeatedly is expected and harmless — a fling crosses the threshold many times, and the
 * paginator behind this drops any call made while a request is already in flight or after the last
 * page has arrived. That is deliberately the paginator's job, not this composable's: only it knows
 * whether there is anything left to ask for.
 *
 * Which is why the flow carries the scroll *position* and not a "have we reached the end" verdict.
 * De-duplicating on a verdict means only its edges fire, and an early one latches: while the list
 * is still short — skeletons, or an empty moment before the first page's rows arrive — every item
 * sits inside the lookahead window, so the verdict goes true, spends its one edge on a call the
 * paginator correctly drops, and stays true. Reaching the bottom for real is then "no change", and
 * the list only loads after leaving the screen and coming back, which restarts this effect and
 * clears the latch. Keyed on position instead, every row scrolled onto asks again, so there is no
 * edge to miss and no dependence on which frames survive `snapshotFlow`'s conflation.
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
        snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .collect { lastVisible ->
                val total = state.layoutInfo.totalItemsCount

                // `>=`, not `==`: a fling can skip the exact index the threshold names, and an
                // equality test would simply never fire on that scroll.
                if (total > 0 && lastVisible >= total - 1 - lookahead) currentOnLoadMore()
            }
    }
}
