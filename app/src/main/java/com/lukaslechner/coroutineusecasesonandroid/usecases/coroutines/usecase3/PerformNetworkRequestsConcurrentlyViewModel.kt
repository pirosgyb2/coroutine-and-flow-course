package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.utils.runSuspendCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            runSuspendCatching {
                val oreoFeatures = mockApi.getAndroidVersionFeatures(27)
                val pieFeatures = mockApi.getAndroidVersionFeatures(28)
                val android10Features = mockApi.getAndroidVersionFeatures(29)

                val list = listOf(
                    oreoFeatures,
                    pieFeatures,
                    android10Features,
                )

                uiState.value = UiState.Success(list)
            }.onFailure {
                Timber.e(it.message)
                uiState.value = UiState.Error("Something went wrong")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        concurrentSolution2()
    }

    private fun concurrentSolution1() {
        uiState.value = UiState.Loading
        val oreoDiffered = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(27)
        }
        val pieDiffered = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(28)
        }
        val a10Differed = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(29)
        }

        viewModelScope.launch {
            runSuspendCatching {
                val oreoFeatures = oreoDiffered.await()
                val pieFeatures = pieDiffered.await()
                val a10Features = a10Differed.await()

                val versionFeatures = listOf(
                    oreoFeatures,
                    pieFeatures,
                    a10Features,
                )
                uiState.value = UiState.Success(versionFeatures)
            }.onFailure {
                uiState.value = UiState.Error("Network Request failed")
            }
        }
    }

    private fun concurrentSolution2() {
        uiState.value = UiState.Loading
        val oreoDiffered = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(27)
        }
        val pieDiffered = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(28)
        }
        val a10Differed = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(29)
        }

        viewModelScope.launch {
            runSuspendCatching {
                val versionFeatures = awaitAll(
                    oreoDiffered,
                    pieDiffered,
                    a10Differed,
                )
                uiState.value = UiState.Success(versionFeatures)
            }.onFailure {
                uiState.value = UiState.Error("Network Request failed")
            }

        }
    }
}
