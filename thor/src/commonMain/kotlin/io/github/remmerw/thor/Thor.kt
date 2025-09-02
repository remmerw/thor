package io.github.remmerw.thor


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect abstract class Context


abstract class Thor

expect fun thor(): Thor
expect fun initializeThor(context: Context)
