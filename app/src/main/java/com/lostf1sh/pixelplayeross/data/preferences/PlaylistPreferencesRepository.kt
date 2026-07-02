package com.lostf1sh.pixelplayeross.data.preferences

import com.lostf1sh.pixelplayeross.data.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

import com.lostf1sh.pixelplayeross.data.database.LocalPlaylistDao
import com.lostf1sh.pixelplayeross.data.database.PlaylistEntity
import com.lostf1sh.pixelplayeross.data.database.toPlaylist
import com.lostf1sh.pixelplayeross.data.repository.DeezerRepository
import kotlinx.coroutines.flow.map

import com.lostf1sh.pixelplayeross.data.database.MusicDao
import com.lostf1sh.pixelplayeross.data.database.FavoritesDao
import com.lostf1sh.pixelplayeross.data.database.SongEntity
import com.lostf1sh.pixelplayeross.data.database.FavoritesEntity
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerPlaylistDetailResponse

@Singleton
class PlaylistPreferencesRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val localPlaylistDao: LocalPlaylistDao,
    private val deezerRepository: DeezerRepository,
    private val musicDao: MusicDao,
    private val favoritesDao: FavoritesDao
) {
    val userPlaylistsFlow: Flow<List<Playlist>> = localPlaylistDao.observePlaylistsWithSongs().map { entities ->
        entities.map { it.playlist.toPlaylist(it.songs.map { s -> s.songId }) }
    }
    val playlistSongOrderModesFlow: Flow<Map<String, String>> = userPreferencesRepository.playlistSongOrderModesFlow
    val playlistsSortOptionFlow: Flow<String> = userPreferencesRepository.playlistsSortOptionFlow

    suspend fun syncUserPlaylists() {
        try {
            val response = deezerRepository.getUserPlaylists()
            val deezerPlaylists = response?.data?.included ?: return
            
            val entities = deezerPlaylists.map { deezerPlaylist ->
                PlaylistEntity(
                    id = "deezer_${deezerPlaylist.id}",
                    name = deezerPlaylist.attributes?.name ?: "Unknown Playlist",
                    coverImageUri = deezerPlaylist.attributes?.image?.medium,
                    source = "DEEZER",
                    nbTracks = deezerPlaylist.attributes?.nbTracks,
                    fans = deezerPlaylist.attributes?.fans,
                    isPublic = deezerPlaylist.attributes?.isPublic,
                    creatorName = deezerPlaylist.attributes?.creator?.name
                )
            }
            
            val newIds = entities.map { it.id }.toSet()
            val existingDeezerIds = localPlaylistDao.getPlaylistIdsBySource("DEEZER")
            
            entities.forEach { entity ->
                localPlaylistDao.upsertPlaylist(entity)
            }
            
            existingDeezerIds.forEach { existingId ->
                if (!newIds.contains(existingId)) {
                    localPlaylistDao.deletePlaylist(existingId)
                    localPlaylistDao.clearPlaylistSongs(existingId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncLovedTracks() {
        android.util.Log.d("SyncDebug", "syncLovedTracks() started")
        try {
            var currentPage = 1
            val limit = 50
            val allLovedSongs = mutableListOf<SongEntity>()
            val allFavorites = mutableListOf<FavoritesEntity>()

            while (true) {
                android.util.Log.d("SyncDebug", "syncLovedTracks() fetching page $currentPage")
                val response = deezerRepository.getGcastLovedTracks(page = currentPage, limit = limit)
                val deezerTracks = response?.data?.included
                
                android.util.Log.d("SyncDebug", "syncLovedTracks() response tracks: ${deezerTracks?.size}")

                if (deezerTracks.isNullOrEmpty()) {
                    android.util.Log.d("SyncDebug", "syncLovedTracks() no more tracks, breaking loop")
                    break
                }

                deezerTracks.forEach { track ->
                    val trackIdStr = track.id
                    val songId = trackIdStr.toLongOrNull() ?: return@forEach
                    val song = SongEntity(
                        id = songId,
                        title = track.attributes?.title ?: "Unknown Title",
                        artistName = track.attributes?.artistName ?: "Unknown Artist",
                        artistId = 0L,
                        albumName = track.attributes?.albumName ?: "Unknown Album",
                        albumId = 0L,
                        contentUriString = "deezer://track/$songId",
                        albumArtUriString = track.attributes?.image?.medium ?: track.attributes?.image?.large,
                        duration = (track.attributes?.duration ?: 0) * 1000L,
                        genre = null,
                        trackNumber = 0,
                        discNumber = 0,
                        filePath = "",
                        parentDirectoryPath = "",
                        sourceType = com.lostf1sh.pixelplayeross.data.database.SourceType.DEEZER,
                    )
                    allLovedSongs.add(song)
                    allFavorites.add(FavoritesEntity(songId = songId, isFavorite = true, timestamp = System.currentTimeMillis() - (allFavorites.size * 1000L)))
                }
                
                if (deezerTracks.size < limit) {
                    break
                }
                currentPage++
            }

            android.util.Log.d("SyncDebug", "syncLovedTracks() fetching done. Total: ${allLovedSongs.size}")
            if (allLovedSongs.isNotEmpty()) {
                android.util.Log.d("SyncDebug", "syncLovedTracks() inserting into database...")
                
                // Ensure foreign key dependencies exist
                musicDao.insertArtistsIgnoreConflicts(listOf(
                    com.lostf1sh.pixelplayeross.data.database.ArtistEntity(
                        id = 0L,
                        name = "Unknown Artist",
                        trackCount = 0
                    )
                ))
                musicDao.insertAlbumsIgnoreConflicts(listOf(
                    com.lostf1sh.pixelplayeross.data.database.AlbumEntity(
                        id = 0L,
                        title = "Unknown Album",
                        artistName = "Unknown Artist",
                        artistId = 0L,
                        albumArtUriString = null,
                        songCount = 0,
                        dateAdded = System.currentTimeMillis(),
                        year = 0
                    )
                ))

                musicDao.insertSongsIgnoreConflicts(allLovedSongs)
                favoritesDao.insertAll(allFavorites)
                android.util.Log.d("SyncDebug", "syncLovedTracks() inserted successfully.")
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncDebug", "syncLovedTracks() error: ${e.message}", e)
            e.printStackTrace()
        }
    }

    suspend fun optimisticLikePlaylist(playlist: Playlist) {
        val entity = PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            coverImageUri = playlist.coverImageUri,
            source = playlist.source,
            nbTracks = playlist.nbTracks,
            fans = playlist.fans,
            isPublic = playlist.isPublic,
            creatorName = playlist.creatorName
        )
        localPlaylistDao.upsertPlaylist(entity)
    }

    suspend fun optimisticUnlikePlaylist(playlistId: String) {
        localPlaylistDao.deletePlaylist(playlistId)
        localPlaylistDao.clearPlaylistSongs(playlistId)
    }

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
