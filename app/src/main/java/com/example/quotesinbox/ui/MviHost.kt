package com.example.quotesinbox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quotesinbox.core.data.FakeQuotesRepository
import com.example.quotesinbox.mvi.QuotesEffect
import com.example.quotesinbox.mvi.QuotesIntent
import com.example.quotesinbox.mvi.QuotesMviViewModel
import com.example.quotesinbox.mvi.QuotesState

@Composable
fun MviHost() {
    val repo = remember { FakeQuotesRepository() }
    val vm: QuotesMviViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return QuotesMviViewModel(repo) as T
            }
        }
    )

    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is QuotesEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { inner ->
        Box(Modifier.padding(inner)) {
            when (state) {
                QuotesState.Loading -> {
                    QuotesScreen(
                        title = "MVI",
                        isLoading = true,
                        isRefreshing = false,
                        items = emptyList(),
                        onRefresh = { vm.accept(QuotesIntent.Refresh) },
                        onToggleFavorite = { id ->
                            vm.accept(QuotesIntent.ToggleFavorite(id))
                        }
                    )
                }
                is QuotesState.Content -> {
                    val s = state as QuotesState.Content
                    QuotesScreen(
                        title = "MVI",
                        isLoading = false,
                        isRefreshing = s.isRefreshing,
                        items = s.items,
                        onRefresh = { vm.accept(QuotesIntent.Refresh) },
                        onToggleFavorite = { id ->
                            vm.accept(QuotesIntent.ToggleFavorite(id))
                        }
                    )
                }
            }
        }
    }
}
