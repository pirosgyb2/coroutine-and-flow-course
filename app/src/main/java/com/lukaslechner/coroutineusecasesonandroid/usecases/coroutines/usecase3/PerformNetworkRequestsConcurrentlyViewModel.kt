package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import com.lukaslechner.coroutineusecasesonandroid.utils.runSuspendCatching
import kotlinx.coroutines.launch
import okhttp3.internal.Version
import timber.log.Timber

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        viewModelScope.launch {
            runSuspendCatching {
                val recentVersions = mockApi
                    .getRecentAndroidVersions()
                    .sortedBy { it.apiLevel }

                val list = buildList {
                    recentVersions.forEach {version ->
                        add(mockApi.getAndroidVersionFeatures(version.apiLevel))
                    }
                }

                uiState.value = UiState.Success(list)
            }.onFailure {
                Timber.e(it.message)
                uiState.value = UiState.Error("Something went wrong")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {

    }
}
