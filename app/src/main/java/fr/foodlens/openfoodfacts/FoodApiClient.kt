package fr.foodlens.openfoodfacts

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object FoodApiClient {

    private val moshi: Moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    private val httpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", "FoodLens/1.0 (contact@rezoleo.fr)")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(requestWithUserAgent)
            }
            .addInterceptor(loggingInterceptor)
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

    private val foodApiService: FoodApiService by lazy { retrofit.create(FoodApiService::class.java) }

    suspend fun getProductByCode(code: String): Result<Product> {
        try {
            val response = foodApiService.getProductByCode(code)
            if (response.isSuccessful) {
                val body = response.body()!!
                return Result.success(body.product)
            } else {
                return Result.failure(Exception("Error"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

}
