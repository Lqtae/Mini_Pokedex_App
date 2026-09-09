package com.example.pokedex.data.model

import com.google.gson.annotations.SerializedName

/** Response of GET /pokemon?limit=&offset= */
data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItemDto>
)

data class PokemonListItemDto(
    val name: String,
    /** e.g. "https://pokeapi.co/api/v2/pokemon/1/" — id is derived from this. */
    val url: String
)

/** Response of GET /pokemon/{id} */
data class PokemonDetailDto(
    val id: Int,
    val name: String,
    /** decimetres */
    val height: Int,
    /** hectograms */
    val weight: Int,
    val types: List<TypeSlotDto>,
    val stats: List<StatSlotDto>,
    val abilities: List<AbilitySlotDto>,
    val sprites: SpritesDto
)

data class TypeSlotDto(
    val slot: Int,
    val type: NamedRefDto
)

data class AbilitySlotDto(
    val ability: NamedRefDto,
    @SerializedName("is_hidden") val isHidden: Boolean
)

/** Response of GET /pokemon-species/{id} — used for the flavor-text description. */
data class PokemonSpeciesDto(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto>
)

data class FlavorTextEntryDto(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedRefDto
)

data class StatSlotDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedRefDto
)

data class NamedRefDto(
    val name: String,
    val url: String
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String?,
    val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @SerializedName("front_default") val frontDefault: String?
)
