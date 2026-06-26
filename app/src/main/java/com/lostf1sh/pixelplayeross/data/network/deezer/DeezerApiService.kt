package com.lostf1sh.pixelplayeross.data.network.deezer

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for Deezer API.
 * Used primarily for fetching artist artwork.
 */
interface DeezerApiService {

    /**
     * Search for an artist by name.
     * @param query Artist name to search for
     * @param limit Maximum number of results to return
     * @return Search response containing list of matching artists
     */
    @GET("search/artist")
    suspend fun searchArtist(
        @Query("q") query: String,
        @Query("limit") limit: Int = 1
    ): DeezerSearchResponse

    @GET("platform/generic/user/{user_id}/multiflow/default")
    suspend fun getMultiFlow(
        @retrofit2.http.Path("user_id") userId: Long,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerMultiFlowResponse

    @GET("platform/gcast/user/{user_id}/recommended-playlists")
    suspend fun getRecommendedPlaylists(
        @retrofit2.http.Path("user_id") userId: Long,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerRecommendedPlaylistsResponse

    @GET("platform/gcast/user/{user_id}/playlists?include=lyrics")
    suspend fun getUserPlaylists(
        @retrofit2.http.Path("user_id") userId: Long,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerRecommendedPlaylistsResponse

    @GET("platform/gcast/track/{track_id}/streamUrls")
    suspend fun getStreamUrls(
        @retrofit2.http.Path("track_id") trackId: Long,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerStreamUrlsResponse

    @GET("user/me")
    suspend fun getUserMe(
        @Query("access_token") auth: String
    ): com.lostf1sh.pixelplayeross.data.network.deezer.DeezerUserMeResponse

    @GET("platform/generic/user/{user_id}/multiflow")
    suspend fun getMultiFlowConfigs(
        @retrofit2.http.Path("user_id") userId: Long,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerMultiFlowConfigsResponse

    @GET
    suspend fun getMultiFlowTracks(
        @retrofit2.http.Url url: String,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerMultiFlowResponse

    @GET("platform/generic/playlist/{playlist_id}")
    suspend fun getPlaylistTracks(
        @retrofit2.http.Path("playlist_id") playlistId: String,
        @retrofit2.http.Header("authorization") auth: String,
        @retrofit2.http.Query("page") page: Int = 1,
        @retrofit2.http.Query("nb_items") limit: Int = 50,
        @retrofit2.http.Query("include") include: String = "lyrics"
    ): DeezerPlaylistDetailResponse

    @GET("platform/generic/lyrics/{track_id}")
    suspend fun getLyrics(
        @retrofit2.http.Path("track_id") trackId: String,
        @retrofit2.http.Header("authorization") auth: String
    ): DeezerLyricsResponse

    @GET("user/{user_id}/tracks")
    suspend fun getLovedTracks(
        @retrofit2.http.Path("user_id") userId: Long,
        @retrofit2.http.Header("authorization") auth: String,
        @retrofit2.http.Query("limit") limit: Int,
        @retrofit2.http.Query("index") index: Int
    ): DeezerPlaylistDetailResponse

    @retrofit2.http.POST("user/me/tracks")
    suspend fun addTrackToFavorites(
        @retrofit2.http.Query("access_token") token: String,
        @retrofit2.http.Query("track_id") trackId: Long
    ): retrofit2.Response<Boolean>

    @retrofit2.http.DELETE("user/me/tracks")
    suspend fun removeTrackFromFavorites(
        @retrofit2.http.Query("access_token") token: String,
        @retrofit2.http.Query("track_id") trackId: Long
    ): retrofit2.Response<Boolean>
    @retrofit2.http.POST("platform/generic/playlist/{playlist_id}/like")
    suspend fun likePlaylist(
        @retrofit2.http.Path("playlist_id") playlistId: String,
        @retrofit2.http.Header("authorization") auth: String
    ): retrofit2.Response<Unit>

    @retrofit2.http.DELETE("platform/generic/playlist/{playlist_id}/like")
    suspend fun unlikePlaylist(
        @retrofit2.http.Path("playlist_id") playlistId: String,
        @retrofit2.http.Header("authorization") auth: String
    ): retrofit2.Response<Unit>
}
