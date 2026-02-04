@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.quotesinbox.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quotesinbox.core.data.FakeQuotesRepository
import com.example.quotesinbox.mvvm.QuotesMvvmViewModel
import com.example.quotesinbox.mvvm.QuotesUiEffect
import com.example.quotesinbox.mvvm.QuotesUiState

@Composable
fun App() {
    var architecture by rememberSaveable { mutableStateOf(Architecture.MVVM) }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MVVM vs MVI - Quotes") },
                    actions = {
                        ArchitectureToggle(
                            architecture = architecture,
                            onArchitectureChange = { architecture = it }
                        )
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                .padding(padding)
                .fillMaxSize()
            ) {
                when (architecture) {
                    Architecture.MVVM -> MvvmHost()
                    Architecture.MVI -> Text(
                        text = "MVI not yet implemented.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchitectureToggle(
    architecture: Architecture,
    onArchitectureChange: (Architecture) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { onArchitectureChange(Architecture.MVVM) },
            label = { Text("MVVM") },
            colors = AssistChipDefaults.assistChipColors()
        )
        AssistChip(
            onClick = { onArchitectureChange(Architecture.MVI) },
            label = { Text("MVI") },
            colors = AssistChipDefaults.assistChipColors()
        )
    }
}

@Composable
private fun MvvmHost() {
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
