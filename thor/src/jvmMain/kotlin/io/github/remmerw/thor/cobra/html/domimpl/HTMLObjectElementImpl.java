package io.github.remmerw.thor.cobra.html.domimpl;

import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLObjectElement;

public class HTMLObjectElementImpl extends HTMLAbstractUIElement implements HTMLObjectElement {
    public HTMLObjectElementImpl(final String name) {
        super(name);
    }

    public String getAlign() {
        return this.getAttribute("align");
    }

    public void setAlign(final String align) {
        this.setAttribute("align", align);
    }

    public String getAlt() {
        return this.getAttribute("alt");
    }

    public void setAlt(final String alt) {
        this.setAttribute("alt", alt);
    }

    public String getArchive() {
        return this.getAttribute("archive");
    }

    public void setArchive(final String archive) {
        this.setAttribute("archive", archive);
    }

    public String getCode() {
        return this.getAttribute("code");
    }

    public void setCode(final String code) {
        this.setAttribute("code", code);
    }

    public String getCodeBase() {
        return this.getAttribute("codebase");
    }

    public void setCodeBase(final String codeBase) {
        this.setAttribute("codebase", codeBase);
    }

    public String getHeight() {
        return this.getAttribute("height");
    }

    public void setHeight(final String height) {
        this.setAttribute("height", height);
    }

    public String getName() {
        return this.getAttribute("name");
    }

    public void setName(final String name) {
        this.setAttribute("name", name);
    }

    public String getObject() {
        return this.getAttribute("object");
    }

    public void setObject(final String object) {
        this.setAttribute("object", object);
    }

    public String getWidth() {
        return this.getAttribute("width");
    }

    public void setWidth(final String width) {
        this.setAttribute("width", width);
    }

    public String getBorder() {
        return this.getAttribute("border");
    }

    public void setBorder(final String border) {
        this.setAttribute("border", border);
    }

    public String getCodeType() {
        return this.getAttribute("codetype");
    }

    public void setCodeType(final String codeType) {
        this.setAttribute("codetype", codeType);
    }

    public Document getContentDocument() {
        return this.getOwnerDocument();
    }

    public String getData() {
        return this.getAttribute("data");
    }

  /* public int getHspace() {
    try {
      return Integer.parseInt(this.getAttribute("hspace"));
    } catch (final Exception err) {
      return 0;
    }
  }*/

    public void setData(final String data) {
        this.setAttribute("data", data);
    }

    public boolean getDeclare() {
        return "declare".equalsIgnoreCase(this.getAttribute("declare"));
    }

    public void setDeclare(final boolean declare) {
        this.setAttribute("declare", declare ? "declare" : null);
    }

    public HTMLFormElement getForm() {
        return (HTMLFormElement) this.getAncestorForJavaClass(HTMLFormElement.class);
    }

    public String getHspace() {
        return this.getAttribute("hspace");
    }

  /* public int getVspace() {
    try {
      return Integer.parseInt(this.getAttribute("vspace"));
    } catch (final Exception err) {
      return 0;
    }
  }*/

    public void setHspace(final String hspace) {
        this.setAttribute("hspace", hspace);
    }

    public String getStandby() {
        return this.getAttribute("standby");
    }

    public void setStandby(final String standby) {
        this.setAttribute("standby", standby);
    }

    public int getTabIndex() {
        try {
            return Integer.parseInt(this.getAttribute("tabindex"));
        } catch (final Exception err) {
            return 0;
        }
    }

    public void setTabIndex(final int tabIndex) {
        this.setAttribute("tabindex", String.valueOf(tabIndex));
    }

  /* public void setHspace(final int hspace) {
    this.setAttribute("hspace", String.valueOf(hspace));
  }*/

    public String getType() {
        return this.getAttribute("type");
    }

    public void setType(final String type) {
        this.setAttribute("type", type);
    }

    public String getUseMap() {
        return this.getAttribute("usemap");
    }

    public void setUseMap(final String useMap) {
        this.setAttribute("usemap", useMap);
    }

    public String getVspace() {
        return this.getAttribute("vspace");
    }

  /* public void setVspace(final int vspace) {
    this.setAttribute("vspace", String.valueOf(vspace));
  }*/

    public void setVspace(final String vspace) {
        this.setAttribute("vspace", vspace);
    }
}
