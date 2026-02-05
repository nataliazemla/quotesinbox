package com.example.quotesinbox.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotesinbox.core.data.QuotesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuotesMviViewModel(
    private val repo: QuotesRepository
) : ViewModel() {

    private val intents = MutableSharedFlow<QuotesIntent>(extraBufferCapacity = 16)

    private val _state = MutableStateFlow<QuotesState>(QuotesState.Loading)
    val state: StateFlow<QuotesState> = _state.asStateFlow()

    private val _effects = Channel<QuotesEffect>(capacity = Channel.BUFFERED)
    val effects: Flow<QuotesEffect> = _effects.receiveAsFlow()

    init {
        bind()
        accept(QuotesIntent.Refresh)
    }

    fun accept(intent: QuotesIntent) {
        intents.tryEmit(intent)
    }

    private fun bind() {
        viewModelScope.launch {
            intents.collect { intent ->
                when (intent) {
                    QuotesIntent.Refresh -> handleRefresh()
                    is QuotesIntent.ToggleFavorite -> handleToggleFavorite(intent.id)
                }
            }
        }
    }

    private suspend fun handleRefresh() {
        emitResult(QuotesResult.LoadingStarted)
        try {
            val items = repo.fetchQuotes()
            emitResult(QuotesResult.QuotesLoaded(items))
            _effects.trySend(QuotesEffect.ShowSnackbar("Loaded"))
        } catch (t: Throwable) {
            emitResult(QuotesResult.LoadFailed(t.message ?: "unknown"))
            _effects.trySend(QuotesEffect.ShowSnackbar("Load failed: ${t.message ?: "unknown"}"))
        }
    }

    private suspend fun handleToggleFavorite(id: String) {
        try {
            val items = repo.toggleFavourite(id)
            emitResult(QuotesResult.FavoriteUpdated(items))
        } catch (t: Throwable) {
            _effects.trySend(QuotesEffect.ShowSnackbar("Toggle failed: ${t.message ?: "unknown"}"))
        }
    }

    private fun emitResult(result: QuotesResult) {
        _state.update { current -> QuotesReducer.reduce(current, result) }
    }
}
