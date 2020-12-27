package com.twoIlya.android.lonelyboardgamer.dataClasses

import com.google.gson.annotations.SerializedName
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse

// Используется в профиле пользователя
data class MyProfile(
    val id: Int,
    val firstName: String,
    val secondName: String,
    var address: String,
    var description: String,
    @SerializedName("prefCategories")
    var categories: List<String>,
    @SerializedName("prefMechanics")
    var mechanics: List<String>,
) : ServerRepositoryResponse()
