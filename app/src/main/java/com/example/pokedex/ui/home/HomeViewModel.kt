package com.example.pokedex.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonListItemUi
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = PokemonRepository()

    private val _state = MutableStateFlow<UiState<List<PokemonListItemUi>>>(UiState.Loading)
    val state: StateFlow<UiState<List<PokemonListItemUi>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            _state.value = try {
                UiState.Success(repository.getPokemonList())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load Pokémon")
            }
        }
    }
}
