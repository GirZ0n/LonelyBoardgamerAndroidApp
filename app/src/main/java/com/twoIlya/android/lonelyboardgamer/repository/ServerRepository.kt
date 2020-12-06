package com.twoIlya.android.lonelyboardgamer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.twoIlya.android.lonelyboardgamer.api.ServerAPI
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository.Constants.NETWORK_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeAddress(serverToken: Token, address: String): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val changeAddressRequest = serverAPI.changeAddress("Bearer ${serverToken.value}", address)

        changeAddressRequest.enqueue(MyCallback("changeAddress", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeDescription(
        serverToken: Token,
        description: String
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val changeDescriptionRequest =
            serverAPI.changeDescription("Bearer ${serverToken.value}", description)

        changeDescriptionRequest.enqueue(MyCallback("changeDescription", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeCategories(
        serverToken: Token,
        categories: List<String>
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val categoriesAsString = categories.joinToString(",")
        val changeCategoriesRequest =
            serverAPI.changeCategories("Bearer ${serverToken.value}", categoriesAsString)

        changeCategoriesRequest.enqueue(MyCallback("changeCategories", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeMechanics(
        serverToken: Token,
        mechanics: List<String>
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val mechanicsAsString = mechanics.joinToString(",")
        val changeMechanicsRequest =
            serverAPI.changeMechanics("Bearer ${serverToken.value}", mechanicsAsString)

        changeMechanicsRequest.enqueue(MyCallback("changeMechanics", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun search(serverToken: Token): Flow<PagingData<SearchProfile>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchProfilePagingSource(serverToken, serverAPI) }
        ).flow
    }

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

    private class SearchProfilePagingSource(
        private val serverToken: Token,
        private val api: ServerAPI
    ) :
        PagingSource<Int, SearchProfile>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchProfile> {
            Log.d(Constants.TAG, "load params: $params")
            val position = params.key ?: Constants.SERVER_STARTING_PAGE_INDEX
            return try {
                val response = api.search("Bearer ${serverToken.value}", params.loadSize, position)
                Log.d(Constants.TAG, response.toString())
                val profileType = object : TypeToken<List<SearchProfile>>() {}.type
                val profiles =
                    Gson().fromJson<List<SearchProfile>>(
                        response.message.toString(),
                        profileType
                    )
                LoadResult.Page(
                    data = profiles,
                    prevKey = if (position == Constants.SERVER_STARTING_PAGE_INDEX) null else position - NETWORK_PAGE_SIZE,
                    nextKey = if (profiles.isEmpty()) null else position + NETWORK_PAGE_SIZE,
                )
            } catch (exception: IOException) {
                Log.d(Constants.TAG, "exception: ${exception.localizedMessage}")
                LoadResult.Error(exception)
            } catch (exception: HttpException) {
                Log.d(Constants.TAG, "exception: ${exception.localizedMessage}")
                LoadResult.Error(exception)
            } catch (exception: JsonSyntaxException) {
                Log.d(Constants.TAG, "exception: ${exception.localizedMessage}")
                LoadResult.Error(exception)
            } catch (exception: NullPointerException) {
                Log.d(Constants.TAG, "exception: ${exception.localizedMessage}")
                LoadResult.Error(exception)
            }
        }
    }

    private object Constants {
        const val TAG = "ServerRepository_TAG"
        const val NETWORK_PAGE_SIZE = 50
        const val SERVER_STARTING_PAGE_INDEX = 0
    }
}
