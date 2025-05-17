package org.example

class CombinedPizza(
    name: String,
    var firstHalf: Pizza,
    var secondHalf: Pizza,
    base: Base,
    size: PizzaSize,
    border: Border?,
    ingredients: MutableList<Ingredient> = mutableListOf()
) : Pizza(name, base, size, border, ingredients) {

    override fun finalPrice(): Double {
        val basePrice = (firstHalf.finalPrice() + secondHalf.finalPrice()) / 2

        val sizePrice = when(size) {
            PizzaSize.SMALL -> 50.0
            PizzaSize.MEDIUM -> 100.0
            PizzaSize.LARGE -> 150.0
        }

        val borderPrice = border?.borderPrice() ?: 0.0

        return basePrice + sizePrice + borderPrice
    }

    override fun Print() {
        println("""
            Название: $name 
            Первая половина: ${firstHalf.name}
            Ингредиенты первой половины: ${firstHalf.ingredients.joinToString()}
            Вторая половина: ${secondHalf.name}
            Ингредиенты второй половины: ${secondHalf.ingredients.joinToString()}
            Основа: $base 
            Бортик: $border
            Размер: $size
            Сумма пиццы: ${finalPrice()}
        """.trimIndent())
    }
}