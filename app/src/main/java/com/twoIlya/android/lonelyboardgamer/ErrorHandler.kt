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
            ServerError.Type.NETWORK,
            ServerError.Type.AUTHORIZATION,
            ServerError.Type.SOME_INFO_MISSING -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.ELEMENT_WAS_NOT_FOUND -> {
                Event(Event.Type.Move, "Registration")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun registrationErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun getProfileErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun logoutErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun changeProfileErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.HTTP_401 -> {
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
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.WRONG_DATA_FORMAT,
            ServerError.Type.BAD_DATA -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun sendFriendRequestErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.WRONG_DATA_FORMAT,
            ServerError.Type.BAD_DATA -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun answerOnRequestErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.WRONG_DATA_FORMAT,
            ServerError.Type.BAD_DATA -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun revokeRequestErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.WRONG_DATA_FORMAT,
            ServerError.Type.BAD_DATA -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }

    fun deleteFriendErrorHandler(error: ServerError): Event {
        return when (error.code) {
            ServerError.Type.NETWORK,
            ServerError.Type.SOME_INFO_MISSING,
            ServerError.Type.ELEMENT_WAS_NOT_FOUND,
            ServerError.Type.WRONG_DATA_FORMAT,
            ServerError.Type.BAD_DATA -> {
                Event(Event.Type.Notification, error.message)
            }

            ServerError.Type.AUTHORIZATION,
            ServerError.Type.HTTP_401 -> {
                Event(Event.Type.Move, "Login")
            }

            else -> {
                Event(Event.Type.Error, "${error.code}: ${error.message}")
            }
        }
    }
}
