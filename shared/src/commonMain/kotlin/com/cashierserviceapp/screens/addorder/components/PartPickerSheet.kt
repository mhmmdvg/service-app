package com.cashierserviceapp.screens.addorder.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cashierserviceapp.shared.generated.resources.Res
import cashierserviceapp.shared.generated.resources.add_order_part_add
import cashierserviceapp.shared.generated.resources.add_order_part_catalogue
import cashierserviceapp.shared.generated.resources.add_order_part_empty
import cashierserviceapp.shared.generated.resources.add_order_part_manual
import cashierserviceapp.shared.generated.resources.add_order_part_name
import cashierserviceapp.shared.generated.resources.add_order_part_price
import cashierserviceapp.shared.generated.resources.add_order_part_qty
import cashierserviceapp.shared.generated.resources.add_order_part_search
import cashierserviceapp.shared.generated.resources.add_order_part_title
import cashierserviceapp.shared.generated.resources.add_order_part_out_of_stock
import com.cashierserviceapp.domain.models.SparePart
import com.cashierserviceapp.screens.addorder.PartDraft
import com.cashierserviceapp.ui.components.BottomSheet
import com.cashierserviceapp.ui.components.Button
import com.cashierserviceapp.ui.components.SearchField
import com.cashierserviceapp.ui.components.SegmentedSelector
import com.cashierserviceapp.ui.components.Text
import com.cashierserviceapp.ui.components.TextField
import com.cashierserviceapp.ui.theme.CashierServiceTheme
import com.cashierserviceapp.utils.Resource
import com.cashierserviceapp.utils.formatRupiah
import org.jetbrains.compose.resources.stringResource

private enum class PartSource { CATALOGUE, MANUAL }

/**
 * Adds one spare part to a device — either picked from inventory or typed in.
 *
 * A typed part is only a draft here; it becomes a real inventory entry at submit, because order
 * creation can only reference parts that already exist. That's also why the catalogue shows stock:
 * the server refuses to attach more of a part than it has.
 */
@Composable
fun PartPickerSheet(
    catalogue: Resource<List<SparePart>>,
    onAdd: (PartDraft) -> Unit,
    onDismiss: () -> Unit,
    newLocalId: () -> String,
) {
    var source by remember { mutableStateOf(PartSource.CATALOGUE) }

    BottomSheet(onDismissRequest = onDismiss) { hide ->
        Spacer(Modifier.height(20.dp))

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(Res.string.add_order_part_title),
                style = CashierServiceTheme.typography.h3,
                color = CashierServiceTheme.colors.primaryText
            )

            Spacer(Modifier.height(14.dp))

            // Resolved here rather than in the label lambda — that one isn't composable.
            val catalogueLabel = stringResource(Res.string.add_order_part_catalogue)
            val manualLabel = stringResource(Res.string.add_order_part_manual)

            SegmentedSelector(
                options = PartSource.entries,
                selected = source,
                onSelect = { source = it },
                label = {
                    when (it) {
                        PartSource.CATALOGUE -> catalogueLabel
                        PartSource.MANUAL -> manualLabel
                    }
                }
            )

            Spacer(Modifier.height(14.dp))

            when (source) {
                PartSource.CATALOGUE -> CatalogueList(
                    catalogue = catalogue,
                    onPick = { part ->
                        hide()
                        onAdd(
                            PartDraft(
                                localId = newLocalId(),
                                sparePartId = part.id,
                                name = part.name,
                                unitPrice = part.sellPrice,
                                qty = 1,
                            )
                        )
                    }
                )

                PartSource.MANUAL -> ManualPartForm(
                    onAdd = { name, price, qty ->
                        hide()
                        onAdd(
                            PartDraft(
                                localId = newLocalId(),
                                sparePartId = null,
                                name = name,
                                unitPrice = price,
                                qty = qty,
                            )
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun CatalogueList(
    catalogue: Resource<List<SparePart>>,
    onPick: (SparePart) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val parts = catalogue.data.orEmpty()
    val results = remember(parts, query) {
        if (query.isBlank()) parts
        else parts.filter {
            it.name.contains(query.trim(), ignoreCase = true) ||
                    it.sku.contains(query.trim(), ignoreCase = true)
        }
    }

    SearchField(
        value = query,
        onValueChange = { query = it },
        placeholder = stringResource(Res.string.add_order_part_search),
    )

    Spacer(Modifier.height(10.dp))

    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (results.isEmpty()) {
            Text(
                text = catalogue.message ?: stringResource(Res.string.add_order_part_empty),
                modifier = Modifier.padding(vertical = 20.dp),
                style = CashierServiceTheme.typography.text2,
                color = CashierServiceTheme.colors.secondaryText
            )
            return@Column
        }

        results.forEach { part ->
            val inStock = part.stock > 0

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(CashierServiceTheme.shapes.roundedCornerLg)
                    .background(CashierServiceTheme.colors.tileBackground.copy(alpha = 0.05f))
                    .clickable(enabled = inStock, role = Role.Button) { onPick(part) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = part.name,
                        style = CashierServiceTheme.typography.text1,
                        color = if (inStock) CashierServiceTheme.colors.primaryText
                        else CashierServiceTheme.colors.noteText,
                        maxLines = 1
                    )
                    Text(
                        text = if (inStock) "${part.sku} · ${part.stock} in stock"
                        else stringResource(Res.string.add_order_part_out_of_stock),
                        style = CashierServiceTheme.typography.text2,
                        color = if (inStock) CashierServiceTheme.colors.secondaryText
                        else CashierServiceTheme.colors.dangerText,
                        maxLines = 1
                    )
                }

                Text(
                    text = formatRupiah(part.sellPrice),
                    style = CashierServiceTheme.typography.h4,
                    color = if (inStock) CashierServiceTheme.colors.primaryText
                    else CashierServiceTheme.colors.noteText,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ManualPartForm(onAdd: (name: String, price: Long, qty: Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("1") }

    val priceValue = price.toLongOrNull()
    val qtyValue = qty.toIntOrNull() ?: 0
    val canAdd = name.isNotBlank() && priceValue != null && qtyValue > 0

    Column {
        TextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.add_order_part_name),
            singleLine = true,
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextField(
                value = price,
                // Digits only: a price is a whole number of rupiah.
                onValueChange = { value -> price = value.filter { it.isDigit() } },
                modifier = Modifier.weight(2f),
                label = stringResource(Res.string.add_order_part_price),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            TextField(
                value = qty,
                onValueChange = { value -> qty = value.filter { it.isDigit() }.take(2) },
                modifier = Modifier.weight(1f),
                label = stringResource(Res.string.add_order_part_qty),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            label = stringResource(Res.string.add_order_part_add),
            onClick = { onAdd(name.trim(), priceValue ?: 0, qtyValue) },
            modifier = Modifier.fillMaxWidth(),
            primary = true,
            enabled = canAdd,
        )
    }
}
