package com.example.cocktailapp.ui.details

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
class DetailViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {

    // UI States
    sealed class UiState {
        object Loading : UiState()
        data class Success(val cocktail: Cocktail) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun loadCocktailDetails(cocktailId: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val cocktail = repository.getCocktailDetails(cocktailId)
                if (cocktail != null) {
                    _uiState.value = UiState.Success(cocktail)
                } else {
                    _uiState.value = UiState.Error("Cocktail not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load cocktail details: ${e.message}")
            }
        }
    }
}