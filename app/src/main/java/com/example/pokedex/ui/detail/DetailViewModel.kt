package com.example.pokedex.ui.detail

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonDetailUi
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.ui.common.UiState
import com.example.pokedex.ui.navigation.ARG_POKEMON_ID
import kotlinx.coroutines.launch

/** Total Pokémon browsable via swipe (matches the Home list limit). */
const val TOTAL_POKEMON = 1025

class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repository = PokemonRepository()

    /** id the screen opened on — used as the pager's initial page. */
    val initialId: Int = (savedStateHandle.get<Int>(ARG_POKEMON_ID) ?: 1).coerceIn(1, TOTAL_POKEMON)

    // Per-id state, cached so swiping back to a loaded page is instant.
    private val cache = mutableStateMapOf<Int, UiState<PokemonDetailUi>>()

    fun stateFor(id: Int): UiState<PokemonDetailUi> = cache[id] ?: UiState.Loading

    fun load(id: Int) {
        val current = cache[id]
        if (current is UiState.Success) return // already loaded
        cache[id] = UiState.Loading
        viewModelScope.launch {
            cache[id] = try {
                UiState.Success(repository.getPokemonDetail(id))
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load details")
            }
        }
    }
}
