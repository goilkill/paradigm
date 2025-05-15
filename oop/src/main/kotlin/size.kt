package org.example

enum class PizzaSize(val check: String) {
    SMALL("Маленькая"),
    MEDIUM("Средняя"),
    LARGE("Большая");

    override fun toString() = check
}