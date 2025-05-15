package org.example

class Manage {
    private val ingredients = ManageIngredient()
    private val bases = ManageBasePizza()
    private val borders = ManageBorders(ingredients.get())
    private val pizza = ManagePizza(ingredients.get(), bases.get(), borders.get())
    private val orders = ManageOrders(pizza.get(), ingredients.get(), bases.get(), borders.get())


    fun menu(){
        var contin = true
        while (contin) {
            println(
                """
                   === Главное меню ===
                    = Введите число =
                1. Управление ингредиентами
                2. Управление основами
                3. Управление бортиками
                4. Управление пиццами
                5. Оформить заказ
                6. Выход
                """.trimIndent()
            )
            val input = readLine()
            when(input){
                "1" -> ingredients.menu()
                "2" -> bases.menu()
                "3" -> borders.menu()
                "4" -> pizza.menu()
                "5" -> orders.menu()
                "6" -> contin = false
                else -> println("Введите корректное число!")
            }
        }
    }
}