package io.github.remmerw.thor.cobra.html.domimpl;

import org.w3c.dom.html.HTMLHRElement;

public class HTMLHRElementImpl extends HTMLAbstractUIElement implements HTMLHRElement {
    public HTMLHRElementImpl(final String name) {
        super(name);
    }

    public String getAlign() {
        return this.getAttribute("align");
    }

    public void setAlign(final String align) {
        this.setAttribute("align", align);
    }

    public boolean getNoShade() {
        return "noshade".equalsIgnoreCase(this.getAttribute("noshade"));
    }

    public void setNoShade(final boolean noShade) {
        this.setAttribute("noshade", noShade ? "noshade" : null);
    }

    public String getSize() {
        return this.getAttribute("size");
    }

    public void setSize(final String size) {
        this.setAttribute("size", size);
    }

    public String getWidth() {
        return this.getAttribute("width");
    }

    public void setWidth(final String width) {
        this.setAttribute("width", width);
    }
}
