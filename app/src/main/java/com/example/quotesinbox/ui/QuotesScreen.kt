package com.example.quotesinbox.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quotesinbox.core.model.Quote

@Composable
fun QuotesScreen(
    title: String,
    isLoading: Boolean,
    isRefreshing: Boolean,
    items: List<Quote>,
    onRefresh: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onRefresh,
                enabled = !isLoading && !isRefreshing
            ) { Text("Refresh") }

            if (isLoading || isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Text("Loading…")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(items, key = { it.id }) { q ->
                    QuoteRow(quote = q, onToggleFavorite = onToggleFavorite)
                }
            }
        }
    }
}

@Composable
private fun QuoteRow(
    quote: Quote,
    onToggleFavorite: (String) -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onToggleFavorite(quote.id) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(quote.text, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Text(if (quote.isFavourite) "★" else "☆")
        }
    }
}