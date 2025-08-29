package io.github.remmerw.thor.cobra.html.domimpl

class HTMLNonStandardElement : HTMLElementImpl {
    constructor(name: String, noStyleSheet: Boolean) : super(name, noStyleSheet)

    constructor(name: String) : super(name)
}
