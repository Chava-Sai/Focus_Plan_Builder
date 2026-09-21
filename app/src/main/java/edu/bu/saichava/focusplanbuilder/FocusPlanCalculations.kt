package edu.bu.saichava.focusplanbuilder

const val MIN_MINUTES = 10
const val MAX_MINUTES = 180

fun durationCategory(minutes: Int): String = when {
    minutes < MIN_MINUTES -> "Invalid"
    minutes in 10..29 -> "Quick review"
    minutes in 30..60 -> "Focused session"
    else -> "Extended session"
}

fun recommendedBreak(minutes: Int): Int = when {
    minutes < MIN_MINUTES -> 0
    minutes in 10..29 -> 5
    minutes in 30..60 -> 10
    else -> 15
}
