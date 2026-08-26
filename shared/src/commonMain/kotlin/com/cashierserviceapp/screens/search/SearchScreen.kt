package com.cashierserviceapp.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.action_back
import cashierserviceapp.shared.generated.resources.action_try_again
import cashierserviceapp.shared.generated.resources.error_generic
import cashierserviceapp.shared.generated.resources.home_orders_load_failed
import cashierserviceapp.shared.generated.resources.search_empty_body
import cashierserviceapp.shared.generated.resources.search_empty_title
import cashierserviceapp.shared.generated.resources.search_hint_body
import cashierserviceapp.shared.generated.resources.search_hint_title
import cashierserviceapp.shared.generated.resources.search_placeholder
import cashierserviceapp.shared.generated.resources.search_result_count_one
import cashierserviceapp.shared.generated.resources.search_result_count_other
import com.cashierserviceapp.navigation.SharedElementKey
import com.cashierserviceapp.navigation.sharedElementBounds
import com.cashierserviceapp.ui.components.ContentMessage
import com.cashierserviceapp.screens.history.components.HistoryOrderCardSkeleton
import com.cashierserviceapp.screens.home.AttentionRow
import com.cashierserviceapp.screens.home.components.AttentionCard
import com.cashierserviceapp.ui.components.CircleIconButton
import com.cashierserviceapp.ui.components.SearchField
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.icons.ChevronLeftOutlined
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.ui.theme.PreviewHelper
import com.cashierserviceapp.ui.utils.PreviewLightDark
import com.cashierserviceapp.utils.Resource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

private const val SKELETON_ROW_COUNT = 4

/**
 * A screen of its own rather than a field on Home: searching gets the whole display and the
 * keyboard opens straight away, which is what a cashier reaching for a customer's name expects.
 */
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenOrder: (String) -> Unit = {},
    viewModel: SearchViewModel = metroViewModel(),
) {
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()

    SearchContent(
        state = ordersState,
        query = query,
        results = results,
        onQueryChange = viewModel::onQueryChange,
        onRetry = viewModel::retry,
        onBack = onBack,
        onOpenOrder = onOpenOrder,
    )
}

@Composable
private fun SearchContent(
    state: Resource<List<AttentionRow>>,
    query: String,
    results: List<AttentionRow>,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onOpenOrder: (String) -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    // Opening the screen *is* the intent to type, so don't make them tap the field as well.
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        Modifier
            .fillMaxSize()
            // A screen has to paint its own surface. At rest nothing is drawn behind it and a
            // transparent one looks fine, but a push or a predictive-back gesture draws two screens
            // at once — without this, the screen underneath shows straight through this one.
            // ScreenWithTitle-based screens get the same thing from Scaffold's containerColor.
            .background(CashierServiceTheme.colors.mainBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
    ) {
        Row(
            Modifier.padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                modifier = Modifier.size(44.dp),
                onClick = onBack,
                icon = ChevronLeftOutlined,
                contentDescription = stringResource(Res.string.action_back)
            )

            Spacer(Modifier.width(8.dp))

            SearchField(
                // Lands where Home's tap target was, morphing out of it rather than appearing.
                modifier = Modifier.sharedElementBounds(SharedElementKey.SearchPill),
                value = query,
                onValueChange = onQueryChange,
                placeholder = stringResource(Res.string.search_placeholder),
                focusRequester = focusRequester,
                keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() })
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 17.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                // Nothing typed yet: say what search covers instead of showing a blank screen.
                query.isBlank() -> item("hint") {
                    ContentMessage(
                        title = stringResource(Res.string.search_hint_title),
                        body = stringResource(Res.string.search_hint_body)
                    )
                }

                state is Resource.Loading -> items(SKELETON_ROW_COUNT) {
                    HistoryOrderCardSkeleton()
                }

                state is Resource.Error -> item("error") {
                    ContentMessage(
                        title = stringResource(Res.string.home_orders_load_failed),
                        body = state.message ?: stringResource(Res.string.error_generic),
                        actionLabel = stringResource(Res.string.action_try_again),
                        onAction = onRetry
                    )
                }

                results.isEmpty() -> item("empty") {
                    ContentMessage(
                        title = stringResource(Res.string.search_empty_title, query),
                        body = stringResource(Res.string.search_empty_body)
                    )
                }

                else -> {
                    item("count") {
                        Text(
                            text = if (results.size == 1) {
                                stringResource(Res.string.search_result_count_one)
                            } else {
                                stringResource(Res.string.search_result_count_other, results.size)
                            },
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = CashierServiceTheme.typography.text2,
                            color = CashierServiceTheme.colors.secondaryText
                        )
                    }

                    items(items = results, key = { it.id }) { row ->
                        AttentionCard(row = row, onClick = { onOpenOrder(row.id) })
                    }
                }
            }

            item("bottom_spacer") { Spacer(Modifier.height(16.dp)) }
        }
    }
}

private val previewRows = listOf(
    AttentionRow("1", "Rina Wijaya", "SV-1786641253", 1, "Rp 350.000", daysWaiting = 1),
    AttentionRow("2", "Rina Kusuma", "SV-1786641190", 2, "Rp 0", daysWaiting = 5),
)

@PreviewLightDark
@Composable
private fun SearchScreenResultsPreview() = PreviewHelper(paddingEnabled = false) {
    SearchContent(
        state = Resource.Success(previewRows),
        query = "rina",
        results = previewRows,
        onQueryChange = {},
        onRetry = {},
        onBack = {}
    )
}

@PreviewLightDark
@Composable
private fun SearchScreenHintPreview() = PreviewHelper(paddingEnabled = false) {
    SearchContent(
        state = Resource.Success(previewRows),
        query = "",
        results = emptyList(),
        onQueryChange = {},
        onRetry = {},
        onBack = {}
    )
}
