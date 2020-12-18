package com.twoIlya.android.lonelyboardgamer

import com.twoIlya.android.lonelyboardgamer.dataClasses.Event
import com.twoIlya.android.lonelyboardgamer.dataClasses.ServerError
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse
import retrofit2.HttpException
import java.io.IOException

object ErrorHandler {

    fun isError(response: ServerRepositoryResponse) = response is ServerError

    fun loginErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 1, 2 -> {
                Event(Event.Type.Notification, error.message)
            }
            3 -> {
                Event(Event.Type.Move, "Registration")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun registrationErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3 -> {
                Event(Event.Type.Notification, error.message)
            }
            1 -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun getProfileErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 3, 401 -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun logoutErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 3, 401 -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun changeProfileErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 401 -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun searchErrorHandler(exception: Throwable): Event {
        return when (exception) {
            is HttpException -> {
                Event(Event.Type.Move, "Login")
            }
            is IOException -> {
                Event(Event.Type.Notification, exception.localizedMessage ?: "")
            }
            else -> {
                Event(Event.Type.Error, exception.toString())
            }
        }
    }

    fun searchByIDErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3, 4, 5 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 401  -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun sendFriendRequestErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3, 4, 5 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 401 -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun answerOnRequestErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3, 4, 5 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 401  -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun revokeRequestErrorHandler(error: ServerError): Event {
        return when (error.code) {
            -1, 2, 3, 4, 5 -> {
                Event(Event.Type.Notification, error.message)
            }
            1, 401  -> {
                Event(Event.Type.Move, "Login")
            }
            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }
}
