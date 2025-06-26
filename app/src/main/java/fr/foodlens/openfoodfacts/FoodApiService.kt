package fr.foodlens.openfoodfacts

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApiService {

    @GET("api/v2/products/{code}.json")
    suspend fun getProductByCode(@Path("code") code: String): Response<ProductResponse>

}
