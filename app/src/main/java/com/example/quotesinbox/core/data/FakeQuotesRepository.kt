package com.example.quotesinbox.core.data

import com.example.quotesinbox.core.model.Quote
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeQuotesRepository(
    private val random: Random = Random(0)
) : QuotesRepository {
    private var quotes: List<Quote> = listOf(
        Quote("1", "Simplicity is a feature.", false),
        Quote("2", "Make state explicit.", true),
        Quote("3", "Effects are not state.", false),
    )

    override suspend fun fetchQuotes(): List<Quote> {
        delay(700)
        if (random.nextInt(100) < 25) error("Fake network error")
        return quotes
    }

    override suspend fun toggleFavourites(id: String): List<Quote> {
        delay(150)
        quotes = quotes.map { q -> if (q.id == id) q.copy(isFavourite = !q.isFavourite) else q }
        return quotes
    }
}