package com.example.pokedex.ui.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pokedex.ui.theme.PokedexTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokedex.R
import com.example.pokedex.data.repository.PokemonDetailUi
import com.example.pokedex.data.repository.StatUi
import com.example.pokedex.ui.common.ErrorState
import com.example.pokedex.ui.common.LoadingState
import com.example.pokedex.ui.common.UiState
import com.example.pokedex.ui.common.typeColor
import com.example.pokedex.util.toDisplayName

private val LabelGray = Color(0xFF9E9E9E)

// Fixed height for an About value slot (fits up to two lines) so it never reflows.
private val ABOUT_VALUE_HEIGHT = 44.dp

/**
 * Cross-fades between values: the old one fades OUT while the new one fades IN whenever
 * [target] changes. Used to animate only the data (name, id, image, weight/height/moves
 * values, stat values) — the static labels below never animate.
 */
@Composable
private fun <T> FadeSwap(
    target: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    Crossfade(targetState = target, animationSpec = tween(350), modifier = modifier, label = "fade") {
        content(it)
    }
}

/**
 * Type chips. On a data switch the old set fades + scales out toward center while the new
 * set fades + scales in and the width animates — so 2→1 collapses and 1→2 expands smoothly.
 * On first open ([animate] = false) the chips render settled, with no transition.
 */
@Composable
private fun TypeChips(types: List<String>, animate: Boolean) {
    if (!animate) {
        TypeChipsRow(types)
        return
    }
    AnimatedContent(
        targetState = types,
        transitionSpec = {
            (fadeIn(tween(300)) + scaleIn(initialScale = 0.85f)) togetherWith
                (fadeOut(tween(200)) + scaleOut(targetScale = 0.85f)) using
                SizeTransform(clip = false)
        },
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(),
        label = "type-chips"
    ) { list ->
        TypeChipsRow(list)
    }
}

@Composable
private fun TypeChipsRow(types: List<String>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        types.forEachIndexed { i, type ->
            TypeChip(type)
            if (i < types.lastIndex) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    var currentId by remember { mutableIntStateOf(viewModel.initialId) }
    // Keep the last loaded Pokémon on screen while the next one loads (no red flash).
    var lastDetail by remember { mutableStateOf<PokemonDetailUi?>(null) }
    // Only animate once the user has actually switched (no entrance anim on first open).
    var userSwitched by remember { mutableStateOf(false) }

    LaunchedEffect(currentId) {
        viewModel.load(currentId)
        if (currentId > 1) viewModel.load(currentId - 1)
        if (currentId < TOTAL_POKEMON) viewModel.load(currentId + 1)
    }

    val state = viewModel.stateFor(currentId)
    LaunchedEffect(state) {
        if (state is UiState.Success) lastDetail = state.data
    }
    val detail = (state as? UiState.Success)?.data ?: lastDetail

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            detail != null -> DetailContent(
                detail = detail,
                canPrev = currentId > 1,
                canNext = currentId < TOTAL_POKEMON,
                onPrev = { if (currentId > 1) { currentId--; userSwitched = true } },
                onNext = { if (currentId < TOTAL_POKEMON) { currentId++; userSwitched = true } },
                onBack = onBack,
                animate = userSwitched
            )
            state is UiState.Error -> Box(Modifier.fillMaxSize()) {
                BackButton(onBack, Modifier.statusBarsPadding().padding(8.dp))
                ErrorState(message = state.message, onRetry = { viewModel.load(currentId) })
            }
            else -> LoadingHeader()
        }
    }
}

@Composable
private fun LoadingHeader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // neutral — no red flash
    ) {
        LoadingState()
    }
}

