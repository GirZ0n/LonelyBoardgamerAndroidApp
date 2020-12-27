package com.twoIlya.android.lonelyboardgamer.dataClasses

import com.google.gson.annotations.SerializedName
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse

// Используется в профиле пользователя
data class MyProfile(
    val id: Int,
    val firstName: String,
    val secondName: String,
    val address: String,
    val description: String,
    @SerializedName("prefCategories")
    val categories: List<String>,
    @SerializedName("prefMechanics")
    val mechanics: List<String>,
) : ServerRepositoryResponse()
