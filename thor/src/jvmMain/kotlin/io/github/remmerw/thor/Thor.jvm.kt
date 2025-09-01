package io.github.remmerw.thor

private var thor: Thor? = null


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual abstract class Context
object JvmContext : Context()

internal class JvmThor(
) : Thor() {



}


actual fun thor(): Thor = thor!!


actual fun initializeThor(context: Context) {
    thor = JvmThor()
}

