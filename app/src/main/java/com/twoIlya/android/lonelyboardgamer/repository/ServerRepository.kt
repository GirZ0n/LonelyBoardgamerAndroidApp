package com.twoIlya.android.lonelyboardgamer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.twoIlya.android.lonelyboardgamer.api.ServerAPI
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ServerRepository {
    private val serverAPI: ServerAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://immense-dusk-70422.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        serverAPI = retrofit.create(ServerAPI::class.java)
    }

    fun login(VKAccessToken: String): LiveData<ServerRepositoryResponse> {
        val responseLiveData: MutableLiveData<ServerRepositoryResponse> = MutableLiveData()

        val tokenBody = VKAccessToken.toRequestBody("text/plain".toMediaTypeOrNull())
        val loginRequest = serverAPI.login(tokenBody)

        loginRequest.enqueue(MyCallback("Login", responseLiveData) { serverResponse ->
            Token(serverResponse.message)
        })

        return responseLiveData
    }

    private class MyCallback(
        val functionName: String,
        val responseLiveData: MutableLiveData<ServerRepositoryResponse>,
        val parser: (ServerResponse) -> ServerRepositoryResponse
    ) : Callback<ServerResponse> {
        override fun onResponse(
            call: Call<ServerResponse>,
            response: Response<ServerResponse>
        ) {
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    if (it.status == 0) {
                        responseLiveData.value = parser(it)
                    } else {
                        responseLiveData.value = ServerError(it.status, it.message)
                    }
                } ?: run { responseLiveData.value = ServerError(-2, "Empty body") }
            } else {
                responseLiveData.value = ServerError(response.code(), response.message())
            }
            Log.d(Constants.TAG, "$functionName (onR): ${response.body()}")
        }

        override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
            responseLiveData.value = onFailureHandling(t)
            Log.d(Constants.TAG, "$functionName (onF): $t")
            Log.e(Constants.TAG, "$functionName (onF): something went wrong", t)
        }
    }

/*
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
*/

    /*
        private class MyCallback(
            val functionName: String,
            val responseLiveData: MutableLiveData<ServerResponse>
        ) : Callback<ServerResponse> {
            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                responseLiveData.value = onNetworkFailureHandling()
                Log.e(Constants.TAG, "$functionName (onF): something went wrong", t)
            }

            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                responseLiveData.value = when {
                    isErrorCode(response.code()) -> ServerResponse(
                        response.code(),
                        response.message()
                    )
                    else -> response.body()
                }

                Log.d(Constants.TAG, "$functionName (onR): ${response.body()}")
            }
        }
    */

    private fun onNetworkFailureHandling(): ServerError {
        val message = "There is no network connection available. Please check your " +
                "connection settings and try again"
        return ServerError(-1, message)
    }

    private fun onFailureHandling(t: Throwable): ServerError {
        return when (t) {
            // Network problems
            is IOException -> onNetworkFailureHandling()
            // Other problems
            else -> ServerError(
                -3,
                "Something went wrong while sending or receiving your request: ${t.message}"
            )
        }
    }

    private object Constants {
        const val TAG = "ServerRepository_TAG"
    }
}
