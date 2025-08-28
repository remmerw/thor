package io.github.remmerw.thor.cobra.html.renderer;

abstract class BaseRenderable implements Renderable {
    private int ordinal = 0;

    public int getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(final int ordinal) {
        this.ordinal = ordinal;
    }

    public int getZIndex() {
        return 0;
    }
}
