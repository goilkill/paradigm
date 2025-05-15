package org.example

data class Pizza(
    var name: String,
    var base: Base,
    var size: PizzaSize,
    var border: Border?,
    var ingredients: MutableList<Ingredient>
) {
    fun finalPrice(): Double {
        var ingrPrice = ingredients.sumOf { it.price }
        if (border != null) ingrPrice += border!!.borderPrice()
        if (size == PizzaSize.SMALL) ingrPrice += 50.0
        else if(size == PizzaSize.MEDIUM) ingrPrice += 100.0
        else ingrPrice += 150.0
        return base.price + ingrPrice
    }

    override fun toString() = """
        Название: $name 
        Основа: $base 
        Ингредиенты: ${ingredients.joinToString()}
        Бортик: $border
        Размер: $size
        Сумма пиццы: ${finalPrice()}
    """.trimIndent()
}

class ManagePizza(private val ingredientsList: List<Ingredient>, private val basesList: List<Base>, private val borderList: List<Border>) : baseFunction {
    private var allPizzas = mutableListOf<Pizza>()

    fun menu(){
        var contin = true
        while (contin) {
            println(
                """
                  === Пиццы ===
                1. Добавить пиццу
                2. Удалить пиццу
                3. Изменить пиццу
                4. Показать все пиццы
                5. Назад
                """.trimIndent()
            )
            val input = readLine()
            when(input){
                "1" -> add()
                "2" -> del()
                "3" -> edit()
                "4" -> show()
                "5" -> contin = false
                else -> println("Введите корректное число!")
            }
        }
    }


    override fun add() {
        println("Введите название пиццы!")
        val name = checkName()
        if (allPizzas.find { it.name.lowercase() == name.lowercase()} != null) {
            println("Такая пицца уже есть!")
            return
        }

        println("Выберите основу:")
        basesList.forEachIndexed { i, base -> println("${i + 1}: $base.") }
        val baseInd = checkIndex(basesList)

        println("Выберите размер:")
        PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
        var ind = checkIndex(PizzaSize.values().toList())
        val size = PizzaSize.values()[ind - 1]

        var border: Border? = null;
        if(borderList.isEmpty()) println("Бортиков нет!")
        else {
            println("Выберите бортик!")
            borderList.forEachIndexed { i, border -> println("${i + 1}: $border") }
            println("${borderList.size + 1}: Пропустить")
            ind = checkIndex(borderList)
            if (ind != borderList.size + 1) border = borderList[ind - 1]
        }


        val ingredientOnPizza = mutableListOf<Ingredient>()
        while (true) {
            println("Выбери ингредиент, который хотите добавить!")
            ingredientsList.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient.") }
            println("${ingredientsList.size + 1}: Закончить")

            val ingrInd = checkIndex(ingredientsList)
            if(ingrInd == ingredientsList.size + 1) break

            ingredientOnPizza.add(ingredientsList[ingrInd - 1])

        }
        allPizzas.add(Pizza(name,basesList[baseInd - 1], size, border, ingredientOnPizza))
        println("Пицца успешно добавлена!")
    }

    override fun del() {
        if(allPizzas.isEmpty()) return println("Пусто!")
        show()

        println("Введите номер пиццы!")
        val pizzaInd = checkIndex(allPizzas)
        if(pizzaInd == allPizzas.size + 1) return
        allPizzas.removeAt(pizzaInd - 1)
        println("Успешно удалено!")
    }

    override fun edit() {
        if(allPizzas.isEmpty()) {
            println("Пусто!")
            return
        }
        show()

        println("Какую пиццу редактируем?")
        val pizzaInd = checkIndex(allPizzas)
        if (pizzaInd == allPizzas.size + 1) return
        val curPizza = allPizzas[pizzaInd - 1]
        while(true) {
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
                        else if (borderInd != borderList.size + 2) curPizza.border = borderList[borderInd - 1]
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
        println("Изменения прошли успешно!")
    }

    override fun show() {
        if(allPizzas.isEmpty()) return println("Пусто!")
        allPizzas.forEachIndexed { i, pizza -> {
            println("Пицца №${i + 1}")
            println(pizza)
        } }
        println("${allPizzas.size + 1}: Назад!")
    }

    fun get() = allPizzas



    fun filter(){
        println("""
            === Фильтруем ===
            1. По ингредиентам
            2. По цене
            3. По размеру
            4. Назад
        """.trimIndent())
        val input = readln()
        when (input) {
            "1" ->{
                println("Выберите номер ингредиента, по которому фильтруем:")
                ingredientsList.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                println("${ingredientsList.size + 1}: Назад")
                val indIngr = checkIndex(ingredientsList)
                if (indIngr == ingredientsList.size + 1) return
                val listPizzas = filterByIngredient(ingredientsList[indIngr - 1])
                showFilteredPizzas(listPizzas)
            }
            "2"->{
                println("Минимальная цена(если минимальный порог не нужен, то нажмите enter)")
                val minPrice = checkPrice()
                println("Максимальная цена(если максимальный порог не нужен, то нажмите enter)")
                val maxPrice = checkPrice()
                val listPizzas = filterByPriceRange(minPrice, maxPrice)
                println("=== Список заказов ===")
                showFilteredPizzas(listPizzas)
            }
            "3"->{
                println("Выберите номер размера, по которому фильтруем:")
                PizzaSize.values().forEachIndexed { i, size -> println("${i + 1}: $size")}
                println("${ingredientsList.size + 1}: Назад")
                val indSize = checkIndex(PizzaSize.values().toList())
                if (indSize == PizzaSize.values().size + 1) return
                val listPizzas = filterBySize(PizzaSize.values()[indSize - 1])
                showFilteredPizzas(listPizzas)
            }
            "4"-> return
        }
    }

    private fun filterByIngredient(ingredient: Ingredient): List<Pizza> {
        return allPizzas.filter { pizza ->
            pizza.ingredients.any { it.name.lowercase() == ingredient.name.lowercase() }
        }
    }

    private fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Pizza> {
        return allPizzas.filter { pizza ->
            val price = pizza.finalPrice()
            price in minPrice..maxPrice
        }
    }

    private fun filterBySize(size: PizzaSize): List<Pizza> {
        return allPizzas.filter { it.size == size }
    }

    private fun showFilteredPizzas(listPizzas:List<Pizza>){
        if(listPizzas.isEmpty()) {
            println("Список пуст!")
            return
        }
        listPizzas.forEachIndexed { i, pizza -> {
            println("Пицца №${i + 1}")
            println(pizza)
        } }
    }
}