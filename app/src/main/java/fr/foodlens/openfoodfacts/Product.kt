package fr.foodlens.openfoodfacts

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(

    @Json(name = "id") val id: String,

    @Json(name = "product_name") val name: String,

    @Json(name = "generic_name") val genericName: String?,

    @Json(name = "quantity") val quantity: String,

    @Json(name = "brands") val brands: String,

    @Json(name = "image_url") val imageUrl: String,

    @Json(name = "categories") val categories: String,

    @Json(name = "nutriscore_grade") val nutriScoreGrade: String,

    @Json(name = "_keywords") val keywords: List<String>,
)
