package com.cashierserviceapp.utils

/**
 * Rows per page for the order and history lists.
 *
 * Well above the server's own default of 5 — that default suits a curl, not a list a cashier
 * scrolls — and well under its cap of 100, so one screenful never costs several requests.
 */
const val PAGE_SIZE = 10

/**
 * Owns only the position: which key comes next, whether a request is in flight, whether the end is
 * reached. What arrives goes to [onSuccess] to store wherever the list lives — here the Room cache
 * the screens observe — so this never holds the rows itself.
 *
 * Re-entrant calls are dropped, not queued: a fling fires the load trigger repeatedly, and each
 * would otherwise refetch the same page.
 */
class Paginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<Item>,
    private val getNextKey: suspend (currentKey: Key, result: Item) -> Key,
    private val onError: suspend (Throwable) -> Unit,
    private val onSuccess: suspend (result: Item, newKey: Key) -> Unit,
    private val endReached: (currentKey: Key, result: Item) -> Boolean,
) {
    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    val isLoading: Boolean get() = isMakingRequest

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) return

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        val item = result.getOrElse { throwable ->
            onError(throwable)
            onLoadUpdated(false)
            return
        }

        currentKey = getNextKey(currentKey, item)
        onSuccess(item, currentKey)
        onLoadUpdated(false)
        isEndReached = endReached(currentKey, item)
    }

    /** Back to the first page — a pull-to-refresh, or a retry after a failed first load. */
    fun reset() {
        currentKey = initialKey
        isEndReached = false
        isMakingRequest = false
    }
}
