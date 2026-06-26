package com.lostf1sh.pixelplayeross.data.network.deezer

import com.google.gson.annotations.SerializedName

/**
 * Response from Deezer artist search API.
 */
data class DeezerSearchResponse(
    @SerializedName("data") val data: List<DeezerArtist> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

/**
 * Artist data from Deezer API.
 * Contains multiple image sizes for different use cases.
 */
data class DeezerArtist(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("picture") val picture: String? = null,
    @SerializedName("picture_small") val pictureSmall: String? = null,
    @SerializedName("picture_medium") val pictureMedium: String? = null,
    @SerializedName("picture_big") val pictureBig: String? = null,
    @SerializedName("picture_xl") val pictureXl: String? = null,
    @SerializedName("nb_album") val albumCount: Int = 0,
    @SerializedName("nb_fan") val fanCount: Int = 0
)

data class DeezerImage(
    @SerializedName("tiny") val tiny: String? = null,
    @SerializedName("small") val small: String? = null,
    @SerializedName("medium") val medium: String? = null,
    @SerializedName("large") val large: String? = null,
    @SerializedName("full") val full: String? = null
)

data class DeezerMultiFlowResponse(
    @SerializedName("data") val data: DeezerMultiFlowData? = null
)

data class DeezerMultiFlowData(
    @SerializedName("included") val included: List<DeezerTrack> = emptyList()
)

data class DeezerTrack(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String? = null,
    @SerializedName("attributes") val attributes: DeezerTrackAttributes? = null
)

data class DeezerTrackAttributes(
    @SerializedName("title") val title: String? = null,
    @SerializedName("artistName") val artistName: String? = null,
    @SerializedName("albumName") val albumName: String? = null,
    @SerializedName("duration") val duration: Int = 0,
    @SerializedName("image") val image: DeezerImage? = null,
    @SerializedName("explicit") val explicit: Boolean? = null
)

data class DeezerRecommendedPlaylistsResponse(
    @SerializedName("data") val data: DeezerRecommendedPlaylistsData? = null
)

data class DeezerRecommendedPlaylistsData(
    @SerializedName("included") val included: List<DeezerPlaylist> = emptyList()
)

data class DeezerPlaylist(
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: DeezerPlaylistAttributes? = null
)

data class DeezerPlaylistAttributes(
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: DeezerImage? = null,
    @SerializedName("nb_tracks") val nbTracks: Int? = null,
    @SerializedName("fans") val fans: Int? = null,
    @SerializedName("public") val isPublic: Boolean? = null,
    @SerializedName("creator") val creator: DeezerCreator? = null
)

data class DeezerCreator(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String? = null
)

data class DeezerStreamUrlsResponse(
    @SerializedName("data") val data: DeezerStreamUrlsData? = null
)

data class DeezerStreamUrlsData(
    @SerializedName("attributes") val attributes: DeezerStreamUrlsAttributes? = null
)

data class DeezerStreamUrlsAttributes(
    @SerializedName("url") val url: String? = null,
    @SerializedName("url_320") val url320: String? = null,
    @SerializedName("url_flac") val urlFlac: String? = null
)

data class DeezerUserMeResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("picture_medium") val pictureMedium: String?,
    @SerializedName("picture_xl") val pictureXl: String?,
    @SerializedName("link") val link: String?
)

data class DeezerMultiFlowConfigsResponse(
    @SerializedName("data") val data: DeezerMultiFlowConfigsData? = null
)

data class DeezerMultiFlowConfigsData(
    @SerializedName("included") val included: List<DeezerMultiFlowConfig> = emptyList()
)

data class DeezerMultiFlowConfig(
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: DeezerMultiFlowConfigAttributes? = null,
    @SerializedName("links") val links: DeezerLinks? = null
)

data class DeezerMultiFlowConfigAttributes(
    @SerializedName("title") val title: String? = null,
    @SerializedName("images") val images: DeezerMultiFlowImages? = null
)

data class DeezerMultiFlowImages(
    @SerializedName("square") val square: DeezerImage? = null,
    @SerializedName("circle") val circle: DeezerImage? = null
)

data class DeezerLinks(
    @SerializedName("self") val self: String? = null
)

data class DeezerPlaylistDetailResponse(
    @SerializedName("data") val data: DeezerPlaylistDetailData? = null
)

data class DeezerPlaylistDetailData(
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: DeezerPlaylistAttributes? = null,
    @SerializedName("included") val included: List<DeezerTrack> = emptyList()
)

data class DeezerLyricsResponse(
    @SerializedName("data") val data: DeezerLyricsData? = null
)

data class DeezerLyricsData(
    @SerializedName("id") val id: String? = null,
    @SerializedName("attributes") val attributes: DeezerLyricsAttributes? = null
)

data class DeezerLyricsAttributes(
    @SerializedName("items") val items: List<DeezerLyricsItem> = emptyList(),
    @SerializedName("writers") val writers: String? = null
)

data class DeezerLyricsItem(
    @SerializedName("lrc_timestamp") val lrcTimestamp: String? = null,
    @SerializedName("milliseconds") val milliseconds: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("line") val line: String? = null
)
