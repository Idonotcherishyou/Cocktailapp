package com.example.cocktailapp.data.repository

import com.example.cocktailapp.data.api.CocktailApiService
import com.example.cocktailapp.data.model.Cocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CocktailRepository @Inject constructor(
    private val apiService: CocktailApiService
) {
    // In-memory cache for search functionality
    private var allCocktailsCache: List<Cocktail> = emptyList()

    suspend fun getPopularCocktails(): List<Cocktail> = withContext(Dispatchers.IO) {
        val response = apiService.getPopularCocktails()
        response.drinks ?: emptyList()
    }

    suspend fun getAllCocktails(): List<Cocktail> = withContext(Dispatchers.IO) {
        if (allCocktailsCache.isNotEmpty()) {
            return@withContext allCocktailsCache
        }

        val allCocktails = mutableListOf<Cocktail>()
        // Fetch cocktails for each letter of the alphabet
        ('a'..'z').forEach { letter ->
            try {
                val response = apiService.searchCocktailsByFirstLetter(letter.toString())
                response.drinks?.let { cocktails ->
                    allCocktails.addAll(cocktails)
                }
            } catch (e: Exception) {
                // Skip letters with no results
            }
        }

        allCocktailsCache = allCocktails
        allCocktails
    }

    suspend fun getAlcoholicCocktails(): List<Cocktail> = withContext(Dispatchers.IO) {
        val response = apiService.getAlcoholicCocktails()
        response.drinks ?: emptyList()
    }

    suspend fun getNonAlcoholicCocktails(): List<Cocktail> = withContext(Dispatchers.IO) {
        val response = apiService.getNonAlcoholicCocktails()
        response.drinks ?: emptyList()
    }

    suspend fun getCocktailDetails(id: String): Cocktail? = withContext(Dispatchers.IO) {
        val response = apiService.getCocktailDetails(id)
        response.drinks?.firstOrNull()
    }

    // Local search functionality
    suspend fun searchCocktails(query: String): List<Cocktail> {
        if (allCocktailsCache.isEmpty()) {
            getAllCocktails() // Initialize cache if empty
        }

        return if (query.isBlank()) {
            allCocktailsCache
        } else {
            allCocktailsCache.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }
}