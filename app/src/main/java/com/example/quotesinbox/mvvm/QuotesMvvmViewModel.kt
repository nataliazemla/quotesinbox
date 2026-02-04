package com.example.quotesinbox.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotesinbox.core.data.QuotesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuotesMvvmViewModel(
    private val repo: QuotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<QuotesUiState>(QuotesUiState.Loading)
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<QuotesUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    init {
        refresh()
    }

    fun onRefreshClicked() = refresh()

    fun onToggleFavorite(id: String) {
        viewModelScope.launch {
            try {
                val updated = repo.toggleFavourite(id)
                _state.update { current ->
                    when (current) {
                        is QuotesUiState.Content -> current.copy(items = updated)
                        QuotesUiState.Loading -> QuotesUiState.Content(items = updated, isRefreshing = false)
                    }
                }
            } catch (t: Throwable) {
                _effects.tryEmit(QuotesUiEffect.ShowSnackbar("Toggle failed: ${t.message ?: "unknown"}"))
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val prev = _state.value
            _state.value = when (prev) {
                is QuotesUiState.Content -> prev.copy(isRefreshing = true)
                QuotesUiState.Loading -> QuotesUiState.Loading
            }

            try {
                val items = repo.fetchQuotes()
                _state.value = QuotesUiState.Content(items = items, isRefreshing = false)
                _effects.tryEmit(QuotesUiEffect.ShowSnackbar("Loaded"))
            } catch (t: Throwable) {
                _state.value = when (prev) {
                    is QuotesUiState.Content -> prev.copy(isRefreshing = false)
                    QuotesUiState.Loading -> QuotesUiState.Content(items = emptyList(), isRefreshing = false)
                }
                _effects.tryEmit(QuotesUiEffect.ShowSnackbar("Load failed: ${t.message ?: "unknown"}"))
            }
        }
    }
}