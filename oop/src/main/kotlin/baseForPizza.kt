package org.example

data class Base(var name: String, var price: Double){
    override fun toString() = "$name = $price"
}


class ManageBasePizza : baseFunction {
    private var allBases = mutableListOf<Base>()

    fun menu(){
        var contin = true
        while (contin){
            println("""
                  === Основы ===
                1. Добавить основу
                2. Удалить основу
                3. Изменить основу
                4. Показать все основы
                5. Назад
            """.trimIndent())
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

    fun checkClassic(name: String, price: Double) : Int{
        if(name.lowercase() != "классическое"){
            val check = allBases.find { it.name.lowercase() == "классическое" || it.name.lowercase() == "классическая"}
            if (check == null) {
                println("Добавьте сначала классическую!")
                return 1
            }
            if(check.price * 1.2 < price){
                println("Цена превосходит больше чем на 20%!")
                return 1
            }
        }
        return 0
    }

    override fun add() {
        println("Введите название!")
        val name = checkName()
        val price = checkPrice()
        val flag = checkClassic(name, price)
        if (flag == 1) return

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
        allBases.removeAt(ind - 1)
        println("Успешно удалён!")
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
        val flag : Int = checkClassic(name, price)
        if (flag == 1) return

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

    fun get(): List<Base> = allBases

}