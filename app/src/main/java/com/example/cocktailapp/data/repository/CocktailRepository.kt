package com.example.cocktailapp.data.repository

class CocktailRepository(private val api: CocktailApiService) {
    suspend fun getAllCocktails() = api.searchCocktails("")
    suspend fun getCocktailById(id: String) = api.getCocktailDetails(id)
    suspend fun filterByAlcohol(type: String) = api.filterByAlcohol(type)
}