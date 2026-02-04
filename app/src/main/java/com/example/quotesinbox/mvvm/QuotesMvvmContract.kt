package com.example.quotesinbox.mvvm

import com.example.quotesinbox.core.model.Quote

sealed interface QuotesUiState {
    data object Loading : QuotesUiState
    data class Content(
        val items: List<Quote>,
        val isRefreshing: Boolean
    ) : QuotesUiState
}

sealed interface QuotesUiEffect {
    data class ShowSnackbar(val message: String) : QuotesUiEffect
}