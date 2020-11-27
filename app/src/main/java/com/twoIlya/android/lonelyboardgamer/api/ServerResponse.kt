package com.twoIlya.android.lonelyboardgamer.api

import com.google.gson.JsonElement

data class ServerResponse(
    var status: Int,
    var message: JsonElement,
)
