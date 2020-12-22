package com.twoIlya.android.lonelyboardgamer.dataClasses

import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse

data class ServerError(
    val code: Type,
    val message: String
) : ServerRepositoryResponse() {
    enum class Type {
        // Local errors
        UNKNOWN, // code: -3
        SERIALIZATION, // code: -2
        NETWORK, // code: -1

        // Server errors
        AUTHORIZATION, // code: 1
        SOME_INFO_MISSING, // code: 2
        ELEMENT_WAS_NOT_FOUND, // code: 3
        WRONG_DATA_FORMAT, // code: 4
        BAD_DATA, // code: 5

        // HTTP errors
        HTTP_401,
    }
}
