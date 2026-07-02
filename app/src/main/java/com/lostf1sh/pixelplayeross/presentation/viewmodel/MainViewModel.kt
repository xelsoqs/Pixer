package com.lostf1sh.pixelplayeross.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lostf1sh.pixelplayeross.data.preferences.UserPreferencesRepository
import com.lostf1sh.pixelplayeross.data.repository.MusicRepository

import com.lostf1sh.pixelplayeross.utils.LogUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.lostf1sh.pixelplayeross.data.preferences.PlaylistPreferencesRepository

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val playlistPreferencesRepository: PlaylistPreferencesRepository
) : ViewModel() {

    val isSetupComplete: StateFlow<Boolean?> = userPreferencesRepository.initialSetupDoneFlow
        .map { it as Boolean? }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val hasCompletedInitialSync: StateFlow<Boolean> = userPreferencesRepository.lastSyncTimestampFlow
        .map { it > 0L }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true // Optimistic strategy: assume synced by default
        )



    /**
     * Emits once each time a library sync ends in failure, so the UI can surface a
     * one-shot toast (the progress flow alone silently reverts to idle on failure).
     */
    val syncFailed: Flow<Unit> = kotlinx.coroutines.flow.emptyFlow()

    /**
     * A Flow that emits `true` if the Room database has no songs.
     * Helps us know whether this is the first time the app is opened.
     */
    val isLibraryEmpty: StateFlow<Boolean> = musicRepository
        .getAudioFiles()
        .map { it.isEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /**
     * Function to start syncing the music library.
     * Should be called after permissions have been granted.
     */
    fun startSync() {
        android.util.Log.d("SyncDebug", "MainViewModel startSync() called")
        viewModelScope.launch {
            playlistPreferencesRepository.isSyncingLibraryFlow.value = true
            android.util.Log.d("SyncDebug", "Calling playlistPreferencesRepository.syncLovedTracks()")
            playlistPreferencesRepository.syncLovedTracks()
            
            android.util.Log.d("SyncDebug", "Calling playlistPreferencesRepository.syncLovedAlbums()")
            playlistPreferencesRepository.syncLovedAlbums()
            
            android.util.Log.d("SyncDebug", "Calling playlistPreferencesRepository.syncLovedArtists()")
            playlistPreferencesRepository.syncLovedArtists()

            android.util.Log.d("SyncDebug", "Calling playlistPreferencesRepository.syncUserPlaylists()")
            playlistPreferencesRepository.syncUserPlaylists()
            
            android.util.Log.d("SyncDebug", "Finished syncing playlists, tracks, albums, and artists")
            playlistPreferencesRepository.isSyncingLibraryFlow.value = false
        }
    }
    fun retrySync() {
        startSync()
    }
}
