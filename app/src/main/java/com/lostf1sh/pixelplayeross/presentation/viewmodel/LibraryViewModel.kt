package com.lostf1sh.pixelplayeross.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryStateHolder: LibraryStateHolder,
    private val deezerRepository: com.lostf1sh.pixelplayeross.data.repository.DeezerRepository
) : ViewModel() {

    val songsPagingFlow = libraryStateHolder.songsPagingFlow.cachedIn(viewModelScope)

    val albumsPagingFlow = libraryStateHolder.albumsPagingFlow.cachedIn(viewModelScope)

    val artistsPagingFlow = libraryStateHolder.artistsPagingFlow.cachedIn(viewModelScope)

    val favoritesPagingFlow = libraryStateHolder.favoritesPagingFlow.cachedIn(viewModelScope)

    val favoriteSongCountFlow = libraryStateHolder.favoriteSongCountFlow

    val isLoadingLibrary = libraryStateHolder.isLoadingLibrary

    private val _deezerFlowConfigs = kotlinx.coroutines.flow.MutableStateFlow<com.lostf1sh.pixelplayeross.data.network.deezer.DeezerMultiFlowConfigsResponse?>(null)
    val deezerFlowConfigs: kotlinx.coroutines.flow.StateFlow<com.lostf1sh.pixelplayeross.data.network.deezer.DeezerMultiFlowConfigsResponse?> = _deezerFlowConfigs

    private val _deezerRecommendedPlaylists = kotlinx.coroutines.flow.MutableStateFlow<com.lostf1sh.pixelplayeross.data.network.deezer.DeezerRecommendedPlaylistsResponse?>(null)
    val deezerRecommendedPlaylists: kotlinx.coroutines.flow.StateFlow<com.lostf1sh.pixelplayeross.data.network.deezer.DeezerRecommendedPlaylistsResponse?> = _deezerRecommendedPlaylists

    init {
        viewModelScope.launch {
            val flowConfigs = deezerRepository.getMultiFlowConfigs()
            _deezerFlowConfigs.value = flowConfigs
        }
        viewModelScope.launch {
            val playlists = deezerRepository.getRecommendedPlaylists()
            _deezerRecommendedPlaylists.value = playlists
        }
    }

    suspend fun getMultiFlowTracks(url: String): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerMultiFlowResponse? {
        return deezerRepository.getMultiFlowTracks(url)
    }
}
