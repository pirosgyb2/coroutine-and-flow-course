package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.utils.runSuspendCatching
import kotlinx.coroutines.launch
import timber.log.Timber

class Perform2SequentialNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun perform2SequentialNetworkRequest() {
        viewModelScope.launch {
            runSuspendCatching {
                val recentApiLevel = mockApi
                    .getRecentAndroidVersions()
                    .maxOf { it.apiLevel }

                val features = mockApi.getAndroidVersionFeatures(recentApiLevel)
                uiState.value = UiState.Success(features)
            }.onFailure {
                Timber.e(it.message)
                uiState.value = UiState.Error("Something went wrong")
            }
        }
    }
}
