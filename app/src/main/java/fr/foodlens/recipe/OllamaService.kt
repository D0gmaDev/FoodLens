package fr.foodlens.recipe

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OllamaService {
    @POST("/api/generate")
    suspend fun generate(@Body request: OllamaRequest): Response<OllamaResponse>
}

data class OllamaRequest(val model: String, val prompt: String, val stream: Boolean = false)

data class OllamaResponse(val response: String)

data class Recipe(
    val title: String,
    val ingredients: List<String>,
    val instructions: List<String>
)