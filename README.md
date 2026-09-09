# Mini Pokédex

Android app that lists Pokémon and shows details for each, using data from the
[PokeAPI](https://pokeapi.co/). Built for the Persec Android Developer internship
practical test.

## Features
- **Home screen** — grid of Pokémon (name + official artwork), loaded from `GET /pokemon`.
- **Detail screen** — tap a Pokémon to see its large artwork, types, weight, height, and base stats (`GET /pokemon/{id}`).
- Loading and error states with retry.

## Tech stack
- **Language:** Kotlin
- **UI:** Jetpack Compose

## Project structure
```
com.example.pokedex
├── data
│   ├── model        # Retrofit/Gson DTOs
│   ├── network      # PokeApiService + RetrofitClient
│   └── repository   # PokemonRepository (+ UI models)
├── ui
│   ├── home         # HomeViewModel, HomeScreen
│   ├── detail       # DetailViewModel, DetailScreen
│   ├── navigation   # PokedexNavHost, routes
│   ├── common       # UiState, shared state views, type colors
│   └── theme        # Compose theme
├── util             # id extraction + artwork URL helpers
└── MainActivity
```
