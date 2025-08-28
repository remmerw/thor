package io.github.remmerw.thor.cobra.html.domimpl;

import org.eclipse.jdt.annotation.NonNull;
import org.mozilla.javascript.Function;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLIFrameElement;

import java.net.URL;

import io.github.remmerw.thor.cobra.html.BrowserFrame;
import io.github.remmerw.thor.cobra.html.js.Event;
import io.github.remmerw.thor.cobra.html.js.Executor;
import io.github.remmerw.thor.cobra.html.js.Window;
import io.github.remmerw.thor.cobra.html.js.Window.JSRunnableTask;
import io.github.remmerw.thor.cobra.html.style.IFrameRenderState;
import io.github.remmerw.thor.cobra.html.style.RenderState;
import io.github.remmerw.thor.cobra.js.HideFromJS;
import io.github.remmerw.thor.cobra.ua.UserAgentContext.Request;
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind;

public class HTMLIFrameElementImpl extends HTMLAbstractUIElement implements HTMLIFrameElement, FrameNode {
    private volatile BrowserFrame browserFrame;
    private boolean jobCreated = false;
    private Function onload;

    public HTMLIFrameElementImpl(final String name) {
        super(name);
    }

    private void createJob() {
        synchronized (this) {
            final String src = this.getAttribute("src");
            if (src != null) {
                if (!jobCreated) {
                    ((HTMLDocumentImpl) document).addJob(() -> loadURLIntoFrame(src), false);
                    jobCreated = true;
                } else {
                    ((HTMLDocumentImpl) document).addJob(() -> loadURLIntoFrame(src), false, 0);
                }
            } else {
                markJobDone(0, isAttachedToDocument());
            }
        }
    }

    private void markJobDone(final int jobs, final boolean loaded) {
        synchronized (this) {
            ((HTMLDocumentImpl) document).markJobsFinished(jobs, false);
            jobCreated = false;

            if (loaded) {
                if (onload != null) {
                    // TODO: onload event object?
                    final Window window = ((HTMLDocumentImpl) document).getWindow();
                    window.addJSTask(new JSRunnableTask(0, "IFrame onload handler", () -> {
                        Executor.executeFunction(HTMLIFrameElementImpl.this, onload, new Event("load", HTMLIFrameElementImpl.this), window.getContextFactory());
                    }));
                }

                dispatchEvent(new Event("load", this));
            }
        }
    }

    public BrowserFrame getBrowserFrame() {
        return this.browserFrame;
    }

    @HideFromJS
    public void setBrowserFrame(final BrowserFrame frame) {
        this.browserFrame = frame;
        createJob();
    }

    public String getAlign() {
        return this.getAttribute("align");
    }

    public void setAlign(final String align) {
        this.setAttribute("align", align);
    }

    public Document getContentDocument() {
        // TODO: Domain-based security
        final BrowserFrame frame = this.browserFrame;
        if (frame == null) {
            // Not loaded yet
            return null;
        }

        {
            // TODO: Remove this very ugly hack.
            // This is required because the content document is sometimes not ready, even though the browser frame is.
            // The browser frame is created by the layout thread, but the iframe is loaded in the window's JS Scheduler thread.
            // See GH #140
            int count = 10;
            while (count > 0 && frame.getContentDocument() == null) {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    throw new RuntimeException("Error while waiting for iframe document");
                }
                count--;
            }
        }

        return frame.getContentDocument();
    }

    public void setContentDocument(final Document d) {
        final BrowserFrame frame = this.browserFrame;
        if (frame == null) {
            // TODO: This needs to be handled.
            return;
        }
        frame.setContentDocument(d);
    }

    public Window getContentWindow() {
        final BrowserFrame frame = this.browserFrame;
        if (frame == null) {
            // Not loaded yet
            return null;
        }
        return Window.getWindow(frame.getHtmlRendererContext());
    }

    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }

    public void setFrameBorder(final String frameBorder) {
        this.setAttribute("frameborder", frameBorder);
    }

    public String getHeight() {
        return this.getAttribute("height");
    }

    public void setHeight(final String height) {
        this.setAttribute("height", height);
    }

    public String getLongDesc() {
        return this.getAttribute("longdesc");
    }

    public void setLongDesc(final String longDesc) {
        this.setAttribute("longdesc", longDesc);
    }

    public String getMarginHeight() {
        return this.getAttribute("marginheight");
    }

    public void setMarginHeight(final String marginHeight) {
        this.setAttribute("marginHeight", marginHeight);
    }

    public String getMarginWidth() {
        return this.getAttribute("marginwidth");
    }

    public void setMarginWidth(final String marginWidth) {
        this.setAttribute("marginWidth", marginWidth);
    }

    public String getName() {
        return this.getAttribute("name");
    }

    public void setName(final String name) {
        this.setAttribute("name", name);
    }

    public String getScrolling() {
        return this.getAttribute("scrolling");
    }

    public void setScrolling(final String scrolling) {
        this.setAttribute("scrolling", scrolling);
    }

    public String getSrc() {
        return this.getAttribute("src");
    }

    public void setSrc(final String src) {
        this.setAttribute("src", src);
    }

    public String getWidth() {
        return this.getAttribute("width");
    }

    public void setWidth(final String width) {
        this.setAttribute("width", width);
    }

    @Override
    protected void handleAttributeChanged(String name, String oldValue, String newValue) {
        super.handleAttributeChanged(name, oldValue, newValue);
        if ("src".equals(name)) {
            createJob();
        }
    }

    @Override
    protected void handleDocumentAttachmentChanged() {
        super.handleDocumentAttachmentChanged();
        if (isAttachedToDocument()) {
            if (hasAttribute("onload")) {
                setOnload(getEventFunction(null, "onload"));
            }
        }
    }

    public Function getOnload() {
        return this.getEventFunction(this.onload, "onload");
    }

    public void setOnload(final Function onload) {
        this.onload = onload;
    }

    private void loadURLIntoFrame(final String value) {
        final BrowserFrame frame = this.browserFrame;
        if (frame != null) {
            try {
                final URL fullURL = value == null ? null : this.getFullURL(value);
                if (fullURL != null) {
                    if (getUserAgentContext().isRequestPermitted(new Request(fullURL, RequestKind.Frame))) {
                        frame.getHtmlRendererContext().setJobFinishedHandler(new Runnable() {
                            public void run() {
                                System.out.println("Iframes window's job over!");
                                markJobDone(1, true);
                            }
                        });
                        // frame.loadURL(fullURL);
                        // ^^ Using window.open is better because it fires the various events correctly.
                        getContentWindow().open(fullURL.toExternalForm(), "iframe", "", true);
                    } else {
                        System.out.println("Request not permitted: " + fullURL);
                        markJobDone(1, false);
                    }
                } else {
                    this.warn("Can't load URL: " + value);
                    // TODO: Plug: marking as load=true because we are not handling javascript URIs currently.
                    //       javascript URI is being used in some of the web-platform-tests.
                    markJobDone(1, true);
                }
            } catch (final java.net.MalformedURLException mfu) {
                this.warn("loadURLIntoFrame(): Unable to navigate to src.", mfu);
                markJobDone(1, false);
            }
        }
    }

    @Override
    protected @NonNull RenderState createRenderState(final RenderState prevRenderState) {
        return new IFrameRenderState(prevRenderState, this);
    }
}
