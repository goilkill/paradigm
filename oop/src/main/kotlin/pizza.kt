package org.example

open class Pizza(
    open var name: String,
    open var base: Base?,
    open var size: PizzaSize,
    open var border: Border?,
    open var ingredients: MutableList<Ingredient>
) {
    open fun finalPrice(): Double {
        var price = ingredients.sumOf { it.price }
        if (border != null) price += border!!.borderPrice()
        when (size) {
            PizzaSize.SMALL -> price += 50.0
            PizzaSize.MEDIUM -> price += 100.0
            PizzaSize.LARGE -> price += 150.0
        }
        if (base != null) price += base!!.price
        return price
    }

    open fun Print() {
        println("""
            Название: $name 
            Основа: $base 
            Ингредиенты: ${ingredients.joinToString()}
            Бортик: $border
            Размер: $size
            Сумма пиццы: ${finalPrice()}
        """.trimIndent())
    }
}

class ManagePizza(private val ingredientsList: List<Ingredient>, private val basesList: List<Base>, private val borderList: List<Border>) : baseFunction {
    private var allPizzas = mutableListOf<Pizza>()

    fun menu() {
        var contin = true
        while (contin) {
            println(
                """
                  === Пиццы ===
                1. Добавить пиццу
                2. Удалить пиццу
                3. Изменить пиццу
                4. Фильтрация
                5. Показать все пиццы
                6. Назад
                """.trimIndent()
            )
            val input = readLine()
            when (input) {
                "1" -> add()
                "2" -> del()
                "3" -> edit()
                "4" -> sort()
                "5" -> show()
                "6" -> contin = false
                else -> println("Введите корректное число!")
            }
        }
    }


    override fun add() {
        println("Введите название пиццы!")
        val name = checkName()
        if (allPizzas.find { it.name.lowercase() == name.lowercase() } != null) {
            println("Такая пицца уже есть!")
            return
        }

        println("Выберите основу:")
        val sortedBases = basesList.sortedWith(compareBy { it.price })
        sortedBases.forEachIndexed { i, base -> println("${i + 1}: $base.") }
        val baseInd = checkIndex(basesList)

        println("Выберите размер:")
        PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
        val ind = checkIndex(PizzaSize.values().toList())
        val size = PizzaSize.values()[ind - 1]

        var border: Border? = null;
        if (borderList.isEmpty()) println("Бортиков нет!")
        else {
            println("Выберите бортик!")
            val sortedBorders = borderList.sortedWith(borderByPriceAsc)
            sortedBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
            println("${borderList.size + 1}: Пропустить")
            val borderInd = checkIndex(borderList)
            if (borderInd != borderList.size + 1) {
                if (borderList[borderInd - 1].checkPizza(name)) border = borderList[borderInd - 1]
                else println("Нельзя совмещать этот бортик с этой пиццей")
            }
        }

        val ingredientOnPizza = mutableListOf<Ingredient>()
        while (true) {
            println("Выбери ингредиент, который хотите добавить!")
            val sortedIngredients = ingredientsList.sortedWith(compareBy { it.price })
            sortedIngredients.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient.") }
            println("${ingredientsList.size + 1}: Закончить")

            val ingrInd = checkIndex(ingredientsList)
            if (ingrInd == ingredientsList.size + 1) break

            ingredientOnPizza.add(ingredientsList[ingrInd - 1])
        }
        allPizzas.add(Pizza(name, basesList[baseInd - 1], size, border, ingredientOnPizza))
        println("Пицца успешно добавлена!")
    }



    override fun del() {
        if (allPizzas.isEmpty()) return println("Пусто!")
        show()

        println("Введите номер пиццы!")
        val pizzaInd = checkIndex(allPizzas)
        if (pizzaInd == allPizzas.size + 1) return
        allPizzas.removeAt(pizzaInd - 1)
        println("Успешно удалено!")
    }



    override fun edit() {
        if (allPizzas.isEmpty()) {
            println("Пусто!")
            return
        }
        show()

        println("Какую пиццу редактируем?")
        val pizzaInd = checkIndex(allPizzas)
        if (pizzaInd == allPizzas.size + 1) return
        val curPizza = allPizzas[pizzaInd - 1]
        editRegularPizza(curPizza)

        println("Изменения прошли успешно!")
    }

