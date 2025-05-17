package org.example

data class Base(var name: String, var price: Double){
    override fun toString() = "$name = $price"
}


class ManageBasePizza : baseFunction {
    private var allBases = mutableListOf<Base>()

    init {
        if (allBases.isEmpty()) {
            allBases.add(Base("Классическая", 300.0))
        }
    }
    fun menu(){
        var contin = true
        while (contin){
            println("""
                  === Основы ===
                1. Добавить основу
                2. Удалить основу
                3. Изменить основу
                4. Показать все основы
                5. Фильтрация
                6. Назад
            """.trimIndent())
            val input = readLine()
            when(input){
                "1" -> add()
                "2" -> del()
                "3" -> edit()
                "4" -> show()
                "5" -> sort()
                "6" -> contin = false
                else -> println("Введите корректное число!")
            }
        }
    }

    fun checkPrice(price: Double) : Boolean {
        val classicBase = allBases.find { it.name.lowercase() == "классическая" }
        if (classicBase != null && price > classicBase.price * 1.2) {
            println("Цена превосходит больше чем на 20% от классической основы!")
            return false
        }
        return true
    }

    override fun add() {
        println("Введите название!")
        val name = checkName()
        val price = checkPrice()
        if (!checkPrice(price)) return

        allBases.add(Base(name, price))
        println("Добавлено!")
    }

    override fun del() {
        if (allBases.isEmpty()) {
            println("Пусто!")
            return
        }
        show()

        println("Номер основы, который удаляем!")
        val ind = checkIndex(allBases)
        if (ind == allBases.size + 1) return
        if (allBases[ind - 1].name == "Классическая") println("Классическую нельзя удалять!")
        else {
            allBases.removeAt(ind - 1)
            println("Успешно удалён!")
        }
    }

    override fun edit() {
        if (allBases.isEmpty()) {
            println("Пусто!")
            return
        }
        show()

        println("Номер основы, который изменяем!")
        val ind = checkIndex(allBases)
        if (ind == allBases.size + 1) return

        println("Название новой основы!")
        val name = checkName()
        val price = checkPrice()
        if (!checkPrice(price)) return

        allBases[ind - 1].name = name
        allBases[ind - 1].price = price

        println("Успешно изменено!")
    }

    override fun show() {
        if (allBases.isEmpty()) {
            println("Пусто!")
            return
        }
        allBases.forEachIndexed { i, base -> println("${i + 1} : $base") }
        println("${allBases.size + 1}: Назад!")
    }

    override fun sort() {
        if(allBases.isEmpty()) return println("Пусто!")
        println("""
            === Сортировка и фильтрация ===
            1. Сортировать по названию (А-Я)
            2. Сортировать по названию (Я-А)
            3. Сортировать по цене (возрастание)
            4. Сортировать по цене (убывание)
            5. Фильтровать по цене (интревал)
            6. Назад
        """.trimIndent())

        when(readln()) {
            "1" -> {
                allBases.sortWith(baseByNameAsc)
                println("Отсортировано по названию (А-Я)")
                show()
            }
            "2" -> {
                allBases.sortWith(baseByNameDesc)
                println("Отсортировано по названию (Я-А)")
                show()
            }
            "3" -> {
                allBases.sortWith(baseByPriceAsc)
                println("Отсортировано по цене (возрастание)")
                show()
            }
            "4" -> {
                allBases.sortWith(baseByPriceDesc)
                println("Отсортировано по цене (убывание)")
                show()
            }
            "5" -> {
                println("Минимальная цена (enter для пропуска)")
                val minPrice = checkPrice()
                println("Максимальная цена (enter для пропуска)")
                val maxPrice = checkPrice()

                val filteredBases = filterByPriceRange(minPrice, maxPrice)
                showFiltered(filteredBases)
            }
            "6" -> return
            else -> println("Некорректный выбор!")
        }
    }

    fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Base> {
        return allBases.filter { base ->
            val price = base.price
            price in minPrice..maxPrice
        }
    }

    fun showFiltered(bases: List<Base>) {
        if (bases.isEmpty()) {
            println("Ничего не найдено!")
            return
        }
        val sortedBases = bases.sortedWith(baseByPriceAsc)
        sortedBases.forEachIndexed { i, base ->
            println("Пицца №${i + 1}")
            base.toString()
        }
    }


    fun get(): List<Base> = allBases

}