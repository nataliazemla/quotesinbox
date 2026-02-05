package com.example.quotesinbox.mvi

object QuotesReducer {
    fun reduce(state: QuotesState, result: QuotesResult): QuotesState {
        return when (result) {
            QuotesResult.LoadingStarted -> when (state) {
                QuotesState.Loading -> QuotesState.Loading
                is QuotesState.Content -> state.copy(isRefreshing = true)
            }

            is QuotesResult.QuotesLoaded ->
                QuotesState.Content(items = result.items, isRefreshing = false)

            is QuotesResult.LoadFailed -> when (state) {
                QuotesState.Loading -> QuotesState.Content(items = emptyList(), isRefreshing = false)
                is QuotesState.Content -> state.copy(isRefreshing = false)
            }

            is QuotesResult.FavoriteUpdated -> when (state) {
                QuotesState.Loading -> QuotesState.Content(items = result.items, isRefreshing = false)
                is QuotesState.Content -> state.copy(items = result.items)
            }
        }
    }
}