    private fun editRegularPizza(curPizza: Pizza) {
        while (true) {
            println(
                """
                    Что меняем? Выберите цифру
                        1. Название
                        2. Основа
                        3. Размер
                        4. Бортик
                        5. Ингредиенты
                        6. Назад
                """.trimIndent()
            )
            val input = readln()
            when (input) {
                "1" -> {
                    println("Введите новое название пиццы:")
                    var name = checkName()
                    curPizza.name = ""
                    while (allPizzas.find { it.name.lowercase() == name.lowercase() } != null) {
                        println("Такая пицца уже есть!")
                        name = checkName()
                    }
                    curPizza.name = name
                }

                "2" -> {
                    println("Выберите новую основу:")
                    basesList.forEachIndexed { i, base -> println("${i + 1}: $base.") }
                    println("${basesList.size + 1}: Не менять")
                    val baseInd = checkIndex(basesList)
                    if (baseInd != basesList.size + 1) curPizza.base = basesList[baseInd - 1]
                }

                "3" -> {
                    println("Выберите новый размер:")
                    PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
                    println("${PizzaSize.values().size + 1}: Не менять")
                    val sizeInd = checkIndex(PizzaSize.values().toList())
                    if (sizeInd != PizzaSize.values().size + 1) curPizza.size = PizzaSize.values()[sizeInd - 1]
                }

                "4" -> {
                    if (borderList.isEmpty()) println("Бортиков нет!")
                    else {
                        println("Выберите бортик:")
                        borderList.forEachIndexed { i, border -> println("${i + 1}: $border") }
                        println("${borderList.size + 1}: Удалить нынешний")
                        println("${borderList.size + 2}: Не изменять")
                        val borderInd = checkIndex(borderList)
                        if (borderInd == borderList.size + 1) curPizza.border = null
                        else if (borderInd != borderList.size + 2) {
                            if (borderList[borderInd - 1].checkPizza(curPizza.name)) curPizza.border = borderList[borderInd - 1]
                            else println("Нельзя совмещать этот бортик с этой пиццей")
                        }
                    }
                }

                "5" -> {
                    while (true) {
                        println("Выберите номер ингредиента, который хотите заменить:")
                        println(curPizza.name)
                        curPizza.ingredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                        println("${curPizza.ingredients.size + 1}: Не менять")

                        val indIngr = checkIndex(curPizza.ingredients)
                        if (indIngr == curPizza.ingredients.size + 1) break

                        println("Выберите номер ингредиента, на который хотите заменить прошлый!")
                        ingredientsList.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                        var indAllIngr = checkIndex(ingredientsList)
                        while (indAllIngr == curPizza.ingredients.size + 1) {
                            println("Введите корректное число!")
                            indAllIngr = checkIndex(ingredientsList)
                        }
                        curPizza.ingredients[indIngr - 1] = ingredientsList[indAllIngr - 1]
                    }
                }

                "6" -> break
            }
        }
    }

    override fun show() {
        if (allPizzas.isEmpty()) return println("Пусто!")
        allPizzas.forEachIndexed { i, pizza ->
            println("======== Пицца №${i + 1} ========")
            pizza.Print()
        }
        println("${allPizzas.size + 1}: Назад!")
    }


    fun get() = allPizzas




    override fun sort() {
        if (allPizzas.isEmpty()) return println("Пусто!")
        println(
            """
            === Сортировка и фильтрация ===
            1. Сортировать по названию (А-Я)
            2. Сортировать по названию (Я-А)
            3. Сортировать по цене (возрастание)
            4. Сортировать по цене (убывание)
            5. Фильтровать по ингредиенту
            6. Фильтровать по цене
            7. Фильтровать по размеру
            8. Назад
        """.trimIndent()
        )

        when (readln()) {
            "1" -> {
                allPizzas.sortWith(pizzaByNameAsc)
                println("Отсортировано по названию (А-Я)")
                show()
            }

            "2" -> {
                allPizzas.sortWith(pizzaByNameDesc)
                println("Отсортировано по названию (Я-А)")
                show()
            }

            "3" -> {
                allPizzas.sortWith(pizzaByPriceAsc)
                println("Отсортировано по цене (возрастание)")
                show()
            }

            "4" -> {
                allPizzas.sortWith(pizzaByPriceDesc)
                println("Отсортировано по цене (убывание)")
                show()
            }

            "5" -> {
                println("Выберите ингредиент для фильтрации:")
                ingredientsList.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                println("${ingredientsList.size + 1}: Назад")
                val indIngr = checkIndex(ingredientsList)
                if (indIngr == ingredientsList.size + 1) return
                
                val filteredPizzas = filterByIngredient(ingredientsList[indIngr - 1])
                showFiltered(filteredPizzas)
            }

            "6" -> {
                println("Минимальная цена (enter для пропуска)")
                val minPrice = checkPrice()
                println("Максимальная цена (enter для пропуска)")
                val maxPrice = checkPrice()
                
                val filteredPizzas = filterByPriceRange(minPrice, maxPrice)
                showFiltered(filteredPizzas)
            }

            "7" -> {
                println("Выберите размер для фильтрации:")
                PizzaSize.values().forEachIndexed { i, size -> println("${i + 1}: $size")}
                println("${PizzaSize.values().size + 1}: Назад")
                val indSize = checkIndex(PizzaSize.values().toList())
                if (indSize == PizzaSize.values().size + 1) return
                
                val filteredPizzas = filterBySize(PizzaSize.values()[indSize - 1])
                showFiltered(filteredPizzas)
            }

            "8" -> return
            else -> println("Некорректный выбор!")
        }
    }

    private fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Pizza> {
        return allPizzas.filter { pizza ->
            val price = pizza.finalPrice()
            price in minPrice..maxPrice
        }
    }

    private fun showFiltered(list: List<Pizza>) {
        if (allPizzas.isEmpty()) {
            println("Ничего не найдено!")
            return
        }
        val sortedPizzas = allPizzas.sortedWith(pizzaByPriceAsc)
        sortedPizzas.forEachIndexed { i, pizza ->
            println("Пицца №${i + 1}")
            pizza.Print()
        }
    }

    private fun filterByIngredient(ingredient: Ingredient): List<Pizza> {
        return allPizzas.filter { pizza ->
            pizza.ingredients.any { it.name.lowercase() == ingredient.name.lowercase() }
        }
    }

    fun filterBySize(size: PizzaSize): List<Pizza> {
        return allPizzas.filter { it.size == size }
    }

}



