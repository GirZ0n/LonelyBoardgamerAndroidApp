package com.twoIlya.android.lonelyboardgamer.dataClasses

// Используется в списках: "Отправленные запросы", "Друзья" и т.д, кроме списка "Поиск"
data class ListProfile(
    val id: Int,
    val firstName: String,
    val secondName: String,
)
