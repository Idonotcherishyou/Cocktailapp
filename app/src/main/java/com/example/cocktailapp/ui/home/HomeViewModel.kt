package com.example.cocktailapp.ui.home

package com.example.cocktailapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktailapp.data.model.Cocktail
import com.example.cocktailapp.data.repository.CocktailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {

    // UI States
    sealed class UiState {
        object Loading : UiState()
        data class Success(val cocktails: List<Cocktail>) : UiState()
        data class Error(val message: String) : UiState()
    }

    // Category types
    enum class CocktailCategory {
        POPULAR, ALL, ALCOHOLIC, NON_ALCOHOLIC
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _currentCategory = MutableStateFlow(CocktailCategory.POPULAR)
    val currentCategory: StateFlow<CocktailCategory> = _currentCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadCocktails(CocktailCategory.POPULAR)
    }

    fun loadCocktails(category: CocktailCategory) {
        _currentCategory.value = category
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val cocktails = when (category) {
                    CocktailCategory.POPULAR -> repository.getPopularCocktails()
                    CocktailCategory.ALL -> repository.getAllCocktails()
                    CocktailCategory.ALCOHOLIC -> repository.getAlcoholicCocktails()
                    CocktailCategory.NON_ALCOHOLIC -> repository.getNonAlcoholicCocktails()
                }
                _uiState.value = UiState.Success(cocktails)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load cocktails: ${e.message}")
            }
        }
    }

    fun searchCocktails(query: String) {
        _searchQuery.value = query

        if (query.isBlank() && _currentCategory.value != CocktailCategory.ALL) {
            // If search query is cleared, return to current category
            loadCocktails(_currentCategory.value)
            return
        }

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val results = repository.searchCocktails(query)
                _uiState.value = UiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Search failed: ${e.message}")
            }
        }
    }
}