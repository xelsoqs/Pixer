package com.lostf1sh.pixelplayeross.data.repository

import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerApiService
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerLyricsResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerMultiFlowResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerPlaylistDetailResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerRecommendedPlaylistsResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerStreamUrlsResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerTrackInfoResponse
import com.lostf1sh.pixelplayeross.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeezerRepository @Inject constructor(
    private val deezerApiService: DeezerApiService,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private suspend fun getAuthHeader(): String? {
        val token = userPreferencesRepository.deezerAccessTokenFlow.firstOrNull()
        return if (!token.isNullOrEmpty()) "Bearer $token" else null
    }

    private suspend fun getRawToken(): String? {
        val token = userPreferencesRepository.deezerAccessTokenFlow.firstOrNull()
        return if (!token.isNullOrEmpty()) token else null
    }

    private suspend fun getUserId(): Long? {
        return userPreferencesRepository.deezerUserIdFlow.firstOrNull()
    }

    suspend fun getMultiFlowConfigs(): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerMultiFlowConfigsResponse? {
        val auth = getAuthHeader() ?: return null
        val userId = getUserId() ?: return null
        return try {
            deezerApiService.getMultiFlowConfigs(userId, auth)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getMultiFlowTracks(url: String): DeezerMultiFlowResponse? {
        val auth = getAuthHeader() ?: return null
        return try {
            val res = deezerApiService.getMultiFlowTracks(url, auth)
            res
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getPlaylistTracks(playlistId: String, page: Int = 1, limit: Int = 50): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerPlaylistDetailResponse? {
        val auth = getAuthHeader() ?: return null
        return try {
            deezerApiService.getPlaylistTracks(playlistId, auth, page, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLovedTracks(nbItems: Int = 2000, page: Int = 0): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerPlaylistDetailResponse? {
        val auth = getAuthHeader() ?: return null
        val userId = getUserId() ?: return null
        return try {
            deezerApiService.getLovedTracks(userId, auth, nbItems, page)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addTrackToFavorites(trackId: Long): Boolean {
        val token = getRawToken() ?: return false
        return try {
            val res = deezerApiService.addTrackToFavorites(token, trackId)
            res.isSuccessful || res.body() == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removeTrackFromFavorites(trackId: Long): Boolean {
        val token = getRawToken() ?: return false
        return try {
            val res = deezerApiService.removeTrackFromFavorites(token, trackId)
            res.isSuccessful || res.body() == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getRecommendedPlaylists(): DeezerRecommendedPlaylistsResponse? {
        val auth = getAuthHeader() ?: return null
        val userId = getUserId() ?: return null
        return try {
            val res = deezerApiService.getRecommendedPlaylists(userId, auth)
            android.util.Log.d("DeezerRepository", "getRecommendedPlaylists size: ${res.data?.included?.size}")
            res
        } catch (e: Exception) {
            android.util.Log.e("DeezerRepository", "getRecommendedPlaylists error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserPlaylists(): DeezerRecommendedPlaylistsResponse? {
        val auth = getAuthHeader() ?: return null
        val userId = getUserId() ?: return null
        return try {
            val res = deezerApiService.getUserPlaylists(userId, auth)
            android.util.Log.d("DeezerRepository", "getUserPlaylists size: ${res.data?.included?.size}")
            res
        } catch (e: Exception) {
            android.util.Log.e("DeezerRepository", "getUserPlaylists error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun getGcastLovedTracks(page: Int = 1, limit: Int = 50): DeezerPlaylistDetailResponse? {
        val auth = getAuthHeader() ?: return null
        val userId = getUserId() ?: return null
        return try {
            val res = deezerApiService.getGcastLovedTracks(userId, auth, page, limit)
            android.util.Log.d("DeezerRepository", "getGcastLovedTracks size: ${res.data?.included?.size}")
            res
        } catch (e: Exception) {
            android.util.Log.e("DeezerRepository", "getGcastLovedTracks error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun likeTrack(trackId: Long): Boolean {
        val auth = getAuthHeader() ?: return false
        return try {
            val res = deezerApiService.likeTrack(trackId, auth)
            res.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("DeezerRepository", "likeTrack error: ${e.message}", e)
            false
        }
    }

    suspend fun unlikeTrack(trackId: Long): Boolean {
        val auth = getAuthHeader() ?: return false
        return try {
            val res = deezerApiService.unlikeTrack(trackId, auth)
            res.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("DeezerRepository", "unlikeTrack error: ${e.message}", e)
            false
        }
    }

    suspend fun getStreamUrls(trackId: Long): DeezerStreamUrlsResponse? {
        val auth = getAuthHeader() ?: return null
        return try {
            val res = deezerApiService.getStreamUrls(trackId, auth)
            res
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLyrics(trackId: String): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerLyricsResponse? {
        val auth = getAuthHeader() ?: return null
        return try {
            deezerApiService.getLyrics(trackId, auth)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserMe(): Result<com.lostf1sh.pixelplayeross.data.network.deezer.DeezerUserMeResponse> {
        val token = getRawToken() ?: return Result.failure(Exception("No access token found"))
        return try {
            val response = deezerApiService.getUserMe(token)
            if (response.name == null) {
                Result.failure(Exception("Name is null in response. This may mean the token is invalid or expired."))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            android.util.Log.e("DeezerRepository", "getUserMe error: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun likePlaylist(playlistId: String): Boolean {
        val auth = getAuthHeader() ?: return false
        return try {
            val res = deezerApiService.likePlaylist(playlistId, auth)
            res.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun unlikePlaylist(playlistId: String): Boolean {
        val auth = getAuthHeader() ?: return false
        return try {
            val res = deezerApiService.unlikePlaylist(playlistId, auth)
            res.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getTrackInfo(trackId: String): DeezerTrackInfoResponse? {
        val auth = getAuthHeader() ?: return null
        return try {
            val response = deezerApiService.getTrackInfo(trackId, auth)
            if (response.isSuccessful) {
                response.body()
            } else {
                timber.log.Timber.e("Error fetching track info: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Exception fetching track info")
            null
        }
    }

    suspend fun getAlbumInfo(albumId: Long): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerAlbumMetadataResponse? {
        val auth = getAuthHeader()
        timber.log.Timber.tag("AlbumDebug").d("DeezerRepository.getAlbumInfo($albumId), auth is null? ${auth == null}")
        if (auth == null) return null
        return try {
            val response = deezerApiService.getAlbumInfo(albumId, auth)
            timber.log.Timber.tag("AlbumDebug").d("DeezerRepository.getAlbumInfo response: $response")
            response
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Exception fetching album info for albumId=$albumId")
            null
        }
    }

    suspend fun likeAlbum(albumId: Long): Boolean {
        val auth = getAuthHeader() ?: return false
        return try {
            val response = deezerApiService.likeAlbum(albumId, auth)
            response.isSuccessful
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Exception liking album")
            false
        }
    }

    suspend fun unlikeAlbum(albumId: Long): Boolean {
        val auth = getAuthHeader() ?: return false
        return try {
            val response = deezerApiService.unlikeAlbum(albumId, auth)
            response.isSuccessful
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Exception unliking album")
            false
        }
    }

    suspend fun getGcastLovedAlbums(start: Int = 0, limit: Int = 50): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerLovedAlbumsResponse? {
        val auth = getAuthHeader() ?: return null
        val userId = getUserId() ?: return null
        return try {
            deezerApiService.getGcastLovedAlbums(userId, auth, start, limit)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Exception fetching loved albums")
            null
        }
    }
}
