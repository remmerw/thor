/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Oct 8, 2005
 */
package io.github.remmerw.thor.cobra.html.domimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLScriptElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

import io.github.remmerw.thor.cobra.html.js.Executor;
import io.github.remmerw.thor.cobra.html.js.Window;
import io.github.remmerw.thor.cobra.html.js.Window.JSRunnableTask;
import io.github.remmerw.thor.cobra.ua.NetworkRequest;
import io.github.remmerw.thor.cobra.ua.UserAgentContext;
import io.github.remmerw.thor.cobra.ua.UserAgentContext.Request;
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind;
import io.github.remmerw.thor.cobra.util.SecurityUtil;

public class HTMLScriptElementImpl extends HTMLElementImpl implements HTMLScriptElement {
    private static final String[] jsTypes = {
            "application/ecmascript",
            "application/javascript",
            "application/x-ecmascript",
            "application/x-javascript",
            "text/ecmascript",
            "text/javascript",
            "text/javascript1.0",
            "text/javascript1.1",
            "text/javascript1.2",
            "text/javascript1.3",
            "text/javascript1.4",
            "text/javascript1.5",
            "text/jscript",
            "text/livescript",
            "text/x-ecmascript",
            "text/x-javascript"
    };
    private String text;
    private boolean defer;

    public HTMLScriptElementImpl() {
        super("SCRIPT", true);
    }

    public HTMLScriptElementImpl(final String name) {
        super(name, true);
    }

    public String getText() {
        final String t = this.text;
        if (t == null) {
            return this.getRawInnerText(true);
        } else {
            return t;
        }
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getHtmlFor() {
        return this.getAttribute("htmlFor");
    }

    public void setHtmlFor(final String htmlFor) {
        this.setAttribute("htmlFor", htmlFor);
    }

    public String getEvent() {
        return this.getAttribute("event");
    }

    public void setEvent(final String event) {
        this.setAttribute("event", event);
    }

    public boolean getDefer() {
        return this.defer;
    }

    public void setDefer(final boolean defer) {
        this.defer = defer;
    }

    public String getSrc() {
        return this.getAttribute("src");
    }

    public void setSrc(final String src) {
        this.setAttribute("src", src);
    }

    public String getType() {
        return this.getAttribute("type");
    }

    public void setType(final String type) {
        this.setAttribute("type", type);
    }

    protected final void processScript() {
        final String scriptType = getType();
        if (scriptType != null) {
            if (Arrays.stream(jsTypes).noneMatch(e -> e.equals(scriptType))) {
                ((HTMLDocumentImpl) HTMLScriptElementImpl.this.document).markJobsFinished(1, false);
                return;
            }
        }
        final UserAgentContext bcontext = this.getUserAgentContext();
        if (bcontext == null) {
            throw new IllegalStateException("No user agent context.");
        }
        final Document docObj = this.document;
        if (!(docObj instanceof HTMLDocumentImpl doc)) {
            throw new IllegalStateException("no valid document");
        }
        if (bcontext.isScriptingEnabled()) {
            String text;
            final String scriptURI;
            int baseLineNumber;
            final String src = this.getSrc();
            if (src == null) {
                final Request request = new Request(doc.getDocumentURL(), RequestKind.JavaScript);
                if (bcontext.isRequestPermitted(request)) {
                    text = this.getText();
                    scriptURI = doc.getBaseURI();
                    baseLineNumber = 1; // TODO: Line number of inner text??
                } else {
                    text = null;
                    scriptURI = null;
                    baseLineNumber = -1;
                }
            } else {
                this.informExternalScriptLoading();
                try {
                    final URL scriptURL = doc.getFullURL(src);
                    scriptURI = scriptURL.toExternalForm();
                    // Perform a synchronous request
                    final NetworkRequest request = bcontext.createHttpRequest();
                    SecurityUtil.doPrivileged(() -> {
                        // Code might have restrictions on accessing
                        // items from elsewhere.
                        try {
                            request.open("GET", scriptURI, false);
                            request.send(null, new Request(scriptURL, RequestKind.JavaScript));
                        } catch (final java.io.IOException thrown) {
                            logger.log(Level.WARNING, "processScript()", thrown);
                        }
                        return null;
                    });
                    final int status = request.getStatus();
                    if ((status != 200) && (status != 0)) {
                        this.warn("Script at [" + scriptURI + "] failed to load; HTTP status: " + status + ".");
                        return;
                    }
                    text = request.getResponseText();
                    baseLineNumber = 1;
                } catch (final MalformedURLException mfe) {
                    throw new IllegalArgumentException(mfe);
                }
            }

            final Window window = doc.getWindow();
            if (text != null) {
                final String textSub = text.substring(0, Math.min(50, text.length())).replaceAll("\n", "");
                window.addJSTaskUnchecked(new JSRunnableTask(0, "script: " + textSub, new Runnable() {
                    public void run() {
                        // final Context ctx = Executor.createContext(HTMLScriptElementImpl.this.getDocumentURL(), bcontext);
                        final Context ctx = Executor.createContext(HTMLScriptElementImpl.this.getDocumentURL(), bcontext, window.getContextFactory());
                        try {
                            final Scriptable scope = window.getWindowScope();
                            if (scope == null) {
                                throw new IllegalStateException("Scriptable (scope) instance was null");
                            }
                            try {
                                ctx.evaluateString(scope, text, scriptURI, baseLineNumber, null);
                                // Why catch this?
                                // } catch (final EcmaError ecmaError) {
                                // logger.log(Level.WARNING,
                                // "Javascript error at " + ecmaError.sourceName() + ":" + ecmaError.lineNumber() + ": " + ecmaError.getMessage(),
                                // ecmaError);
                            } catch (final Exception err) {
                                Executor.logJSException(err);
                            }
                        } finally {
                            Context.exit();
                            doc.markJobsFinished(1, false);
                        }
                    }
                }));
            } else {
                doc.markJobsFinished(1, false);
            }
        } else {
            doc.markJobsFinished(1, false);
        }
    }

    @Override
    protected void appendInnerTextImpl(final StringBuffer buffer) {
        // nop
    }

    @Override
    protected void handleDocumentAttachmentChanged() {
        if (isAttachedToDocument()) {
            ((HTMLDocumentImpl) document).addJob(() -> processScript(), false);
        } else {
            // TODO What does script element do when detached?
        }
        super.handleDocumentAttachmentChanged();
    }
}
