package com.example.quotesinbox.mvvm

import com.example.quotesinbox.core.model.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuotesMvvmViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers refresh and emits Content on success`() = runTest {
        val repo = FakeRepoForTests(
            quotes = listOf(Quote("1", "hello", false))
        )

        val vm = QuotesMvvmViewModel(repo)

        // init() odpala coroutine -> przepchnij scheduler
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state is QuotesUiState.Content)
    }

    @Test
    fun `refresh failure emits snackbar effect`() = runTest {
        val repo = FakeRepoForTests(
            quotes = emptyList(),
            fetchError = IllegalStateException("boom")
        )
        val vm = QuotesMvvmViewModel(repo)

        testDispatcher.scheduler.advanceUntilIdle()

        // złap pierwszy efekt
        val effect = vm.effects.first()
        assertTrue(effect is QuotesUiEffect.ShowSnackbar)
    }
}
