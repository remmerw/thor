package io.github.remmerw.thor.cobra.html.renderer;

import org.eclipse.jdt.annotation.NonNull;

import java.awt.Insets;

import io.github.remmerw.thor.cobra.html.style.HtmlInsets;

final class BorderOverrider {
    boolean leftOverridden = false;
    boolean rightOverridden = false;
    boolean bottomOverridden = false;
    boolean topOverridden = false;

    @NonNull
    Insets get(final @NonNull Insets borderInsets) {
        if (leftOverridden || rightOverridden || topOverridden || bottomOverridden) {
            final int topDash = topOverridden ? 0 : borderInsets.top;
            final int leftDash = leftOverridden ? 0 : borderInsets.left;
            final int bottomDash = bottomOverridden ? 0 : borderInsets.bottom;
            final int rightDash = rightOverridden ? 0 : borderInsets.right;
            return new Insets(topDash, leftDash, bottomDash, rightDash);
        }
        return borderInsets;
    }

    public void copyFrom(final BorderOverrider other) {
        this.topOverridden = other.topOverridden;
        this.leftOverridden = other.leftOverridden;
        this.rightOverridden = other.rightOverridden;
        this.bottomOverridden = other.bottomOverridden;
    }

    public HtmlInsets get(final HtmlInsets borderInsets) {
        if ((borderInsets != null) && (leftOverridden || rightOverridden || topOverridden || bottomOverridden)) {
            final int topDash = topOverridden ? 0 : borderInsets.top;
            final int leftDash = leftOverridden ? 0 : borderInsets.left;
            final int bottomDash = bottomOverridden ? 0 : borderInsets.bottom;
            final int rightDash = rightOverridden ? 0 : borderInsets.right;
            return new HtmlInsets(topDash, leftDash, bottomDash, rightDash, HtmlInsets.TYPE_PIXELS);
        }
        return borderInsets;
    }
}