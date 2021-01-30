package com.twoIlya.android.lonelyboardgamer.dataClasses

data class Event(
    val type: Type,
    val message: String,
    var isHandle: Boolean = false,
) {
    enum class Type {
        ERROR,
        NOTIFICATION,
        MOVE,
    }
}
