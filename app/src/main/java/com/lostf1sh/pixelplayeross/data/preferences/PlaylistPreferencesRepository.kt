package com.lostf1sh.pixelplayeross.data.preferences

import com.lostf1sh.pixelplayeross.data.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistPreferencesRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    val userPlaylistsFlow: Flow<List<Playlist>> = flowOf(emptyList())
    val playlistSongOrderModesFlow: Flow<Map<String, String>> = userPreferencesRepository.playlistSongOrderModesFlow
    val playlistsSortOptionFlow: Flow<String> = userPreferencesRepository.playlistsSortOptionFlow

    suspend fun createPlaylist(
        name: String,
        songIds: List<String> = emptyList(),
        isQueueGenerated: Boolean = false,
        coverImageUri: String? = null,
        coverColorArgb: Int? = null,
        coverIconName: String? = null,
        coverShapeType: String? = null,
        coverShapeDetail1: Float? = null,
        coverShapeDetail2: Float? = null,
        coverShapeDetail3: Float? = null,
        coverShapeDetail4: Float? = null,
        customId: String? = null,
        source: String = "LOCAL"
    ): Playlist {
        throw NotImplementedError("Stubbed for Deezer")
    }

    suspend fun addSongsToPlaylist(playlistId: String, songIds: List<String>) {}
    suspend fun removeSongsFromPlaylist(playlistId: String, songIds: List<String>) {}
    suspend fun renamePlaylist(playlistId: String, newName: String) {}
    suspend fun deletePlaylist(playlistId: String) {}
    suspend fun updatePlaylistOrder(playlistId: String, newOrder: List<String>) {}
    suspend fun getPlaylistsOnce(): List<Playlist> = emptyList()
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {}
    suspend fun reorderSongsInPlaylist(playlistId: String, songIds: List<String>) {}
    suspend fun removeSongFromAllPlaylists(songId: String) {}
    suspend fun setPlaylistSongOrderMode(playlistId: String, mode: String) {}
    suspend fun updatePlaylist(playlist: Playlist) {}
    suspend fun addOrRemoveSongFromPlaylists(songId: String, playlistIds: List<String>): List<String> = emptyList()
    suspend fun setPlaylistsSortOption(sortOption: String) {}
    suspend fun updatePlaylistCover(
        playlistId: String,
        coverImageUri: String? = null,
        coverColorArgb: Int? = null,
        coverIconName: String? = null,
        coverShapeType: String? = null,
        d1: Float? = null,
        d2: Float? = null,
        d3: Float? = null,
        d4: Float? = null
    ) {}
    suspend fun createSmartPlaylist(
        name: String,
        smartRuleKey: String,
        coverColorArgb: Int? = null,
        coverIconName: String? = null,
        coverShapeType: String? = null,
        d1: Float? = null,
        d2: Float? = null,
        d3: Float? = null,
        d4: Float? = null
    ): Playlist {
        throw NotImplementedError("Stubbed")
    }
}
