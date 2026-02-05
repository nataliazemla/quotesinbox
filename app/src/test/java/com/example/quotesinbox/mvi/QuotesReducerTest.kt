package com.example.quotesinbox.mvi

import com.example.quotesinbox.core.model.Quote
import org.junit.Assert.assertEquals
import org.junit.Test

class QuotesReducerTest {

    @Test
    fun `LoadingStarted from Content sets isRefreshing true`() {
        val start = QuotesState.Content(
            items = listOf(Quote("1", "x", false)),
            isRefreshing = false
        )

        val next = QuotesReducer.reduce(start, QuotesResult.LoadingStarted)

        assertEquals(
            QuotesState.Content(items = start.items, isRefreshing = true),
            next
        )
    }

    @Test
    fun `LoadFailed from Loading returns empty Content and stops refreshing`() {
        val start: QuotesState = QuotesState.Loading

        val next = QuotesReducer.reduce(start, QuotesResult.LoadFailed("err"))

        assertEquals(
            QuotesState.Content(items = emptyList(), isRefreshing = false),
            next
        )
    }
}
