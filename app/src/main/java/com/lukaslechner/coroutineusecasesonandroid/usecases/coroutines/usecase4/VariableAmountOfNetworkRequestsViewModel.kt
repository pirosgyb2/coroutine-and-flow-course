package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.utils.runSuspendCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class VariableAmountOfNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            runSuspendCatching {
                val recentVersions = mockApi.getRecentAndroidVersions()
                val versionFeatures = recentVersions.map{ androidVersion ->
                    mockApi.getAndroidVersionFeatures(androidVersion.apiLevel)
                }
                uiState.value = UiState.Success(versionFeatures)
            }.onFailure {
                uiState.value = UiState.Error("Error")
            }

        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            val recentVersions = mockApi.getRecentAndroidVersions()
            val versionFeatures = recentVersions.map {version ->
                async { mockApi.getAndroidVersionFeatures(version.apiLevel) }
            }.awaitAll()

            uiState.value = UiState.Success(versionFeatures)
        }

    }
}