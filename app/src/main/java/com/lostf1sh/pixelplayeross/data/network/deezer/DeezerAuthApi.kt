package com.lostf1sh.pixelplayeross.data.network.deezer

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerAuthApi {
    @POST("2.0/smartlogin")
    suspend fun getSmartLoginCode(
        @Query("app_id") appId: String
    ): SmartLoginRequestResponse

    @GET("2.0/smartlogin/{code}")
    suspend fun pollSmartLogin(
        @Path("code") code: String,
        @Query("app_id") appId: String
    ): SmartLoginPollResponse
}
