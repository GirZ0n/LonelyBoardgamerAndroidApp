package com.twoIlya.android.lonelyboardgamer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.twoIlya.android.lonelyboardgamer.api.ServerAPI
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object Repository {
    private val serverAPI: ServerAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://immense-dusk-70422.herokuapp.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        serverAPI = retrofit.create(ServerAPI::class.java)
    }

    fun login(VKAccessToken: String): LiveData<ServerResponse> {
        val responseLiveData: MutableLiveData<ServerResponse> = MutableLiveData()

        val tokenBody = VKAccessToken.toRequestBody("text/plain".toMediaTypeOrNull())
        val loginRequest = serverAPI.login(tokenBody)

        loginRequest.enqueue(MyCallback("login", responseLiveData))

        return responseLiveData
    }

    fun logout(serverToken: String): LiveData<ServerResponse> {
        val responseLiveData: MutableLiveData<ServerResponse> = MutableLiveData()

        val logoutRequest = serverAPI.logout("Bearer $serverToken")

        logoutRequest.enqueue(MyCallback("logout", responseLiveData))

        return responseLiveData
    }

    fun getProfile(serverToken: String): LiveData<ServerResponse> {
        val responseLiveData = MutableLiveData<ServerResponse>()

        val getProfileRequest = serverAPI.getProfile("Bearer $serverToken")

        getProfileRequest.enqueue(MyCallback("getProfile", responseLiveData))

        return responseLiveData
    }

    fun register(
        VKAccessToken: String,
        location: String,
        description: String,
        categories: String,
        mechanics: String
    ): LiveData<ServerResponse> {
        val responseLiveData = MutableLiveData<ServerResponse>()

        val getProfileRequest = serverAPI.register(
            VKAccessToken.toRequestBody("text/plain".toMediaTypeOrNull()),
            location.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            categories.toRequestBody("text/plain".toMediaTypeOrNull()),
            mechanics.toRequestBody("text/plain".toMediaTypeOrNull())
        )

        getProfileRequest.enqueue(MyCallback("register", responseLiveData))

        return responseLiveData
    }

    private fun onFailureHandling(): ServerResponse {
        val message = "There is no network connection available. Please check your " +
                "connection settings and try again"
        return ServerResponse("-1", message)
    }

    private class MyCallback(
        val functionName: String,
        val responseLiveData: MutableLiveData<ServerResponse>
    ) : Callback<ServerResponse> {
        override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
            responseLiveData.value = onFailureHandling()
            Log.e(Constants.TAG, "$functionName (onF): something went wrong", t)
        }

        override fun onResponse(
            call: Call<ServerResponse>,
            response: Response<ServerResponse>
        ) {
            responseLiveData.value = when {
                isErrorCode(response.code()) -> ServerResponse(
                    response.code().toString(),
                    response.message()
                )
                else -> response.body()
            }

            Log.d(Constants.TAG, "$functionName (onR): ${response.body()}")
        }
    }

    private object Constants {
        const val TAG = "Repository"
    }

    private fun isErrorCode(code: Int): Boolean {
        return code != 200
    }
}