@Composable
private fun DetailContent(
    detail: PokemonDetailUi,
    canPrev: Boolean,
    canNext: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    animate: Boolean
) {
    val targetHeaderColor = detail.types.firstOrNull()?.let { typeColor(it) }
        ?: MaterialTheme.colorScheme.primary
    val headerColor by animateColorAsState(targetHeaderColor, tween(400), label = "header-color")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(headerColor)
    ) {
        // Colored header — raised above the white card so the artwork can overlap it.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerColor)
                .zIndex(1f)
        ) {
            Image(
                painter = painterResource(R.drawable.pokeball),
                contentDescription = null,
                alpha = 1.0f,
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-15).dp, y = 90.dp)
            )
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackButton(onBack)
                    FadeSwap(target = detail.name) { name ->
                        Text(
                            text = name.toDisplayName(),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    FadeSwap(target = detail.id, modifier = Modifier.padding(end = 16.dp)) { id ->
                        Text(
                            text = "#%03d".format(id),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Artwork; spills below onto the white card.
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    if (canPrev) {
                        ArrowButton(
                            icon = R.drawable.ic_chevron_left,
                            description = "Previous",
                            onClick = onPrev,
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                        )
                    }
                    if (canNext) {
                        ArrowButton(
                            icon = R.drawable.ic_chevron_right,
                            description = "Next",
                            onClick = onNext,
                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
                        )
                    }
                    FadeSwap(
                        target = detail.imageUrl,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 55.dp)
                    ) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = detail.name,
                            modifier = Modifier.size(350.dp) // ขนาดรูป
                        )
                    }
                }
            }
        }

        // White card — inset horizontally so the type color shows as a side border.
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp) // ปรับเลขนี้ = เห็นขอบสีมาก/น้อย
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 66.dp, bottom = 20.dp)
            ) {
                TypeChips(types = detail.types, animate = animate)

                SectionTitle("About", headerColor)
                AboutRow(detail = detail)

                if (detail.description.isNotBlank()) {
                    FadeSwap(
                        target = detail.description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .animateContentSize()
                    ) { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                SectionTitle("Base Stats", headerColor)
                detail.stats.forEach { stat -> StatRow(stat = stat, accent = headerColor) }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 12.dp)
    )
}

@Composable
private fun AboutRow(detail: PokemonDetailUi) {
    Row(
        modifier = Modifier.fillMaxWidth().height(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AboutColumn(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_weight,
            value = "%.1f kg".format(detail.weightKg),
            label = "Weight"
        )
        VDivider()
        AboutColumn(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_height,
            value = "%.1f m".format(detail.heightM),
            label = "Height"
        )
        VDivider()
        // Moves — only the value cross-fades; the "Moves" label stays put.
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.height(ABOUT_VALUE_HEIGHT), contentAlignment = Alignment.Center) {
                FadeSwap(target = detail.abilities.take(2).joinToString("\n").ifBlank { "—" }) { moves ->
                    Text(
                        text = moves,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Moves", style = MaterialTheme.typography.labelMedium, color = LabelGray)
        }
    }
}

@Composable
private fun AboutColumn(icon: Int, value: String, label: String, modifier: Modifier = Modifier) {
    // fillMaxWidth + CenterHorizontally keeps the content centered inside its own cell.
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fixed-height value slot so length changes never move the label below.
        Box(modifier = Modifier.height(ABOUT_VALUE_HEIGHT), contentAlignment = Alignment.Center) {
            FadeSwap(target = value) { v ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = v,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = LabelGray)
    }
}

@Composable
private fun VDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(44.dp)
            .background(Color(0xFFE0E0E0))
    )
}

@Composable
private fun ArrowButton(
    icon: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier.zIndex(2f)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun BackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onBack, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Composable
private fun TypeChip(type: String) {
    Surface(
        color = typeColor(type),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = type.toDisplayName(),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun StatRow(stat: StatUi, accent: Color) {
    val pct by animateFloatAsState(
        targetValue = (stat.value / 255f).coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "stat-fill"
    )

    Row(
        modifier = Modifier.fillMaxWidth().height(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accent,
            textAlign = TextAlign.End,
            modifier = Modifier.width(48.dp)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .width(1.dp)
                .fillMaxHeight()
                .background(Color(0xFFE0E0E0))
        )
        FadeSwap(target = stat.value, modifier = Modifier.width(40.dp)) { v ->
            Text(
                text = "%03d".format(v),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        LinearProgressIndicator(
            progress = { pct },
            color = accent,
            trackColor = accent.copy(alpha = 0.2f),
            gapSize = 0.dp,
            drawStopIndicator = {},
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailScreenPreview() {
    val sample = PokemonDetailUi(
        id = 1,
        name = "bulbasaur",
        imageUrl = "",
        types = listOf("grass", "poison"),
        weightKg = 6.9,
        heightM = 0.7,
        abilities = listOf("Overgrow", "Chlorophyll"),
        description = "A strange seed was planted on its back at birth. The plant sprouts and grows with this Pokémon.",
        stats = listOf(
            StatUi("HP", 45), StatUi("ATK", 49), StatUi("DEF", 49),
            StatUi("SATK", 65), StatUi("SDEF", 65), StatUi("SPD", 45),
        ),
    )
    PokedexTheme {
        DetailContent(
            detail = sample,
            canPrev = true,
            canNext = true,
            onPrev = {},
            onNext = {},
            onBack = {},
            animate = false,
        )
    }
}
