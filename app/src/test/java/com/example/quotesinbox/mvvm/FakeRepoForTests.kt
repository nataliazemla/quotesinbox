package com.example.quotesinbox.mvvm

import com.example.quotesinbox.core.data.QuotesRepository
import com.example.quotesinbox.core.model.Quote

class FakeRepoForTests(
    private var quotes: List<Quote> = emptyList(),
    private var fetchError: Throwable? = null
) : QuotesRepository {

    override suspend fun fetchQuotes(): List<Quote> {
        fetchError?.let { throw it }
        return quotes
    }

    override suspend fun toggleFavourite(id: String): List<Quote> {
        quotes = quotes.map { q -> if (q.id == id) q.copy(isFavourite = !q.isFavourite) else q }
        return quotes
    }

    fun setQuotes(newQuotes: List<Quote>) { quotes = newQuotes }
    fun setFetchError(t: Throwable?) { fetchError = t }
}
