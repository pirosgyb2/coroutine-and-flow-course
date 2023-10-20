package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.utils.runSuspendCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.lang.Exception

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        val numberOfRetries = 2
        val timeout = 1000L

        val oreoDeferred = viewModelScope.async {
            retryWithTimeout(timeout, numberOfRetries) {
                api.getAndroidVersionFeatures(27)
            }
        }

        val pieDeferred = viewModelScope.async {
            retryWithTimeout(timeout, numberOfRetries) {
                api.getAndroidVersionFeatures(28)
            }
        }

        viewModelScope.launch {
            val versionFeatures = awaitAll(
                oreoDeferred,
                pieDeferred
            )

            uiState.value = UiState.Success(versionFeatures.filterNotNull())
        }
    }

    private suspend fun <T> retryWithTimeout(
        timeout: Long,
        retries: Int,
        block: suspend () -> T?
    ): T? = retry(numberOfRetries = retries) {
        withTimeoutOrNull(timeout) { block() }
    }


    private suspend fun <T> retry(
        numberOfRetries: Int,
        delayBetweenRetries: Long = 100L,
        block: suspend () -> T
    ): T {
        val id = block.hashCode()
        var attempt = 0

        repeat(numberOfRetries) {
            try {
                Timber.e("#$attempt Attempt of $id")
                return block()
            } catch (e: Exception) {
                Timber.e("$id got an error: $e")
            }

            attempt += 1
            delay(delayBetweenRetries)
        }
        return block()
    }

}
