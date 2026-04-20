package com.app.admin.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.admin.data.model.CryptoPayout
import com.app.admin.data.model.GiftCardPayout
import com.app.admin.data.model.Payout
import com.app.admin.ui.theme.MyApplicationTheme
import com.app.admin.ui.viewmodel.PayoutsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PayoutsScreen() {
    val viewmodel = PayoutsViewModel()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Crypto", "Gift Cards")
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Combine payouts based on the selected tab
    val filteredPayouts = if (selectedTabIndex == 0) {
        filterCryptoPayouts(viewmodel.cryptoPayouts.value, searchQuery.text).map { Payout.Crypto(it) }
    } else {
        filterGiftCardPayouts(viewmodel.giftCardPayouts.value, searchQuery.text).map { Payout.GiftCard(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search payouts...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.LightGray, RoundedCornerShape(24.dp))
            )
        }

        // Styled Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (selectedTabIndex == index) Color.White else Color.Gray
                        ),
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Payout list
        LazyColumn {
            if (filteredPayouts.isEmpty()) {
                item {
                    Text(
                        "No payouts available.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                items(filteredPayouts) { payout ->
                    when (payout) {
                        is Payout.Crypto -> PayoutCard(payout = payout.payout)
                        is Payout.GiftCard -> GiftCardPayoutCard(payout = payout.payout)
                    }
                }
            }
        }
    }
}


/**
 * Filter crypto payouts based on the search query.
 *
 * @param payouts List of crypto payouts to filter.
 * @param query The search query.
 * @return Filtered list of crypto payouts.
 */
fun filterCryptoPayouts(payouts: List<CryptoPayout>, query: String): List<CryptoPayout> {
    return payouts.filter { payout ->
        payout.coin?.contains(query, ignoreCase = true) == true ||
                payout.address?.contains(query, ignoreCase = true) == true
    }
}

/**
 * Filter gift card payouts based on the search query.
 *
 * @param payouts List of gift card payouts to filter.
 * @param query The search query.
 * @return Filtered list of gift card payouts.
 */
fun filterGiftCardPayouts(payouts: List<GiftCardPayout>, query: String): List<GiftCardPayout> {
    return payouts.filter { payout ->
        payout.email?.contains(query, ignoreCase = true) == true ||
                payout.cardType?.contains(query, ignoreCase = true) == true
    }
}

/**
 * PayoutCard displays information about a single crypto payout.
 *
 * @param payout The crypto payout to display.
 */
@Composable
fun PayoutCard(payout: CryptoPayout) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Crypto payout details
            Column {
                Text(text = "Coin: ${payout.coin}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Amount: ${payout.amount}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Address: ${payout.address}", style = MaterialTheme.typography.bodySmall)
                payout.added_date?.let {
                    Text(text = "Date: ${formatDate(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/**
 * GiftCardPayoutCard displays information about a single gift card payout.
 *
 * @param payout The gift card payout to display.
 */
@Composable
fun GiftCardPayoutCard(payout: GiftCardPayout) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Gift card payout details
            Column {
                Text(text = "Email: ${payout.email}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Amount: ${payout.ptsAmount} Points", style = MaterialTheme.typography.bodySmall)
                Text(text = "Card Type: ${payout.cardType}", style = MaterialTheme.typography.bodySmall)
                payout.added_date?.let {
                    Text(text = "Date: ${formatDate(it)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/**
 * Helper function to format date.
 *
 * @param date Date object to format.
 * @return Formatted date string.
 */
fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}
