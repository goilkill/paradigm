package org.example

data class Ingredient(var name: String, var price: Double) {
    override fun toString() = "$name = $price"
}

class ManageIngredient : baseFunction {
    private var allIngredients = mutableListOf<Ingredient>()

    fun menu(){
        var contin = true
        while (contin) {
            println(
                """
                  === Ингредиенты ===
                1. Добавить ингредиенты
                2. Удалить ингредиенты
                3. Изменить ингредиенты
                4. Показать все ингредиенты
                5. Сортировка
                6. Назад
                """.trimIndent()
            )
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

    override fun add(){
        println("Введите название")
        val name = checkName()
        val price = checkPrice()

        allIngredients.add(Ingredient(name, price))
        println("Добавлено!")
    }

    override fun del(){
        if (allIngredients.isEmpty()){
            println("Нет ингредиентов!")
            return
        }
        show()

        println("Номер ингредиента, который удаляем!")
        val ind = checkIndex(allIngredients)
        if (ind == allIngredients.size + 1) return
        allIngredients.removeAt(ind - 1)
        println("Успешно удалён!")
    }

    override fun edit(){
        if (allIngredients.isEmpty()){
            println("Нет ингредиентов!")
            return
        }
        show()

        println("Номер ингредиента, который изменяем!")
        var ind = checkIndex(allIngredients)
        if (ind == allIngredients.size + 1) return

        println("Название нового ингредиента!")
        val name = checkName()
        allIngredients[ind - 1].name = name

        val price = checkPrice()
        allIngredients[ind - 1].price = price

        println("Успешно изменено!")
    }

    override fun show(){
        if (allIngredients.isEmpty()) return println("Пусто!")
        println("Ингридиенты: ")
        allIngredients.sortedWith(ingredientByPriceAsc)
        allIngredients.forEachIndexed { i, ingredient -> println("${i + 1} : $ingredient") }
        println("${allIngredients.size + 1}: Назад!")
    }

    override fun sort() {
        if (allIngredients.isEmpty()) return println("Пусто!")
        println(
            """
            === Сортировка и фильтрация ===
            1. Сортировать по названию (А-Я)
            2. Сортировать по названию (Я-А)
            3. Сортировать по цене (возрастание)
            4. Сортировать по цене (убывание)
            5. Фильтровать по цене (интервал) 
            6. Назад
        """.trimIndent()
        )

        when (readln()) {
            "1" -> {
                allIngredients.sortWith(ingredientByNameAsc)
                println("Отсортировано по названию (А-Я)")
                show()
            }

            "2" -> {
                allIngredients.sortWith(ingredientByNameDesc)
                println("Отсортировано по названию (Я-А)")
                show()
            }

            "3" -> {
                allIngredients.sortWith(ingredientByPriceAsc)
                println("Отсортировано по цене (возрастание)")
                show()
            }

            "4" -> {
                allIngredients.sortWith(ingredientByPriceDesc)
                println("Отсортировано по цене (убывание)")
                show()
            }

            "5" -> {
                println("Минимальная цена (enter для пропуска)")
                val minPrice = checkPrice()
                println("Максимальная цена (enter для пропуска)")
                val maxPrice = checkPrice()

                val filteredPizzas = filterByPriceRange(minPrice, maxPrice)
                showFiltered(filteredPizzas)
            }

            "6" -> return
            else -> println("Некорректный выбор!")
        }

    }

    fun showFiltered(list: List<Ingredient>) {
        if (list.isEmpty()) {
            println("Ничего не найдено!")
            return
        }
        val sortedBases = list.sortedWith(ingredientByPriceAsc)
        sortedBases.forEachIndexed { i, ingr ->
            println("Пицца №${i + 1}")
            ingr.toString()
        }
    }


    fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Ingredient> {
        return allIngredients.filter { ingr ->
            val price = ingr.price
            price in minPrice..maxPrice
        }
    }

    fun get(): List<Ingredient> = allIngredients

}