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

    val practiceCharacters = listOf(
        "\u0905", "\u0906", "\u0907", "\u0908", "\u0909", "\u090A", "\u090B", "\u090F", "\u0910", "\u0913", "\u0914",
        "\u0905\u0902", "\u0905\u0903",
        "\u0915", "\u0916", "\u0917", "\u0918", "\u0919",
        "\u091A", "\u091B", "\u091C", "\u091D", "\u091E",
        "\u091F", "\u0920", "\u0921", "\u0922", "\u0923",
        "\u0924", "\u0925", "\u0926", "\u0927", "\u0928",
        "\u092A", "\u092B", "\u092C", "\u092D", "\u092E",
        "\u092F", "\u0930", "\u0932", "\u0935",
        "\u0936", "\u0937", "\u0938", "\u0939",
        "\u0915\u094D\u0937", "\u0924\u094D\u0930", "\u091C\u094D\u091E"
    )

    val signImages: Map<String, Int> = mapOf(
        "\u0905" to R.drawable.vowelsign_a,
        "\u0906" to R.drawable.vowelsign_aa,
        "\u0907" to R.drawable.vowelsign_e,
        "\u0908" to R.drawable.vowelsign_ee,
        "\u0909" to R.drawable.vowelsign_u,
        "\u090A" to R.drawable.vowelsign_uu,
        "\u090B" to R.drawable.vowelsign_ri,
        "\u090F" to R.drawable.vowelsign_ya,
        "\u0910" to R.drawable.vowelsign_yai,
        "\u0913" to R.drawable.vowelsign_wo,
        "\u0914" to R.drawable.vowelsign_wau,
        "\u0905\u0902" to R.drawable.vowelsign_aam,
        "\u0905\u0903" to R.drawable.vowelsign_aha,
        "\u0915" to R.drawable.ka71,
        "\u0916" to R.drawable.kha3,
        "\u0917" to R.drawable.ga5,
        "\u0918" to R.drawable.gha6,
        "\u0919" to R.drawable.nga6,
        "\u091A" to R.drawable.cha6,
        "\u091B" to R.drawable.chha7,
        "\u091C" to R.drawable.ja7,
        "\u091D" to R.drawable.jha5,
        "\u091E" to R.drawable.yan6,
        "\u091F" to R.drawable.ta4,
        "\u0920" to R.drawable.tha7,
        "\u0921" to R.drawable.da7,
        "\u0922" to R.drawable.dha6,
        "\u0923" to R.drawable.ada4,
        "\u0924" to R.drawable.taa4,
        "\u0925" to R.drawable.tha6,
        "\u0926" to R.drawable.daa4,
        "\u0927" to R.drawable.dhha5,
        "\u0928" to R.drawable.na4,
        "\u092A" to R.drawable.pa4,
        "\u092B" to R.drawable.fa5,
        "\u092C" to R.drawable.ba6,
        "\u092D" to R.drawable.bha6,
        "\u092E" to R.drawable.ma3,
        "\u092F" to R.drawable.ya3,
        "\u0930" to R.drawable.ra2,
        "\u0932" to R.drawable.la6,
        "\u0935" to R.drawable.wa2,
        "\u0936" to R.drawable.sha2,
        "\u0937" to R.drawable.shaa4,
        "\u0938" to R.drawable.sa3,
        "\u0939" to R.drawable.ha5,
        "\u0915\u094D\u0937" to R.drawable.ksha47,
        "\u0924\u094D\u0930" to R.drawable.taa4,
        "\u091C\u094D\u091E" to R.drawable.gya5,
    )

    val characterLabels: Map<String, String> = mapOf(
        "\u0905" to "a", "\u0906" to "aa", "\u0907" to "i", "\u0908" to "ee",
        "\u0909" to "u", "\u090A" to "oo", "\u090B" to "ri", "\u090F" to "e",
        "\u0910" to "ai", "\u0913" to "o", "\u0914" to "au",
        "\u0905\u0902" to "am", "\u0905\u0903" to "aha",
        "\u0915" to "ka", "\u0916" to "kha", "\u0917" to "ga", "\u0918" to "gha", "\u0919" to "nga",
        "\u091A" to "cha", "\u091B" to "chha", "\u091C" to "ja", "\u091D" to "jha", "\u091E" to "nya",
        "\u091F" to "ta", "\u0920" to "tha", "\u0921" to "da", "\u0922" to "dha", "\u0923" to "na",
        "\u0924" to "ta", "\u0925" to "tha", "\u0926" to "da", "\u0927" to "dha", "\u0928" to "na",
        "\u092A" to "pa", "\u092B" to "pha", "\u092C" to "ba", "\u092D" to "bha", "\u092E" to "ma",
        "\u092F" to "ya", "\u0930" to "ra", "\u0932" to "la", "\u0935" to "wa",
        "\u0936" to "sha", "\u0937" to "sha", "\u0938" to "sa", "\u0939" to "ha",
        "\u0915\u094D\u0937" to "ksha", "\u0924\u094D\u0930" to "tra", "\u091C\u094D\u091E" to "gya"
    )
}
