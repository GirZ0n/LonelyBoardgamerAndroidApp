package com.twoIlya.android.lonelyboardgamer.dataClasses

data class Event(
    val type: EventType,
    val message: String,
    var isHandle: Boolean = false,
)
