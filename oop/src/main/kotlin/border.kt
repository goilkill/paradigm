package org.example

data class Border(
    var name: String,
    var ingredients: MutableList<Ingredient>,
    var allowed: MutableList<String> = mutableListOf(),
    var banned: MutableList<String> = mutableListOf()
) {
    private val BASE_COST = 20.0
    override fun toString() = "$name = ${borderPrice()}"

    fun checkPizza(namePizza: String) : Boolean {
        if (allowed.isNotEmpty() && namePizza in allowed) return true
        else if (banned.isNotEmpty() && namePizza in banned) return false
        else return true
    }

    fun borderPrice() : Double {
        return ingredients.sumOf { it.price } + BASE_COST
    }
}

class ManageBorders(private val ingredients: List<Ingredient>) : baseFunction {
    private val allBorders = mutableListOf<Border>()

    fun menu(){
        var contin = true
        while (contin) {
            println(
                """
                  === Бортики ===
                1. Добавить бортик
                2. Удалить бортик
                3. Изменить бортик
                4. Показать все бортики
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

    override fun sort() {
        if (allBorders.isEmpty()) {
            println("Пусто!")
            return
        }
        println("""
            === Сортировка ===
            1. По названию (А-Я)
            2. По названию (Я-А)
            3. По цене (возрастание)
            4. По цене (убывание)
            5. Фильтровать по цене (интревал)
            6. Фильтровать по ингредиенту
            7. Назад
        """.trimIndent())

        when(readln()) {
            "1" -> {
                allBorders.sortWith(borderByNameAsc)
                println("Отсортировано по названию (А-Я)")
                show()
            }
            "2" -> {
                allBorders.sortWith(borderByNameDesc)
                println("Отсортировано по названию (Я-А)")
                show()
            }
            "3" -> {
                allBorders.sortWith(borderByPriceAsc)
                println("Отсортировано по цене (возрастание)")
                show()
            }
            "4" -> {
                allBorders.sortWith(borderByPriceDesc)
                println("Отсортировано по цене (убывание)")
                show()
            }
            "5" -> {
                println("Минимальная цена (enter для пропуска)")
                val minPrice = checkPrice()
                println("Максимальная цена (enter для пропуска)")
                val maxPrice = checkPrice()

                val filteredBases = filterByPriceRange( minPrice, maxPrice)
                showFiltered(filteredBases)
            }
            "6" -> {
                println("Выберите ингредиент для фильтрации:")
                ingredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                println("${ingredients.size + 1}: Назад")
                val indIngr = checkIndex(ingredients)
                if (indIngr == ingredients.size + 1) return

                val filteredBorders = filterByIngredient(ingredients[indIngr - 1])
                showFiltered(filteredBorders)
            }
            "7" -> return
            else -> println("Некорректный выбор!")
        }
    }

    override fun add() {
        println("Введите название!")
        val name = checkName()

        val selectedIngredients = mutableListOf<Ingredient>()
        while(true) {
            println("Выберите ингредиенты для бортика:")
            val sortedIngredients = ingredients.sortedWith(compareBy { it.price })
            sortedIngredients.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient") }
            println("${ingredients.size + 1}: Готово")

            val ingrBorder = checkIndex(ingredients)
            if(ingrBorder == ingredients.size + 1) break

            selectedIngredients.add(ingredients[ingrBorder - 1])
        }

        val allowed = mutableListOf<String>()
        val banned = mutableListOf<String>()

        println("Добавить пиццы, с которыми можно использовать данный бортик? (да или нет)")
        var check = readln()
        if (check.lowercase() == "да"){
            println("Вводите название пиццы одно на строку, если всё внесли, то нажмите enter!")
            while(true){
                check = readln()
                if (check.isBlank()) break
                allowed.add(check)
            }
        }
        println("Добавить пиццы, с которыми нельзя использовать данный бортик? (да или нет)")
        check = readln()
        if (check.lowercase() == "да"){
            println("Вводите название пиццы одно на строку, если всё внесли, то нажмите enter!")
            while(true){
                check = readln()
                if (check.isBlank()) break
                banned.add(check)
            }
        }
        allBorders.add(Border(name, selectedIngredients, allowed, banned))
        println("Добавлено!")
    }

    override fun del() {
        if(allBorders.isEmpty()) return println("Пусто!")
        show()
        println("Введите номер бортика!")
        val ind = checkIndex(allBorders)
        if (ind == allBorders.size + 1) return
        allBorders.removeAt(ind - 1)
        println("Успешно удалено!")
    }

    override fun edit() {
        if(allBorders.isEmpty()) return println("Пусто!")
        show()
        println("Введите номер бортика!")
        val ind = checkIndex(allBorders)
        if (ind == allBorders.size + 1) return
        val curBorder = allBorders[ind - 1]


        println("Новое название (нажать enter если не надо)!")
        val newName = readLine()
        if(!newName.isNullOrBlank()) curBorder.name = newName

        println("Меняем ингредиенты? ('да' или 'нет')")
        var check = readln()
        if (check.lowercase() == "да"){
            val newIngredient = mutableListOf<Ingredient>()
            while(true) {
                println("Выберите ингредиент, который хотите добавить!")
                ingredients.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient") }
                println("${ingredients.size + 1}: Конец")

                val indIngr = checkIndex(ingredients)
                if (indIngr == ingredients.size + 1) break

                newIngredient.add(ingredients[indIngr - 1])
                println("Успешно добавлено!")
            }
            curBorder.ingredients = newIngredient

        }
        println("Меняем пиццы, с которыми можно использовать, а с какими нет? ('да' или 'нет')")
        check = readln()
        if (check.lowercase() == "да") {
            println("Добавить пиццы, с которыми можно использовать данный бортик? (да или нет)")
            var check = readln()
            if (check.lowercase() == "да") {
                while(true) {
                    println("=== Разрешенные пиццы ===")
                    curBorder.allowed.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient") }
                    println("${curBorder.allowed.size + 1}: Отмена")
                    println("Введите номер пиццы, которое хотите заменить")
                    val ind = checkIndex(curBorder.allowed)
                    if (ind == curBorder.allowed.size + 1) break

                    println("Введите название новой пиццы, на которую хотите заменить!")
                    val newName = checkName()
                    curBorder.allowed[ind - 1] = newName
                }
            }
            println("Добавить пиццы, с которыми нельзя использовать данный бортик? (да или нет)")
            check = readln()
            if (check.lowercase() == "да") {
                while (true) {
                    println("=== Запрещенные пиццы ===")
                    curBorder.banned.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient") }
                    println("${curBorder.banned.size + 1}: Отмена")
                    println("Введите номер пиццы, которое хотите заменить")
                    val ind = checkIndex(curBorder.banned)
                    if (ind == curBorder.banned.size + 1) break

                    println("Введите название новой пиццы, на которую хотите заменить!")
                    val newName = checkName()
                    curBorder.banned[ind - 1] = newName
                }
            }

        }
        println("Изменения успешно добавлены!")
    }

    override fun show() {
        if(allBorders.isEmpty()) {
            println("Пусто!")
            return
        }
        allBorders.forEachIndexed { i, border -> println("${i + 1} : $border") }
        println("${allBorders.size + 1}: Назад!")
    }

    fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Border> {
        return allBorders.filter { border ->
            val price = border.borderPrice()
            price in minPrice..maxPrice
        }
    }

    fun filterByIngredient(ingredient: Ingredient): List<Border> {
        return allBorders.filter { border ->
            border.ingredients.any { it.name.lowercase() == ingredient.name.lowercase() }
        }
    }




    fun showFiltered(borders: List<Border>) {
        if (borders.isEmpty()) {
            println("Ничего не найдено!")
            return
        }
        val sortedBases = borders.sortedWith(borderByPriceAsc)
        sortedBases.forEachIndexed { i, border ->
            println("Пицца №${i + 1}")
            border.toString()
        }
    }


    fun get(): List<Border> = allBorders
}