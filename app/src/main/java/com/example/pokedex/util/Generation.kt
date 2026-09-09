package com.example.pokedex.util

/** National-Dex generation ranges (Gen 1–9, up to #1025). */
object Generations {

    data class Gen(val number: Int, val range: IntRange)

    val all = listOf(
        Gen(1, 1..151),
        Gen(2, 152..251),
        Gen(3, 252..386),
        Gen(4, 387..493),
        Gen(5, 494..649),
        Gen(6, 650..721),
        Gen(7, 722..809),
        Gen(8, 810..905),
        Gen(9, 906..1025),
    )

    /** Generation number for a Pokédex id, or 0 if out of range. */
    fun of(id: Int): Int = all.firstOrNull { id in it.range }?.number ?: 0
}
