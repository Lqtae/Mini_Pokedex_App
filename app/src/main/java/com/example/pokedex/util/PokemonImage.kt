package com.example.pokedex.util

/** Helpers for deriving a Pokémon id and its artwork URL from the list endpoint. */
object PokemonImage {

    /**
     * Extracts the numeric id from a resource url like
     * "https://pokeapi.co/api/v2/pokemon/25/". Returns null if none found.
     */
    fun idFromUrl(url: String): Int? =
        url.trimEnd('/').substringAfterLast('/').toIntOrNull()

    /** Official-artwork sprite URL for a given id (list endpoint has no image). */
    fun officialArtworkUrl(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}

/** "bulbasaur" -> "Bulbasaur"; "mr-mime" -> "Mr Mime". */
fun String.toDisplayName(): String =
    split('-').joinToString(" ") { part ->
        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
