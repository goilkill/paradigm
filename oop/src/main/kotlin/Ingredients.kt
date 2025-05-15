package org.example



data class Ingredient (var name: String, var price: Double){
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
        if (allIngredients.isEmpty()) {
            println("Пусто!")
            return
        }
        println("Ингридиенты: ")
        allIngredients.forEachIndexed { i, ingredient -> println("${i + 1} : $ingredient") }
        println("${allIngredients.size + 1}: Назад!")
    }

    fun get(): List<Ingredient> = allIngredients

}