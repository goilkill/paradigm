package org.example

import java.time.LocalDateTime
import java.util.UUID

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
    ) : baseFunction {
    private val allOrders = mutableListOf<Order>()


    fun menu() {
        var contin = true
        while (contin) {
            println(
                """
                  === Заказы ===
                1. Добавить заказ
                2. Удалить заказ
                3. Изменить заказ
                4. Показать все заказы
                5. Фильтрация
                6. Назад
            """.trimIndent()
            )
            val input = readln()
            when (input) {
                "1" -> add()
                "2" -> del()
                "3" -> edit()
                "4" -> show()
                "5" -> sort()
                "6" -> contin = false
            }
        }
    }


    override fun add() {
        val curPizzas: MutableList<Pizza> = mutableListOf()
        while (true) {
            println("Добавление пиццы/еще одной пиццы")
            println("Выбрать готовую пиццу или собрать вручную? (готовая/ручная/ нажать enter - если ввел все пиццы в заказ)")
            var check = readln()
            if (check.isBlank()) break
            while (check.lowercase() != "готовая" && check.lowercase() != "ручная") {
                println("Введите корректную команду!")
                check = readln()
            }

            if (check.lowercase() == "готовая") {
                if (allPizza.isEmpty()) return println("Пусто")
                println("Выбери пиццу")
                allPizza.forEachIndexed { i, pizza -> println("${i + 1}: $pizza") }
                println("${allPizza.size + 1}: Назад")

                val indOrder = checkIndex(allPizza)
                if (indOrder == allPizza.size + 1) return

                curPizzas.add(allPizza[indOrder - 1])

            } else if (check.lowercase() == "ручная") {
                println(
                    """
                    Выберите тип пиццы:
                    1. Обычная пицца
                    2. Комбинированная пицца (из двух половинок)
                """.trimIndent()
                )
                val input = readln()
                when (input) {
                    "1" -> curPizzas.add(createRegularPizza())
                    "2" -> curPizzas.add(createCombinedPizza())
                    else -> {
                        println("Некорректный выбор!")
                        continue
                    }
                }
            }
        }

        println("Комментарий!(если не нужен, то нажмите enter)")
        val com = readln()

        var delayed: LocalDateTime? = null
        println("Делаем заказ отложенным? ('да' или 'нет')")
        val check = readln()
        if (check.lowercase() == "да") {
            println("Введите дату и время в формате 'гггг-мм-дд чч:мм (нажмите enter если не нужно)'")
            while (true) {
                try {
                    val time = readln()
                    if (time.isBlank()) break
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

        allOrders.add(Order(pizzas = curPizzas, comment = com, delayed = delayed))
        println("Заказ успешно добавлен!")
    }

    private fun createRegularPizza(): Pizza {
        println("Название пиццы!")
        val name = checkName()
        val selIngredient = mutableListOf<Ingredient>()

        println("Выберите основание для пиццы:")
        allBases.forEachIndexed { i, base -> println("${i + 1}: $base") }
        val baseIndex = checkIndex(allBases)
        val selectedBase = allBases[baseIndex - 1]

        while (true) {
            println("Выберите ингредиент!")
            allIngredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
            println("${allIngredients.size + 1}: Далее")

            val indOrder = checkIndex(allIngredients)
            if (indOrder == allIngredients.size + 1) break
            selIngredient.add(allIngredients[indOrder - 1])
        }

        println("Выберите размер пиццы!")
        PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
        val ind = checkIndex(PizzaSize.values().toList())
        val size = PizzaSize.values()[ind - 1]

        var border: Border? = null;
        if (allBorders.isEmpty()) println("Бортиков нет!")
        else {
            println("Выберите бортик!")
            allBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
            println("${allBorders.size + 1}: Пропустить")
            val borderInd = checkIndex(allBorders)
            if (borderInd != allBorders.size + 1) {
                if (allBorders[borderInd - 1].checkPizza(name)) border = allBorders[borderInd - 1]
                else println("Нельзя совмещать этот бортик с этой пиццей")
            }
        }
        return Pizza(name, selectedBase, size, border, selIngredient)
    }

    private fun createCombinedPizza(): Pizza {
        val firstHalf: Pizza
        val secondHalf: Pizza
        val name: String

        println("Делаем ручную или собираем из двух существующих пицц? ('да' - ручная пицца , 'нет' - из двух существующих)")
        var input = readln()
        while (input.isBlank() || input.lowercase() != "да" || input.lowercase() != "нет") {
            println("Введите 'да' - ручная пицца , 'нет' - из двух существующих")
            input  = readln()
        }
        if (input.lowercase() != "нет"){
            println("Введите название пиццы!")
            name = checkName()

            allPizza.forEachIndexed { i, pizza -> {
                println("Номер ${i + 1}")
                pizza.Print()
            } }
            println("Введите номер первой пиццы из списка (первая половинка):")
            var halfIndPizza = checkIndex(allPizza)
            firstHalf = allPizza[halfIndPizza - 1]
            firstHalf.name = "первая половина"

            println("Введите номер первой пиццы из списка (вторая половинка):")
            halfIndPizza = checkIndex(allPizza)
            secondHalf = allPizza[halfIndPizza - 1]
            secondHalf.name = "вторая половинка"
        }
        else {
            println("Введите название пиццы!")
            name = checkName()

            println("Создание первой половины пиццы")
            val firstHalfName = "первая половина"
            firstHalf = createHalfPizza(firstHalfName)

            println("Создание второй половины пиццы")
            val secondHalfName ="вторая половина"
            secondHalf = createHalfPizza(secondHalfName)
        }

        println("Общие параметры пиццы")
        println("Выберите основу:")
        allBases.forEachIndexed { i, base -> println("${i + 1}: $base") }
        val baseInd = checkIndex(allBases)

        println("Выберите размер пиццы!")
        PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
        val ind = checkIndex(PizzaSize.values().toList())
        val size = PizzaSize.values()[ind - 1]

        var border: Border? = null
        if (allBorders.isEmpty()) println("Бортиков нет!")
        else {
            println("Выберите бортик!")
            allBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
            println("${allBorders.size + 1}: Пропустить")
            val borderInd = checkIndex(allBorders)
            if (borderInd != allBorders.size + 1) {
                if (allBorders[borderInd - 1].checkPizza(name)) border = allBorders[borderInd - 1]
                else println("Нельзя совмещать этот бортик с этой пиццей")
            }
        }

        return CombinedPizza(name, firstHalf, secondHalf, allBases[baseInd - 1], size, border)
    }

    private fun createHalfPizza(name: String): Pizza {
        val selIngredient = mutableListOf<Ingredient>()

        while (true) {
            println("Выберите ингредиенты для ${name}:")
            allIngredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
            println("${allIngredients.size + 1}: Далее")

            val indOrder = checkIndex(allIngredients)
            if (indOrder == allIngredients.size + 1) break
            selIngredient.add(allIngredients[indOrder - 1])
        }

        return Pizza(
            name = name,
            base = null,
            size = PizzaSize.MEDIUM,
            border = null,
            ingredients = selIngredient
        )
    }

    override fun del() {
        if (allOrders.isEmpty()) return println("Пусто!")
        println("Выберите номер заказа для удаления!")
        var numOrd = 1
        for (order in allOrders) {
            println("=== $numOrd заказ ===")
            val numberPi = 1
            for (pizza in order.pizzas) {
                println("№$numberPi пицца в заказе")
                println(pizza.Print())
            }
            numOrd++
        }
        println("${allOrders.size + 1}: Назад")

        val ind = checkIndex(allOrders)
        if (ind == allOrders.size + 1) return

        allOrders.removeAt(ind - 1)
        println("Заказ успешно удалён!")
    }


    override fun edit() {
        while (true) {
            if (allOrders.isEmpty()) return println("Пусто!")
            println("Выберите номер заказа для изменения!")

            val numOrd = 1
            for (order in allOrders) {
                println("=== $numOrd заказ ===")
                val numberPi = 1
                for (pizza in order.pizzas) {
                    println("№$numberPi пицца в заказе")
                    println(pizza.Print())
                }
            }
            println("${allOrders.size + 1}: Назад")

            val ind = checkIndex(allOrders)
            if (ind == allOrders.size + 1) break
            val curOrder = allOrders[ind - 1]

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
                        println("${allOrders.size + 1}: Назад")
                        val pizzaInd = checkIndex(curOrder.pizzas)
                        if (pizzaInd == allOrders.size + 1) continue
                        val curPizza = curOrder.pizzas[pizzaInd - 1]

                        when (curPizza) {
                            is CombinedPizza -> editCombinedPizza(curPizza, curOrder)
                            else -> editRegularPizza(curPizza, curOrder)
                        }
                    }

                    "2" -> {
                        print("Новый комментарий: (нажмите enter если не хотите)")
                        val com = readln()
                        curOrder.comment = com
                    }

                    "3" -> {
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

                    "4" -> break
                }
            }
        }
    }

    private fun editRegularPizza(curPizza: Pizza, curOrder: Order) {
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
                        else if (borderInd != allBorders.size + 2) {
                            if (allBorders[borderInd - 1].checkPizza(curPizza.name)) curPizza.border = allBorders[borderInd - 1]
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
                        allIngredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                        var indAllIngr = checkIndex(allIngredients)
                        while (indAllIngr == curPizza.ingredients.size + 1) {
                            println("Введите корректное число!")
                            indAllIngr = checkIndex(allIngredients)
                        }
                        curPizza.ingredients[indIngr - 1] = allIngredients[indAllIngr - 1]
                    }
                }

                "6" -> break
            }
        }
    }

    private fun editCombinedPizza(curPizza: CombinedPizza, curOrder: Order) {
        while (true) {
            println(
                """
                    Что меняем? Выберите цифру
                        1. Название
                        2. Первая половина
                        3. Вторая половина
                        4. Основа
                        5. Размер
                        6. Бортик
                        7. Назад
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
                "2" -> editHalfPizza(curPizza.firstHalf, "первой половины")
                "3" -> editHalfPizza(curPizza.secondHalf, "второй половины")
                "4" -> {
                    println("Выберите новую основу:")
                    allBases.forEachIndexed { i, base -> println("${i + 1}: $base.") }
                    println("${allBases.size + 1}: Не менять")
                    val baseInd = checkIndex(allBases)
                    if (baseInd != allBases.size + 1) curPizza.base = allBases[baseInd - 1]
                }

                "5" -> {
                    println("Выберите новый размер:")
                    PizzaSize.values().forEachIndexed { i, pizzaSize -> println("${i + 1}: $pizzaSize") }
                    println("${PizzaSize.values().size + 1}: Не менять")
                    val sizeInd = checkIndex(PizzaSize.values().toList())
                    if (sizeInd != PizzaSize.values().size + 1) curPizza.size = PizzaSize.values()[sizeInd - 1]
                }

                "6" -> {
                    if (allBorders.isEmpty()) println("Бортиков нет!")
                    else {
                        println("Выберите бортик:")
                        allBorders.forEachIndexed { i, border -> println("${i + 1}: $border") }
                        println("${allBorders.size + 1}: Удалить нынешний")
                        println("${allBorders.size + 2}: Не изменять")
                        val borderInd = checkIndex(allBorders)
                        if (borderInd == allBorders.size + 1) curPizza.border = null
                        else if (borderInd != allBorders.size + 2) {
                            if (allBorders[borderInd - 1].checkPizza(curPizza.name)) curPizza.border = allBorders[borderInd - 1]
                            else println("Нельзя совмещать этот бортик с этой пиццей, бортик не изменён")

                        }
                    }
                }

                "7" -> break
            }
        }
    }

    private fun editHalfPizza(halfPizza: Pizza, halfName: String) {
        while (true) {
            println(
                """
                    Что меняем в пицце $halfName? Выберите цифру
                       1. Ингредиенты
                       2. Назад
                """.trimIndent()
            )
            val input = readln()
            when (input) {
                "1" -> {
                    while (true) {
                        println("Выберите номер ингредиента, который хотите заменить:")
                        println(halfPizza.name)
                        halfPizza.ingredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                        println("${halfPizza.ingredients.size + 1}: Не менять")

                        val indIngr = checkIndex(halfPizza.ingredients)
                        if (indIngr == halfPizza.ingredients.size + 1) break

                        println("Выберите номер ингредиента, на который хотите заменить прошлый!")
                        allIngredients.forEachIndexed { i, ingr -> println("${i + 1}: $ingr") }
                        var indAllIngr = checkIndex(allIngredients)
                        while (indAllIngr == halfPizza.ingredients.size + 1) {
                            println("Введите корректное число!")
                            indAllIngr = checkIndex(allIngredients)
                        }
                        halfPizza.ingredients[indIngr - 1] = allIngredients[indAllIngr - 1]
                    }
                }
                "2" -> break
            }
        }
    }

    override fun show() {
        if (allOrders.isEmpty()) {
            println("Нет заказов для отображения.")
            return
        }

        println("***** Список заказов *****")
        println("--------------------------------------------------------")
        allOrders.forEachIndexed { i, order ->
            println("Заказ №${i + 1}")
            println("ID: ${order.id}")
            var numberPi = 1
            for (pizza in order.pizzas) {
                println("№$numberPi пицца в заказе ")
                println(pizza.Print())
                numberPi++
            }
            println("Комментарий: ${order.comment}")
            println("Создан: ${order.timeCreate}")
            if (order.delayed != null) println("Отложен до: ${order.delayed}")
            println("--------------------------------------------------------")
        }
    }

    override fun sort() {
        println(
            """
            === Фильтруем ===
            1. По Дате
            2. По цене
            3. По отложенной задержке
            4. Назад
        """.trimIndent()
        )
        val input = readln()
        when (input) {
            "1" -> {
                println("Введите дату, по которой фильтруем (ДД.ММ.ГГГГ):")
                val input = readln()
                try {
                    val date = input.split(".").map { it.toInt() }
                    if (date.size != 3) {
                        println("Неправильный формат даты! Используйте ДД.ММ.ГГГГ")
                        return
                    }

                    val day = date[0]
                    val month = date[1]
                    val year = date[2]

                    // Проверяем валидность значений
                    if (month !in 1..12) {
                        println("Месяц должен быть от 1 до 12!")
                        return
                    }

                    if (day !in 1..31) {
                        println("День должен быть от 1 до 31!")
                        return
                    }

                    if (year < 2000 || year > 2100) { // можно задать разумный диапазон лет
                        println("Год указан некорректно!")
                        return
                    }
                    val listOrder = filterByDate(year, month, day)
                    showFilteredOrders(listOrder)
                } catch (e: NumberFormatException) {
                    println("Неправильно введена дата! (ДД.ММ.ГГГГ)")
                }
            }

            "2" -> {
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
                while (input.lowercase() != "да" || input.lowercase() != "нет" || input.isBlank()) {
                    println("Введите 'да' или 'нет'")
                    input = readln()
                }
                val listOrder: List<Order>
                if (input.lowercase() == "да") listOrder = filterDelayedOrders(1)
                else listOrder = filterDelayedOrders(2)

                showFilteredOrders(listOrder)
            }

            "4" -> return
        }
    }

    private fun filterByPriceRange(minPrice: Double = 0.0, maxPrice: Double = 10000000.0): List<Order> {
        return allOrders.filter { order ->
            val totalPrice = order.totalPrice()
            totalPrice in minPrice..maxPrice
        }
    }

    private fun filterByDate(year: Int, month: Int, day: Int): List<Order> {
        return allOrders.filter { order ->
            val orderDate = order.timeCreate.toLocalDate()
            orderDate.year == year && orderDate.monthValue == month && orderDate.dayOfMonth == day
        }
    }



    private fun filterDelayedOrders( priority: Int): List<Order> {
        return if(priority == 1) allOrders.filter { it.delayed != null }
        else allOrders.filter { it.delayed == null }
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
                println(pizza.Print())
                numberPi++
            }
            println("Комментарий: ${order.comment}")
            println("Создан: ${order.timeCreate}")
            if (order.delayed != null) println("Отложен до: ${order.delayed}")
            println("--------------------------------------------------------")

        } }
    }
}