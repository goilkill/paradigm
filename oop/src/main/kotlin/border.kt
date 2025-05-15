package org.example

data class Border(
    var name: String,
    var ingredients: MutableList<Ingredient>,
    var allowed: MutableList<String> = mutableListOf<String>(),
    var banned: MutableList<String> = mutableListOf<String>()
){
    private val BASE_COST = 20.0
    override fun toString() = "$name = ${borderPrice()}"

    fun checkPizza(namePizza: String) : Boolean {
        if (allowed.isNotEmpty() && namePizza in allowed) return true
        else if (banned.isNotEmpty() && namePizza in banned) return false
        else return true
    }

    fun borderPrice() : Double{
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
        println("Введите название!")
        val name = checkName()

        val selectedIngredients = mutableListOf<Ingredient>()
        while(true) {
            println("Выберите ингредиенты для бортика:")
            ingredients.forEachIndexed { i, ingredient -> println("${i + 1}: $ingredient") }
            println("${ingredients.size + 1}: Готово")

            var ingrBorder = checkIndex(ingredients)
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
        else{
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
        if(allBorders.isEmpty()) return println("Пусто!")
        allBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
        println("${allBorders.size + 1}: Назад!")
    }

    fun get(): List<Border> = allBorders
}