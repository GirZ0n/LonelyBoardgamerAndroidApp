package com.twoIlya.android.lonelyboardgamer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.twoIlya.android.lonelyboardgamer.api.ServerAPI
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import com.twoIlya.android.lonelyboardgamer.dataClasses.*
import com.twoIlya.android.lonelyboardgamer.paging.ListPagingSource
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository.Constants.NETWORK_PAGE_SIZE
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository.Tag.TAG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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
        val loginCall = serverAPI.login(tokenBody)

        loginCall.enqueue(MyCallback("Login", responseLiveData) { serverResponse ->
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

        val getProfileCall = serverAPI.register(
            vkToken.value.toRequestBody("text/plain".toMediaTypeOrNull()),
            location.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            categories.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull()),
            mechanics.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
        )

        getProfileCall.enqueue(MyCallback("register", responseLiveData) { serverResponse ->
            val jsonElementAsString = serverResponse.message.toString()
            Token(jsonElementAsString.trim { it == '"' })
        })

        return responseLiveData
    }

    fun getProfile(serverToken: Token): LiveData<ServerRepositoryResponse> {
        val responseLiveData: MutableLiveData<ServerRepositoryResponse> = MutableLiveData()

        val getProfileCall = serverAPI.getProfile("Bearer ${serverToken.value}")

        getProfileCall.enqueue(MyCallback("getProfile", responseLiveData) {
            val response: ServerRepositoryResponse = try {
                Gson().fromJson(it.message.toString(), MyProfile::class.java)
            } catch (e: JsonSyntaxException) {
                Log.d(TAG, e.toString())
                ServerError(
                    ServerError.Type.SERIALIZATION,
                    "getProfile: ${ErrorMessages.SERIALIZATION}.\n" +
                            "Exception: $e.\n" +
                            "Response: $it"
                )
            } catch (e: NullPointerException) {
                Log.d(TAG, e.toString())
                ServerError(
                    ServerError.Type.SERIALIZATION,
                    "getProfile: ${ErrorMessages.SERIALIZATION}.\n" +
                            "Exception: $e.\n" +
                            "Response: $it"
                )
            }
            response
        })

        return responseLiveData
    }

    fun logout(serverToken: Token): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val logoutCall = serverAPI.logout("Bearer ${serverToken.value}")

        logoutCall.enqueue(MyCallback("logout", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeAddress(serverToken: Token, address: String): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val addressRequestBody = address.toRequestBody("text/plain".toMediaTypeOrNull())

        val changeAddressCall =
            serverAPI.changeAddress("Bearer ${serverToken.value}", addressRequestBody)

        changeAddressCall.enqueue(MyCallback("changeAddress", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeDescription(
        serverToken: Token,
        description: String
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

        val changeDescriptionCall =
            serverAPI.changeDescription("Bearer ${serverToken.value}", descriptionRequestBody)

        changeDescriptionCall.enqueue(MyCallback("changeDescription", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeCategories(
        serverToken: Token,
        categories: List<String>
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val categoriesRequestBody =
            categories.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())

        val changeCategoriesCall =
            serverAPI.changeCategories("Bearer ${serverToken.value}", categoriesRequestBody)

        changeCategoriesCall.enqueue(MyCallback("changeCategories", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun changeMechanics(
        serverToken: Token,
        mechanics: List<String>
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val mechanicsRequestBody =
            mechanics.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())

        val changeMechanicsCall =
            serverAPI.changeMechanics("Bearer ${serverToken.value}", mechanicsRequestBody)

        changeMechanicsCall.enqueue(MyCallback("changeMechanics", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun search(serverToken: Token): LiveData<PagingData<SearchProfile>> {
        val pagingSourceFactory = ListPagingSource(SearchProfile::class.java) { limit, offset ->
            serverAPI.search("Bearer ${serverToken.value}", limit, offset)
        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { pagingSourceFactory }
        ).liveData
    }

    fun getFriends(serverToken: Token): LiveData<PagingData<ListProfile>> {
        val pagingSourceFactory = ListPagingSource(ListProfile::class.java) { limit, offset ->
            serverAPI.getFriends("Bearer ${serverToken.value}", limit, offset)
        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { pagingSourceFactory }
        ).liveData
    }

    fun getInRequests(serverToken: Token): LiveData<PagingData<ListProfile>> {
        val pagingSourceFactory = ListPagingSource(ListProfile::class.java) { limit, offset ->
            serverAPI.getInRequests("Bearer ${serverToken.value}", limit, offset)
        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { pagingSourceFactory }
        ).liveData
    }

    fun getOutRequests(serverToken: Token): LiveData<PagingData<ListProfile>> {
        val pagingSourceFactory = ListPagingSource(ListProfile::class.java) { limit, offset ->
            serverAPI.getOutRequests("Bearer ${serverToken.value}", limit, offset)
        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { pagingSourceFactory }
        ).liveData
    }

    fun getHiddenRequests(serverToken: Token): LiveData<PagingData<ListProfile>> {
        val pagingSourceFactory = ListPagingSource(ListProfile::class.java) { limit, offset ->
            serverAPI.getHiddenRequests("Bearer ${serverToken.value}", limit, offset)
        }

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { pagingSourceFactory }
        ).liveData
    }

    fun searchByID(serverToken: Token, id: Int): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val searchByIDCall =
            serverAPI.searchByID("Bearer ${serverToken.value}", id)

        searchByIDCall.enqueue(MyCallback("searchByID", responseLiveData) {
            val response: ServerRepositoryResponse = try {
                Gson().fromJson(it.message.toString(), UserProfile::class.java)
            } catch (e: JsonSyntaxException) {
                Log.d(TAG, e.toString())
                ServerError(
                    ServerError.Type.SERIALIZATION,
                    "searchByID: ${ErrorMessages.SERIALIZATION}.\n" +
                            "Exception: $e.\n" +
                            "Response: $it"
                )
            } catch (e: NullPointerException) {
                Log.d(TAG, e.toString())
                ServerError(
                    ServerError.Type.SERIALIZATION,
                    "searchByID: ${ErrorMessages.SERIALIZATION}.\n" +
                            "Exception: $e.\n" +
                            "Response: $it"
                )
            }
            response
        })

        return responseLiveData
    }

    fun sendFriendRequest(serverToken: Token, id: Int): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val idRequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val sendFriendRequestCall =
            serverAPI.sendFriendRequest("Bearer ${serverToken.value}", idRequestBody)

        sendFriendRequestCall.enqueue(MyCallback("sendFriendRequest", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun revokeRequest(serverToken: Token, id: Int): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val idRequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val revokeRequestCall =
            serverAPI.revokeRequest("Bearer ${serverToken.value}", idRequestBody)

        revokeRequestCall.enqueue(MyCallback("revokeRequest", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }


    fun answerOnRequest(
        serverToken: Token,
        id: Int,
        isAccept: Boolean
    ): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val code = when (isAccept) {
            true -> 1
            false -> 0
        }

        val codeRequestBody = code.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val idRequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val answerOnRequestCall =
            serverAPI.answerOnRequest("Bearer ${serverToken.value}", codeRequestBody, idRequestBody)

        answerOnRequestCall.enqueue(MyCallback("answerOnRequest", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    fun deleteFriend(serverToken: Token, id: Int): LiveData<ServerRepositoryResponse> {
        val responseLiveData = MutableLiveData<ServerRepositoryResponse>()

        val idRequestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val deleteFriendCall =
            serverAPI.deleteFriend("Bearer ${serverToken.value}", idRequestBody)

        deleteFriendCall.enqueue(MyCallback("deleteFriend", responseLiveData) {
            ServerMessage(it.message.toString())
        })

        return responseLiveData
    }

    private fun onFailureHandling(functionName: String, t: Throwable): ServerError {
        return when (t) {
            // Server fell asleep
            is SocketTimeoutException -> ServerError(
                ServerError.Type.NETWORK,
                "${ErrorMessages.SOCKET_TIMEOUT}. ($functionName)"
            )
            // Network problems
            is IOException -> ServerError(
                ServerError.Type.NETWORK,
                "${ErrorMessages.IO}. ($functionName)"
            )
            // Other problems
            else -> ServerError(
                ServerError.Type.UNKNOWN,
                "$functionName: ${ErrorMessages.UNKNOWN}.\n" +
                        "Exception: $t"
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
                        val type = when (it.status) {
                            1 -> ServerError.Type.AUTHORIZATION
                            2 -> ServerError.Type.SOME_INFO_MISSING
                            3 -> ServerError.Type.ELEMENT_WAS_NOT_FOUND
                            4 -> ServerError.Type.WRONG_DATA_FORMAT
                            5 -> ServerError.Type.BAD_DATA
                            else -> ServerError.Type.UNKNOWN
                        }
                        responseLiveData.value =
                            ServerError(type, "${it.message}. ($functionName)")
                    }
                } ?: run {
                    responseLiveData.value =
                        ServerError(
                            ServerError.Type.SERIALIZATION,
                            "$functionName: ${ErrorMessages.SERIALIZATION}.\n" +
                                    "Response: $body"
                        )
                }
            } else {
                val type = when (response.code()) {
                    401 -> ServerError.Type.HTTP_401
                    else -> ServerError.Type.UNKNOWN
                }
                responseLiveData.value = ServerError(type, "$functionName: ${response.message()}")
            }

            val message = "$functionName (onR): \n" +
                    "body - ${response.body()} \n" +
                    "code - ${response.code()} \n" +
                    "message - ${response.message()}"
            Log.d(TAG, message)
        }

        override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
            responseLiveData.value = onFailureHandling(functionName, t)
            Log.d(TAG, "$functionName (onF): $t")
        }
    }

    private object Tag {
        const val TAG = "ServerRepository_TAG"
    }

    object Constants {
        const val NETWORK_PAGE_SIZE = 50
        const val SERVER_STARTING_PAGE_INDEX = 0
    }

    private object ErrorMessages {
        private const val EMAIL = "ilyavlasov2011@gmail.com"

        const val SERIALIZATION = "Произошла ошибка во время сериализации"

        const val SOCKET_TIMEOUT =
            "К сожалению, сервер не доступен, попробуйте позднее. " +
                    "Если ошибка повторяется, обратитесь сюда: $EMAIL"

        const val IO =
            "Произошла ошибка во время отправки вашего запроса. " +
                    "Проверьте ваше интернет - соединение. " +
                    "Если ошибка повторяется, обратитесь сюда: $EMAIL"

        const val UNKNOWN = "Что-то пошло не так во время отправки или получения вашего запроса"
    }
}
