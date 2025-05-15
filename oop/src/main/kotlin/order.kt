package org.example

import java.time.LocalDateTime
import java.util.UUID
import kotlin.collections.mutableListOf

data class Order(
    var id: UUID = UUID.randomUUID(),
    var pizzas: MutableList<Pizza> = mutableListOf(),
    var comment : String = "",
    var timeCreate: LocalDateTime = LocalDateTime.now(),
    var delayed : LocalDateTime? = null
){
    fun totalPrice(): Double{
        var res = 0.0;
        pizzas.forEachIndexed { i, pizza -> res += pizza.finalPrice()}
        return res
    }
}

class ManageOrders(
    val allPizza: List<Pizza>,
    val allIngredients: List<Ingredient>,
    val allBases: List<Base>,
    val allBorders : List<Border>,
    ) : baseFunction{
    private val orders = mutableListOf<Order>()


    fun menu(){
        var contin = true
        while (contin) {
            println("""
                  === Заказы ===
                1. Добавить заказ
                2. Удалить заказ
                3. Изменить заказ
                4. Показать все заказы
                5. Фильтрация
                6. Назад
            """.trimIndent())
            val input = readln()
            when (input) {
                "1" -> add()
                "2" -> del()
                "3" -> edit()
                "4" -> show()
                "5" -> filter()
                "6" -> contin = false
            }
        }
    }


    override fun add() {
        println("Выбрать готовую пиццу или собрать вручную? (готовая/ручная)")
        var check = readln()
        while (check.lowercase() != "готовая" || check.lowercase() != "ручная") {
            println("Введите корректную команду!")
            check = readln()
        }
        val curPizza : MutableList<Pizza> = mutableListOf()
        if (check.lowercase() == "готовая") {
            if (allPizza.isEmpty()) return println("Пусто")
            println("Выбери пиццу")
            allPizza.forEachIndexed { i, pizza -> println("${i + 1}: $pizza") }
            println("${allPizza.size + 1}: Назад")

            val indOrder = checkIndex(allPizza)
            if (indOrder == allPizza.size + 1) return

            curPizza.add(allPizza[indOrder - 1])

        }
        else {
            println("Название пиццы!")
            val name = checkName()
            val selIngredient = mutableListOf<Ingredient>()

            println("Выберите основание для пиццы:")
            allBases.forEachIndexed { i, base -> println("${i + 1}: $base") }
            val baseIndex = checkIndex(allBases)
            val selectedBase = allBases[baseIndex - 1]

            while(true){
                println("Выберите ингредиент!")
                allIngredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                println("${allIngredients.size + 1}: Далее")

                val indOrder = checkIndex(allIngredients)
                if (indOrder == allIngredients.size + 1) break
                selIngredient.add(allIngredients[indOrder - 1])
            }

            println("Выберите размер пиццы!")
            PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
            var ind = checkIndex(PizzaSize.values().toList())
            val size = PizzaSize.values()[ind - 1]

            var border: Border? = null;
            if(allBorders.isEmpty()) println("Бортиков нет!")
            else {
                println("Выберите бортик!")
                allBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
                println("${allBorders.size + 1}: Пропустить")
                ind = checkIndex(allBorders)
                if (ind != allBorders.size + 1) border = allBorders[ind - 1]
            }
            curPizza.add(Pizza(name, selectedBase, size, border, selIngredient))
        }


        println("Комментарий!(если не нужен, то нажмите enter)")
        val com = readln()

        var delayed : LocalDateTime? = null
        println("Делаем заказ отложенным? ('да' или 'нет')")
        check = readln()
        if (check.lowercase() == "да") {
            println("Введите дату и время в формате 'гггг-мм-дд чч:мм (нажмите enter если не нужно)'")
            while (true) {
                try {
                    val time = readln()
                    if(time.isBlank()) break
                    val (datePart, timePart) = time.split(" ")
                    val (year, month, day) = datePart.split("-").map { it.toInt() }
                    val (hour, minute) = timePart.split(":").map { it.toInt() }
                    delayed = LocalDateTime.of(year, month, day, hour, minute)
                    break
                } catch (e: Exception) {
                    println("Неверный формат. Попробуйте ещё раз: 'гггг-мм-дд чч:мм (нажмите enter если не нужно)'")
                }
            }
        }

        orders.add(Order(pizzas = curPizza, comment = com, delayed = delayed))
        println("Заказ успешно добавлен!")
    }

    override fun del() {
        if (orders.isEmpty()) return println("Пусто!")
        println("Выберите номер заказа для удаления!")
        val numOrd = 1
        for (order in orders) {
            println("=== $numOrd заказ ===")
            val numberPi = 1
            for (pizza in order.pizzas) {
                println("№$numberPi пицца в заказе")
                println("$pizza")
            }
        }
        println("${orders.size + 1}: Назад")

        val ind = checkIndex(orders)
        if (ind == orders.size + 1) return

        orders.removeAt(ind - 1)
        println("Заказ успешно удалён!")
    }


    override fun edit() {
        while (true) {
            if (orders.isEmpty()) return println("Пусто!")
            println("Выберите номер заказа для изменения!")

            val numOrd = 1
            for (order in orders) {
                println("=== $numOrd заказ ===")
                val numberPi = 1
                for (pizza in order.pizzas) {
                    println("№$numberPi пицца в заказе")
                    println("$pizza")
                }
            }
            println("${orders.size + 1}: Назад")

            val ind = checkIndex(orders)
            if (ind == orders.size + 1) break
            val curOrder = orders[ind - 1]

            while (true) {
                println(
                    """
                Что меняем? Выберите цифру
                1. Пиццы
                2. Комментарий
                3. Время откладки
                4. Назад
            """.trimIndent()
                )
                val input = readln()
                when (input) {
                    "1" -> {
                        println("Выберите пиццу")
                        curOrder.pizzas.forEachIndexed { i, pizza -> println("${i + 1}: $pizza") }
                        println("${orders.size + 1}: Назад")
                        val pizzaInd = checkIndex(curOrder.pizzas)
                        if (pizzaInd != orders.size + 1) continue
                        val curPizza = curOrder.pizzas[pizzaInd - 1]


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
                                while (allPizza.find { it.name.lowercase() == name.lowercase() } != null ||
                                    curOrder.pizzas.find { it.name.lowercase() == name.lowercase() } != null) {
                                    println("Такая пицца уже есть!")
                                    name = checkName()
                                }
                                curPizza.name = name
                            }

                            "2" -> {
                                println("Выберите новую основу:")
                                allBases.forEachIndexed { i, base -> println("${i + 1}: $base.") }
                                println("${allBases.size + 1}: Не менять")
                                val baseInd = checkIndex(allBases)
                                if (baseInd != allBases.size + 1) curPizza.base = allBases[baseInd - 1]
                            }

                            "3" -> {
                                println("Выберите новый размер:")
                                PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
                                println("${PizzaSize.values().size + 1}: Не менять")
                                val sizeInd = checkIndex(PizzaSize.values().toList())
                                if (sizeInd != PizzaSize.values().size + 1) curPizza.size = PizzaSize.values()[sizeInd - 1]
                            }

                            "4" -> {
                                if (allBorders.isEmpty()) println("Бортиков нет!")
                                else {
                                    println("Выберите бортик:")
                                    allBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
                                    println("${allBorders.size + 1}: Удалить нынешний")
                                    println("${allBorders.size + 2}: Не изменять")
                                    val borderInd = checkIndex(allBorders)
                                    if (borderInd == allBorders.size + 1) curPizza.border = null
                                    else if (borderInd != allBorders.size + 2) curPizza.border = allBorders[borderInd - 1]
                                }
                            }
                            "5"->{
                                while (true) {
                                    println("Выберите номер ингредиента, который хотите заменить:")
                                    println(curPizza.name)
                                    curPizza.ingredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                                    println("${curPizza.ingredients.size + 1}: Не менять")

                                    val indIngr = checkIndex(curPizza.ingredients)
                                    if (indIngr == curPizza.ingredients.size + 1) break

                                    println("Выберите номер ингредиента, на который хотите заменить прошлый!")
                                    allIngredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                                    var indAllIngr = checkIndex(allIngredients)
                                    while(indAllIngr == curPizza.ingredients.size + 1) {
                                        println("Введите корректное число!")
                                        indAllIngr = checkIndex(allIngredients)
                                    }
                                    curPizza.ingredients[indIngr - 1] = allIngredients[indAllIngr - 1]
                                }
                            }
                            "6"-> break
                        }
                    }
                    "2"->{
                        print("Новый комментарий: (нажмите enter если не хотите)")
                        val com = readln()
                        curOrder.comment = com
                    }

                    "3"->{
                        println("Делаем заказ отложенным? ('да' или 'нет')")
                        var check = readln()
                        if (check.lowercase() == "да") {
                            println("Введите дату и время в формате 'гггг-мм-дд чч:мм (нажмите enter если не нужно)'")
                            while (true) {
                                try {
                                    val time = readln()
                                    if (time.isBlank()) break
                                    val (datePart, timePart) = time.split(" ")
                                    val (year, month, day) = datePart.split("-").map { it.toInt() }
                                    val (hour, minute) = timePart.split(":").map { it.toInt() }
                                    curOrder.delayed = LocalDateTime.of(year, month, day, hour, minute)
                                    break
                                } catch (e: Exception) {
                                    println("Неверный формат. Попробуйте ещё раз: 'гггг-мм-дд чч:мм (нажмите enter если не нужно)'")
                                }
                            }
                        }
                    }
                    "4"->break
                }
            }
        }
    }

    override fun show() {
        if (orders.isEmpty()) {
            println("Нет заказов для отображения.")
            return
        }

        println("=== Список заказов ===")
        println("--------------------------------------------------------")
        orders.forEachIndexed { i, order ->
            println("Заказ №${i + 1}")
            println("ID: ${order.id}")
            var numberPi = 1
            for (pizza in order.pizzas) {
                println("№$numberPi пицца в заказе ")
                println("$pizza")
                numberPi++
            }
            println("Комментарий: ${order.comment}")
            println("Создан: ${order.timeCreate}")
            if (order.delayed != null) println("Отложен до: ${order.delayed}")
            println("--------------------------------------------------------")
        }
    }


    fun filter(){
        println("""
            === Фильтруем ===
            1. По Дате
            2. По цене
            3. По отложенной задержке
            4. Назад
        """.trimIndent())
        val input = readln()
        when (input) {
            "1" ->{
                println("Введите дату, по которой фильтруем (ДД.ММ.ГГГГ):")
                val input = readln()
                try {
                    val date = input.split(".").map { it.toInt() }
                    val listOrder = filterByDate(date[2],date[1], date[0])
                    showFilteredOrders(listOrder)
                }
                catch (e: NumberFormatException){
                    println("Неправильно введена дата! (ДД.ММ.ГГГГ)")
                }
            }
            "2"->{
                println("Минимальная цена(если минимальный порог не нужен, то нажмите enter)")
                val minPrice = checkPrice()
                println("Максимальная цена(если максимальный порог не нужен, то нажмите enter)")
                val maxPrice = checkPrice()
                val listOrder = filterByPriceRange(minPrice, maxPrice)

                showFilteredOrders(listOrder)
            }
            "3" -> {
                println("Вам нужны заказы отложенные?('да' или 'нет')")
                var input = readln()
                while (input.lowercase() !="да" || input.lowercase() !="нет" || input.isBlank()){
                    println("Введите 'да' или 'нет'")
                    input = readln()
                }
                val listOrder : List<Order>
                if (input.lowercase() =="да") listOrder = filterDelayedOrders(1)
                else listOrder = filterDelayedOrders(2)

                showFilteredOrders(listOrder)
            }
            "4"-> return
        }
    }

    private fun filterByDate(year: Int, month: Int, day: Int): List<Order> {
        return orders.filter { order ->
            val orderDate = order.timeCreate.toLocalDate()
            orderDate.year == year && orderDate.monthValue == month && orderDate.dayOfMonth == day
        }
    }

    private fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Order> {
        return orders.filter { order ->
            val totalPrice = order.totalPrice()
            totalPrice in minPrice..maxPrice
        }
    }

    private fun filterDelayedOrders(priority: Int): List<Order> {
        return if(priority == 1) orders.filter { it.delayed != null }
        else orders.filter { it.delayed == null }
    }

    private fun showFilteredOrders(listOrder:List<Order>){
        println("=== Список заказов ===")
        if(listOrder.isEmpty()) {
            println("Список пуст!")
            return
        }
        listOrder.forEachIndexed { i, order -> {
            println("--------------------------------------------------------")
            println("Заказ №${i + 1}")
            println("ID: ${order.id}")
            var numberPi = 1
            for (pizza in order.pizzas) {
                println("№$numberPi пицца в заказе ")
                println("$pizza")
                numberPi++
            }
            println("Комментарий: ${order.comment}")
            println("Создан: ${order.timeCreate}")
            if (order.delayed != null) println("Отложен до: ${order.delayed}")
            println("--------------------------------------------------------")

        } }
    }
}