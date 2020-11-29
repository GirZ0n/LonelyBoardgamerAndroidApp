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

    @POST("profile/change/address")
    fun changeAddress(
        @Header("Authorization") serverToken: String,
        @Query("new") newAddress: String
    ): Call<ServerResponse>

    @POST("profile/change/description")
    fun changeDescription(
        @Header("Authorization") serverToken: String,
        @Query("new") newDescription: String
    ): Call<ServerResponse>

    @POST("profile/change/prefCategories")
    fun changeCategories(
        @Header("Authorization") serverToken: String,
        @Query("new") newCategories: String
    ): Call<ServerResponse>

    @POST("profile/change/prefMechanics")
    fun changeMechanics(
        @Header("Authorization") serverToken: String,
        @Query("new") newMechanics: String
    ): Call<ServerResponse>
}
