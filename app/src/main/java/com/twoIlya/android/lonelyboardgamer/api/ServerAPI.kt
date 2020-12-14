package com.twoIlya.android.lonelyboardgamer.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ServerAPI {

    @Multipart
    @POST("login")
    fun login(@Part("VKAccessToken") token: RequestBody): Call<ServerResponse>

    @Multipart
    @POST("register")
    fun register(
            @Part("VKAccessToken") token: RequestBody,
            @Part("address") location: RequestBody,
            @Part("description") aboutMe: RequestBody,
            @Part("prefCategories") categories: RequestBody,
            @Part("prefMechanics") mechanics: RequestBody
    ): Call<ServerResponse>

    @GET("profile")
    fun getProfile(@Header("Authorization") serverToken: String): Call<ServerResponse>

    @POST("profile/logout")
    fun logout(@Header("Authorization") serverToken: String): Call<ServerResponse>

    @Multipart
    @POST("profile/change/address")
    fun changeAddress(
            @Header("Authorization") serverToken: String,
            @Part("new") newAddress: RequestBody
    ): Call<ServerResponse>

    @Multipart
    @POST("profile/change/description")
    fun changeDescription(
            @Header("Authorization") serverToken: String,
            @Part("new") newDescription: RequestBody
    ): Call<ServerResponse>

    @Multipart
    @POST("profile/change/prefCategories")
    fun changeCategories(
            @Header("Authorization") serverToken: String,
            @Part("new") newCategories: RequestBody
    ): Call<ServerResponse>

    @Multipart
    @POST("profile/change/prefMechanics")
    fun changeMechanics(
            @Header("Authorization") serverToken: String,
            @Part("new") newMechanics: RequestBody
    ): Call<ServerResponse>

    @GET("search/byId")
    fun searchByID(
            @Header("Authorization") serverToken: String,
            @Query("id") id: Int
    ): Call<ServerResponse>

    @Multipart
    @POST("friends/requests/send")
    fun sendFriendRequest(
            @Header("Authorization") serverToken: String,
            @Part("targetId") id: RequestBody
    ): Call<ServerResponse>

    @Multipart
    @POST("/friends/requests/revoke")
    fun revokeRequest(
            @Header("Authorization") serverToken: String,
            @Part("targetId") id: RequestBody
    ): Call<ServerResponse>

    @GET("friends/requests/send")
    fun answerOnRequest(
            @Header("Authorization") serverToken: String,
            @Query("with") code: Int,
            @Query("targetId") id: Int
    ): Call<ServerResponse>

    // TODO
    fun deleteFriend(): Call<ServerResponse>

    @GET("search")
    suspend fun search(
            @Header("Authorization") serverToken: String,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int
    ): ServerResponse

    @GET("friends")
    suspend fun getFriends(
        @Header("Authorization") serverToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ServerResponse
}
