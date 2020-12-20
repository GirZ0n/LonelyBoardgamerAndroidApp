package com.twoIlya.android.lonelyboardgamer.repository

object PreferencesRepository {
    val categories = listOf(
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


    fun getIndicesOfSelectedCategories(selected: List<String>): IntArray {
        return categories.mapIndexed { index, category ->
            when (category in selected) {
                true -> index
                false -> null
            }
        }.filterNotNull().toIntArray()
    }

    fun convertToCategoriesList(indices: IntArray) = indices.sorted().map { categories[it] }

    // ----------------------------------------

    val mechanics = listOf(
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

    fun getIndicesOfSelectedMechanics(selected: List<String>): IntArray {
        return mechanics.mapIndexed { index, category ->
            when (category in selected) {
                true -> index
                false -> null
            }
        }.filterNotNull().toIntArray()
    }

    fun convertToMechanicsList(indices: IntArray) = indices.sorted().map { mechanics[it] }
}
