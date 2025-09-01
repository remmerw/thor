package io.github.remmerw.thor


private var thor: Thor? = null


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias Context = android.content.Context

internal class AndroidThor(
    private val context: Context,
) : Thor() {
}


actual fun thor(): Thor = thor!!


actual fun initializeThor(context: Context) {

    thor = AndroidThor(context)
}