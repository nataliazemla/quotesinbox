package com.example.quotesinbox.core.data

import com.example.quotesinbox.core.model.Quote

interface QuotesRepository {
    suspend fun fetchQuotes(): List<Quote>
    suspend fun toggleFavourite(id: String): List<Quote>
}