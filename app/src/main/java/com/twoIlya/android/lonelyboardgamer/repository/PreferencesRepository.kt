package com.twoIlya.android.lonelyboardgamer.repository

import com.androidbuts.multispinnerfilter.KeyPairBoolData

object PreferencesRepository {
    private val categories = arrayOf(
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

    fun getCategories(selected: List<String> = emptyList()): List<KeyPairBoolData> {
        val answer = mutableListOf<KeyPairBoolData>()
        categories.forEachIndexed { index, it ->
            val item = KeyPairBoolData()
            item.name = it
            item.isSelected = it in selected
            item.id = index.toLong()
            answer.add(item)
        }
        return answer
    }

    // ----------------------------------------

    private val mechanics = arrayOf(
        "Контроль над территориями",
        "Аукцион",
        "Блеф",
        "Удача",
        "Кооперация",
        "Составление колоды",
        "Дедукция",
        "Ловкость",
        "Броски кубиков",
        "Динамические правила",
        "Создание карты местности",
        "Запоминание",
        "Доставка ресурса",
        "Риск",
        "Загадки",
        "Игра в реальном времени",
        "Распоряжение ресурсами",
        "Большая вариативность",
        "Обмен",
        "Викторина",
        "Голосование",
        "Размещение рабочих",
        "Ролевой отыгрыш",
        "Устранение соперников",
        "Разнящиеся способности игроков",
    )

    fun getMechanics(selected: List<String> = emptyList()): List<KeyPairBoolData> {
        val answer = mutableListOf<KeyPairBoolData>()
        mechanics.forEachIndexed { index, it ->
            val item = KeyPairBoolData()
            item.name = it
            item.isSelected = it in selected
            item.id = index.toLong()
            answer.add(item)
        }
        return answer
    }

    // ----------------------------------------

    fun convertToList(items: List<KeyPairBoolData>) = items.map { it.name }.toList()
}
