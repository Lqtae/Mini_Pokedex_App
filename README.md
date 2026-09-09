# Mini Pokédex

Android app that lists Pokémon and shows details for each, using data from the
[PokeAPI](https://pokeapi.co/). Built for the Persec Android Developer internship
practical test.

## Features
- **Home screen** — grid of Pokémon (name + official artwork), loaded from
  `GET /pokemon`.
- **Detail screen** — tap a Pokémon to see its large artwork, types, weight, height,
  and base stats (`GET /pokemon/{id}`).
- Loading and error states with retry.

## Tech stack
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (ViewModel + Repository, `StateFlow` UI state), no DI framework
- **Networking:** Retrofit + Gson
- **Images:** Coil
- **Navigation:** Navigation Compose

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

## Build & run
1. Open in Android Studio, or from the command line:
   ```
   ./gradlew assembleDebug
   ```
2. Run on an emulator or device (minSdk 26 / Android 8.0+).

## Tests
```
./gradlew testDebugUnitTest
```

## API notes
The list endpoint returns only name + resource URL (no image). The Pokémon id is parsed
from that URL and the official artwork is loaded from the PokeAPI sprites repository.
