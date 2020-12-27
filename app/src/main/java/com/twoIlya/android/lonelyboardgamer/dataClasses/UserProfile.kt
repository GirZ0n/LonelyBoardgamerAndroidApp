package com.twoIlya.android.lonelyboardgamer.dataClasses

import com.google.gson.annotations.SerializedName
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse

// Используется в профиле другого игрока
data class UserProfile(
        var friendStatus: Int,
        val id: Int,
        @SerializedName("vkid")
        var idVK: String?,
        val firstName: String,
        val secondName: String,
        val description: String,
        @SerializedName("prefCategories")
        val categories: List<String>,
        @SerializedName("prefMechanics")
        val mechanics: List<String>,
) : ServerRepositoryResponse()
