package com.cashierserviceapp.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

private const val DEFAULT_LOOKAHEAD = 3

/**
 * Calls [onLoadMore] as the list nears its end, so the next page is fetched before the user gets
 * there. [lookahead] counts items — spacers and footers included — not rows.
 *
 * Carries the scroll position rather than a "reached the end" flag. A flag only fires on its edges,
 * and a short list latches it true early, which then swallows the real arrival at the bottom.
 *
 * Fires often on purpose: the paginator behind it drops calls made mid-request or past the last
 * page, and only it knows whether there is more to ask for.
 */
@Composable
fun LoadMoreOnScroll(
    state: LazyListState,
    lookahead: Int = DEFAULT_LOOKAHEAD,
    onLoadMore: () -> Unit,
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)

    LaunchedEffect(state, lookahead) {
        snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged()
            .collect { lastVisible ->
                val total = state.layoutInfo.totalItemsCount

                // `>=`, not `==`: a fling skips indices, and an equality test would never match.
                if (total > 0 && lastVisible >= total - 1 - lookahead) currentOnLoadMore()
            }
    }
}
