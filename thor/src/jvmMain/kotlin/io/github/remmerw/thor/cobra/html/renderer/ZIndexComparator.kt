package io.github.remmerw.thor.cobra.html.renderer

internal class ZIndexComparator : Comparator<PositionedRenderable> {
    // Note: It is assumed that objects don't change their
    // z-indexes or ordinals after entering the sorted set.
    // They may do so after the sorted set is no longer valid.
    override fun compare(element1: PositionedRenderable, element2: PositionedRenderable): Int {
        val zIndex1 = element1.renderable.zIndex
        val zIndex2 = element2.renderable.zIndex
        val diff = zIndex1 - zIndex2
        if (diff != 0) {
            return diff
        }
        return element1.ordinal - element2.ordinal
    }
}
