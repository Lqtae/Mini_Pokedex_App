package com.example.pokedex

import com.example.pokedex.util.PokemonImage
import com.example.pokedex.util.toDisplayName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PokemonImageTest {

    @Test
    fun idFromUrl_parsesTrailingId() {
        assertEquals(1, PokemonImage.idFromUrl("https://pokeapi.co/api/v2/pokemon/1/"))
        assertEquals(151, PokemonImage.idFromUrl("https://pokeapi.co/api/v2/pokemon/151"))
    }

    @Test
    fun idFromUrl_returnsNullForGarbage() {
        assertNull(PokemonImage.idFromUrl("https://pokeapi.co/api/v2/pokemon/abc/"))
    }

    @Test
    fun officialArtworkUrl_buildsExpectedPath() {
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
            PokemonImage.officialArtworkUrl(25)
        )
    }

    @Test
    fun toDisplayName_capitalizesAndSplits() {
        assertEquals("Bulbasaur", "bulbasaur".toDisplayName())
        assertEquals("Mr Mime", "mr-mime".toDisplayName())
    }
}
