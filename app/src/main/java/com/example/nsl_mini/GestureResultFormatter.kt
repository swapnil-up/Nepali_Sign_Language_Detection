package com.example.nsl_mini

object GestureResultFormatter {
    const val GESTURE_MODEL_FILE = "gesture_recognizer1.task"

    fun firstGesture(result: String): String? {
        return result.split("\n")
            .firstOrNull()
            ?.trim()
            ?.takeIf { it.isNotEmpty() && it.lowercase() != "none" }
    }

    val compoundCharacters = listOf(
        "\u0905\u0902",
        "\u0915\u094D\u0937",
        "\u0924\u094D\u0930",
        "\u091C\u094D\u091E",
        "\u0905\u0903",
    )
}
