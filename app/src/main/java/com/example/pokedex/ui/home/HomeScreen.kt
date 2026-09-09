package com.example.pokedex.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokedex.R
import com.example.pokedex.data.repository.PokemonListItemUi
import com.example.pokedex.ui.common.ErrorState
import com.example.pokedex.ui.common.LoadingState
import com.example.pokedex.ui.common.UiState
import com.example.pokedex.ui.theme.PokedexTheme
import com.example.pokedex.util.Generations
import com.example.pokedex.util.toDisplayName

@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var sortByName by remember { mutableStateOf(false) }
    var selectedGen by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(
                query = query,
                onQueryChange = { query = it },
                sortByName = sortByName,
                onToggleSort = { sortByName = !sortByName }
            )

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when (val s = state) {
                    is UiState.Loading -> LoadingState()
                    is UiState.Error -> ErrorState(message = s.message, onRetry = viewModel::load)
                    is UiState.Success -> {
                        val q = query.trim()
                        // No search → show only the selected generation; searching → across all gens.
                        val base = if (q.isBlank()) {
                            s.data.filter { Generations.of(it.id) == selectedGen }
                        } else {
                            s.data.filter { it.name.contains(q, ignoreCase = true) }
                        }
                        val items = if (sortByName) base.sortedBy { it.name } else base.sortedBy { it.id }
                        Row(modifier = Modifier.fillMaxSize()) {
                            PokemonGrid(
                                items = items,
                                onPokemonClick = onPokemonClick,
                                modifier = Modifier.weight(1f)
                            )
                            GenSideBar(selected = selectedGen, onSelect = { selectedGen = it })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    sortByName: Boolean,
    onToggleSort: () -> Unit
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_pokeball_white),
                contentDescription = null,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "Pokédex",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = { Text("Search", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f)
            )
            // Sort button — circle showing the CURRENT order (123 = by number, A–Z = by name).
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(52.dp)
                    .background(Color.White, CircleShape)
                    .clickable(onClick = onToggleSort),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (sortByName) "A–Z" else "123",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PokemonGrid(
    items: List<PokemonListItemUi>,
    onPokemonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { pokemon ->
            PokemonCard(pokemon = pokemon, onClick = { onPokemonClick(pokemon.id) })
        }
    }
}

/** Vertical generation selector on the right edge — tap a number to switch generation. */
@Composable
private fun GenSideBar(selected: Int, onSelect: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(44.dp)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Generations.all.forEach { gen ->
            val active = gen.number == selected
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .clickable { onSelect(gen.number) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gen.number.toString(),
                    color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun PokemonCard(
    pokemon: PokemonListItemUi,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(6.dp)) {
            // light-gray block behind the lower part of the card
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.52f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            )
            Text(
                text = "#%03d".format(pokemon.id),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 2.dp, top = 2.dp)
            )
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.82f)
                    .aspectRatio(1f)
                    .padding(top = 8.dp)
            )
            Text(
                text = pokemon.name.toDisplayName(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonCardPreview() {
    PokedexTheme {
        PokemonCard(
            pokemon = PokemonListItemUi(id = 1, name = "bulbasaur", imageUrl = ""),
            onClick = {},
        )
    }
}
