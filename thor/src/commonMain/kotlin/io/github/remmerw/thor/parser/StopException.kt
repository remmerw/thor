package io.github.remmerw.thor.parser

import org.w3c.dom.Element

internal class StopException(val element: Element) : Exception()
