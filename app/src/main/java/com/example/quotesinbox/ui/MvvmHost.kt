package com.example.quotesinbox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quotesinbox.core.data.FakeQuotesRepository
import com.example.quotesinbox.mvvm.QuotesMvvmViewModel
import com.example.quotesinbox.mvvm.QuotesUiEffect
import com.example.quotesinbox.mvvm.QuotesUiState

@Composable
fun MvvmHost() {
    val repo = remember { FakeQuotesRepository() }
    val vm: QuotesMvvmViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return QuotesMvvmViewModel(repo) as T
            }
        }
    )

    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // One-off effects -> snackbar
    LaunchedEffect(Unit) {
        vm.effects.collect { effect ->
            when (effect) {
                is QuotesUiEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Box(Modifier.padding(inner)) {
            when (state) {
                is QuotesUiState.Loading -> {
                    QuotesScreen(
                        title = "MVVM",
                        isLoading = true,
                        isRefreshing = false,
                        items = emptyList(),
                        onRefresh = vm::onRefreshClicked,
                        onToggleFavorite = vm::onToggleFavorite
                    )
                }
                is QuotesUiState.Content -> {
                    val s = state as QuotesUiState.Content
                    QuotesScreen(
                        title = "MVVM",
                        isLoading = false,
                        isRefreshing = s.isRefreshing,
                        items = s.items,
                        onRefresh = vm::onRefreshClicked,
                        onToggleFavorite = vm::onToggleFavorite
                    )
                }
            }
        }
    }
}
