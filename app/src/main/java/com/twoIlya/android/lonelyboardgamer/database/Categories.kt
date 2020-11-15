package com.twoIlya.android.lonelyboardgamer.database

import com.androidbuts.multispinnerfilter.KeyPairBoolData

object Categories {
    private val array = arrayOf(
        "Соревновательные",
        "Кооперативные",
        "Один против всех",
        "Абстрактные",
        "Евро",
        "Америтреш",
        "Варгеймы",
        "Социальные игры",
        "Спокойные",
        "Агрессивные",
        "Точный просчет",
        "Рандом",
        "Ролевые",
        "На ассоциации",
        "Атмосферные",
        "Сложные",
        "Простые",
        "Карточные",
    )

    fun getAsListOfKeyPairBoolData(selected: List<String>): List<KeyPairBoolData> {
        val answer = mutableListOf<KeyPairBoolData>()
        array.forEachIndexed { index, it ->
            val item = KeyPairBoolData()
            item.name = it
            item.isSelected = it in selected
            item.id = index.toLong()
            answer.add(item)
        }
        return answer
    }
}
