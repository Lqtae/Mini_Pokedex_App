package com.example.pokedex.data.repository

import com.example.pokedex.data.api.PokeApiService
import com.example.pokedex.data.api.RetrofitClient
import com.example.pokedex.util.PokemonImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ---- UI-facing models (mapped from DTOs) ----

data class PokemonListItemUi(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class PokemonDetailUi(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>,
    val weightKg: Double,
    val heightM: Double,
    val abilities: List<String>,
    val description: String,
    val stats: List<StatUi>
)

data class StatUi(
    val label: String,
    val value: Int
)

/** Single source of truth for Pokémon data. Maps network DTOs to UI models. */
class PokemonRepository(
    private val api: PokeApiService = RetrofitClient.api
) {

    suspend fun getPokemonList(): List<PokemonListItemUi> = withContext(Dispatchers.IO) {
        api.getPokemonList().results.mapNotNull { item ->
            val id = PokemonImage.idFromUrl(item.url) ?: return@mapNotNull null
            PokemonListItemUi(
                id = id,
                name = item.name,
                imageUrl = PokemonImage.officialArtworkUrl(id)
            )
        }
    }

    suspend fun getPokemonDetail(id: Int): PokemonDetailUi = withContext(Dispatchers.IO) {
        val dto = api.getPokemonDetail(id)
        val artwork = dto.sprites.other?.officialArtwork?.frontDefault
            ?: dto.sprites.frontDefault
            ?: PokemonImage.officialArtworkUrl(dto.id)
        // Species (flavor text) is a separate endpoint — best-effort, don't fail detail on it.
        val description = try {
            api.getPokemonSpecies(id).flavorTextEntries
                .firstOrNull { it.language.name == "en" }
                ?.flavorText
                ?.replace(Regex("[\\n\\f\\r]"), " ")
                ?.trim()
                .orEmpty()
        } catch (_: Exception) {
            ""
        }
        PokemonDetailUi(
            id = dto.id,
            name = dto.name,
            imageUrl = artwork,
            types = dto.types.sortedBy { it.slot }.map { it.type.name },
            // API gives hectograms / decimetres -> kg / m
            weightKg = dto.weight / 10.0,
            heightM = dto.height / 10.0,
            abilities = dto.abilities.map { it.ability.name.split("-").joinToString(" ") { p -> p.replaceFirstChar { c -> c.uppercase() } } },
            description = description,
            stats = dto.stats.map { StatUi(statLabel(it.stat.name), it.baseStat) }
        )
    }

    private fun statLabel(raw: String): String = when (raw) {
        "hp" -> "HP"
        "attack" -> "ATK"
        "defense" -> "DEF"
        "special-attack" -> "SATK"
        "special-defense" -> "SDEF"
        "speed" -> "SPD"
        else -> raw.uppercase()
    }
}
