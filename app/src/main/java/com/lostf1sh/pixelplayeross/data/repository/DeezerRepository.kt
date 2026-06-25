package com.lostf1sh.pixelplayeross.data.repository

import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerApiService
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerMultiFlowResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerRecommendedPlaylistsResponse
import com.lostf1sh.pixelplayeross.data.network.deezer.DeezerStreamUrlsResponse
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

    suspend fun getPlaylistTracks(playlistId: String): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerPlaylistDetailResponse? {
        val auth = getAuthHeader() ?: return null
        return try {
            deezerApiService.getPlaylistTracks(playlistId, auth)
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
}
