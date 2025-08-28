package io.github.remmerw.thor.cobra.html.renderer;

import io.github.remmerw.thor.cobra.html.domimpl.ModelNode;
import io.github.remmerw.thor.cobra.ua.UserAgentContext;

abstract class BaseBlockyRenderable extends BaseElementRenderable {

    public BaseBlockyRenderable(RenderableContainer container, ModelNode modelNode, UserAgentContext ucontext) {
        super(container, modelNode, ucontext);
    }

    public abstract void layout(int availWidth, int availHeight, boolean b, boolean c, FloatingBoundsSource source, boolean sizeOnly);

}
