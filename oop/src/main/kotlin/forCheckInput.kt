package org.example

fun checkIndex(list: List<Any>): Int{
    var ind = readln().toIntOrNull()
    while (ind == null || ind !in 1..list.size + 1) {
        println("Введите корректное число!")
        ind = readln().toIntOrNull()
    }
    return ind
}

fun checkName() : String{
    var name = readLine()
    while (name.isNullOrBlank()) {
        println("Введите корректное имя!")
        name = readLine()
    }
    return name
}

fun checkPrice() : Double{
    println("Введите цену")
    var price = readln().toDoubleOrNull()
    while (price == null || price < 0.0) {
        println("Введите корректную цену")
        price = readln().toDoubleOrNull()
    }
    return price
}