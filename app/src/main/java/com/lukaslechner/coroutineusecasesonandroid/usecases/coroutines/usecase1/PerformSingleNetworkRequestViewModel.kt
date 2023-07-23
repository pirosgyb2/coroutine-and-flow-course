package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch
import timber.log.Timber

class PerformSingleNetworkRequestViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performSingleNetworkRequest() {
        viewModelScope.launch {
                try {
                    val recentVersions = mockApi.getRecentAndroidVersions()
                    uiState.value = UiState.Success(recentVersions)

                } catch (exception: Exception) {
                    Timber.e(exception)
                    uiState.value = UiState.Error("Network request failed!")
                }
        }
    }
}
