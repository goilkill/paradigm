package org.example


val pizzaByPriceAsc = compareBy<Pizza> { it.finalPrice() }
val pizzaByPriceDesc = compareByDescending<Pizza> { it.finalPrice() }
val borderByPriceAsc = compareBy<Border> { it.borderPrice() }
val borderByPriceDesc = compareByDescending<Border> { it.borderPrice() }
val ingredientByPriceAsc = compareBy<Ingredient> { it.price }
val ingredientByPriceDesc = compareByDescending<Ingredient> { it.price }
val baseByPriceAsc = compareBy<Base> { it.price }
val baseByPriceDesc = compareByDescending<Base> { it.price }


val pizzaByNameAsc = compareBy<Pizza> { it.name }
val pizzaByNameDesc = compareByDescending<Pizza> { it.name }
val borderByNameAsc = compareBy<Border> { it.name }
val borderByNameDesc = compareByDescending<Border> { it.name }
val ingredientByNameAsc = compareBy<Ingredient> { it.name }
val ingredientByNameDesc = compareByDescending<Ingredient> { it.name }
val baseByNameAsc = compareBy<Base> { it.name }
val baseByNameDesc = compareByDescending<Base> { it.name }