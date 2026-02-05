package com.example.quotesinbox.mvi

import com.example.quotesinbox.core.model.Quote

sealed interface QuotesIntent {
    data object Refresh : QuotesIntent
    data class ToggleFavorite(val id: String) : QuotesIntent
}

sealed interface QuotesResult {
    data object LoadingStarted : QuotesResult
    data class QuotesLoaded(val items: List<Quote>) : QuotesResult
    data class LoadFailed(val message: String) : QuotesResult
    data class FavoriteUpdated(val items: List<Quote>) : QuotesResult
}

sealed interface QuotesState {
    data object Loading : QuotesState
    data class Content(
        val items: List<Quote>,
        val isRefreshing: Boolean
    ) : QuotesState
}

sealed interface QuotesEffect {
    data class ShowSnackbar(val message: String) : QuotesEffect
}