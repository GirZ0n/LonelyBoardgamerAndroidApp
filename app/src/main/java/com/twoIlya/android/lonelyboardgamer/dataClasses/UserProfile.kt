package com.twoIlya.android.lonelyboardgamer.dataClasses

import com.google.gson.annotations.SerializedName

data class UserProfile(
        var friendStatus: Int,
        val id: Int,
        @SerializedName("VKid")
        var idVK: String?,
        val firstName: String,
        val secondName: String,
        val description: String,
        @SerializedName("prefCategories")
        val categories: List<String>,
        @SerializedName("prefMechanics")
        val mechanics: List<String>,
)
