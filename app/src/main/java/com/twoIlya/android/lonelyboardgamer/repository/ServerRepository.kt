package com.twoIlya.android.lonelyboardgamer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.twoIlya.android.lonelyboardgamer.api.ServerAPI
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import com.twoIlya.android.lonelyboardgamer.dataClasses.LogoutMessage
import com.twoIlya.android.lonelyboardgamer.dataClasses.Profile
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
import java.lang.NullPointerException
import java.net.SocketTimeoutException

object ServerRepository {
    private val serverAPI: ServerAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://immense-dusk-70422.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        serverAPI = retrofit.create(ServerAPI::class.java)
    }

    fun login(vkToken: Token): LiveData<ServerRepositoryResponse> {
        val responseLiveData: MutableLiveData<ServerRepositoryResponse> = MutableLiveData()

        val tokenBody = vkToken.value.toRequestBody("text/plain".toMediaTypeOrNull())
        val loginRequest = serverAPI.login(tokenBody)

        loginRequest.enqueue(MyCallback("Login", responseLiveData) { serverResponse ->
            val jsonElementAsString = serverResponse.message.toString()
            Token(jsonElementAsString.trim { it == '"' })
        })

        return responseLiveData
    }

    fun register(
        vkToken: Token,
        location: String,
        categories: List<String>,
        mechanics: List<String>,
        description: String
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val getProfileRequest = serverAPI.register(
            vkToken.value.toRequestBody("text/plain".toMediaTypeOrNull()),
            location.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            categories.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull()),
            mechanics.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
        )

        getProfileRequest.enqueue(MyCallback("register", responseLiveData) { serverResponse ->
            val jsonElementAsString = serverResponse.message.toString()
            Token(jsonElementAsString.trim { it == '"' })
        })

        return responseLiveData
    }

    fun getProfile(serverToken: Token): LiveData<ServerRepositoryResponse> {
        val responseLiveData: MutableLiveData<ServerRepositoryResponse> = MutableLiveData()

        val getProfileRequest = serverAPI.getProfile("Bearer ${serverToken.value}")

        getProfileRequest.enqueue(MyCallback("getProfile", responseLiveData) {
            val response: ServerRepositoryResponse = try {
                Gson().fromJson(it.message.toString(), Profile::class.java)
            } catch (e: JsonSyntaxException) {
                Log.d(Constants.TAG, e.toString())
                ServerError(-2, "Error during deserialization: ${e.message} ")
            } catch (e: NullPointerException) {
                Log.d(Constants.TAG, e.toString())
                ServerError(-2, "Error during deserialization: ${e.message} ")
            }
            response
        })

        return responseLiveData
    }


    fun logout(serverToken: Token): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val logoutRequest = serverAPI.logout("Bearer ${serverToken.value}")

        logoutRequest.enqueue(MyCallback("logout", responseLiveData) {
            LogoutMessage(it.message.toString())
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
                        responseLiveData.value = ServerError(it.status, it.message.toString())
                    }
                } ?: run { responseLiveData.value = ServerError(-2, "Empty body") }
            } else {
                responseLiveData.value = ServerError(response.code(), response.message())
            }
            val message = "$functionName (onR): \n" +
                    "body - ${response.body()} \n" +
                    "code - ${response.code()} \n" +
                    "message - ${response.message()}"
            Log.d(Constants.TAG, message)
        }

        override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
            responseLiveData.value = onFailureHandling(t)
            Log.d(Constants.TAG, "$functionName (onF): $t")
        }
    }

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

    private fun onFailureHandling(t: Throwable): ServerError {
        return when (t) {
            // Server fell asleep
            is SocketTimeoutException -> ServerError(
                -1,
                "The server fell asleep. Repeat your action"
            )
            // Network problems
            is IOException -> ServerError(
                -1,
                "There was a problem sending your request. Check your internet connection. " +
                        "If the problem persists, please contact us at: placeholder@placeholder.com"
            )
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
