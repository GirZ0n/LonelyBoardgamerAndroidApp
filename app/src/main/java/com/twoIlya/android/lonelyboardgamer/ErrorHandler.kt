package com.twoIlya.android.lonelyboardgamer

import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.EventType
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse

object ErrorHandler {

    fun isError(response: ServerRepositoryResponse) = response is ServerError

    fun loginErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 1, 2 -> {
                Event(EventType.Warning, error.message)
            }
            3 -> {
                Event(EventType.Move, "Registration")
            }
            else -> {
                Event(EventType.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun registrationErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3 -> {
                Event(EventType.Warning, error.message)
            }
            1 -> {
                Event(EventType.Move, "Login")
            }
            else -> {
                Event(EventType.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun getProfileErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2 -> {
                Event(EventType.Warning, error.message)
            }
            1, 3, 401 -> {
                Event(EventType.Move, "Login")
            }
            else -> {
                Event(EventType.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun logoutErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2 -> {
                Event(EventType.Warning, error.message)
            }
            1, 3, 401 -> {
                Event(EventType.Move, "Login")
            }
            else -> {
                Event(EventType.Error, "${error.code}: ${error.message}")
            }
        }
    }
}
