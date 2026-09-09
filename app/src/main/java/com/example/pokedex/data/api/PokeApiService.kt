package com.example.pokedex.data.api

import com.example.pokedex.data.model.PokemonDetailDto
import com.example.pokedex.data.model.PokemonListResponse
import com.example.pokedex.data.model.PokemonSpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 1025,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetailDto

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesDto
}
