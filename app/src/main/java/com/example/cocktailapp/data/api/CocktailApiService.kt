package com.example.cocktailapp.data.api

import com.example.cocktailapp.data.model.CocktailResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApiService {

    companion object {
        const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }

    // Get popular cocktails (using "Popular drinks" filter)
    @GET("filter.php")
    suspend fun getPopularCocktails(@Query("c") category: String = "Cocktail"): CocktailResponse

    // Get all cocktails (paginate through alphabet)
    @GET("search.php")
    suspend fun searchCocktailsByFirstLetter(@Query("f") firstLetter: String): CocktailResponse

    // Filter by alcoholic
    @GET("filter.php")
    suspend fun getAlcoholicCocktails(@Query("a") alcoholic: String = "Alcoholic"): CocktailResponse

    // Filter by non-alcoholic
    @GET("filter.php")
    suspend fun getNonAlcoholicCocktails(@Query("a") nonAlcoholic: String = "Non_Alcoholic"): CocktailResponse

    // Get cocktail details by id
    @GET("lookup.php")
    suspend fun getCocktailDetails(@Query("i") id: String): CocktailResponse

    // Search cocktails by name
    @GET("search.php")
    suspend fun searchCocktailsByName(@Query("s") name: String): CocktailResponse
}