package com.twoIlya.android.lonelyboardgamer.database

import com.androidbuts.multispinnerfilter.KeyPairBoolData

object Mechanics {
    private val array = arrayOf(
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
        "Удача",
        "Устранение соперников",
        "Разнящиеся способности игроков",
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
