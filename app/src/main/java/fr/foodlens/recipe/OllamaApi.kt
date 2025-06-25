package fr.foodlens.recipe

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object OllamaApi {

    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    private val httpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private lateinit var retrofit: Retrofit

    fun init(apiUrl: String) {
        retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val service: OllamaService by lazy { retrofit.create(OllamaService::class.java) }

    suspend fun generateRecipe(ingredients: List<String>): Result<Recipe> {

        val products = ingredients.joinToString(separator = ", ")

        val prompt = """Fais une recette de cuisine très consise avec les ingrédients suivants :
                
                $products
                -------
                
                Répond uniquement sous un format Json valide, sans explications ni texte supplémentaire, rien d'autre !
                
                avec trois champs exactement :
                - "title" : le titre de la recette
                - "ingredients" : List<String> : un tableau des ingrédients (simple chaînes de caractères)
                - "instructions" : List<String> : un tableau des instructions de préparation (simples chaînes de caractères)
                """
        try {
            val response = service.generate(
                OllamaRequest(model = "gemma3", prompt = prompt)
            )

            val body = response.body()!!

            val recipe =
                moshi.adapter<Recipe>(Recipe::class.java).fromJson(sanitizeJson(body.response))!!

            return Result.success(recipe)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun sanitizeJson(rawResponse: String): String {

        val cleanedJson = rawResponse
            .replace("```json", "")
            .replace("```", "")
            .trim()

        val start = cleanedJson.indexOf('{')
        val end = cleanedJson.lastIndexOf('}')

        return if (start != -1 && end != -1 && end > start) {
            cleanedJson.substring(start, end + 1)
        } else {
            cleanedJson
        }
    }
}