package fr.foodlens.openfoodfacts

data class ProductResponse(
    val code: String,
    val status: Int,
    val product: Product,

    val apiVersion: Int = 1
)
