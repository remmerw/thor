package io.github.remmerw.thor.cobra.html.js;

import org.eclipse.jdt.annotation.NonNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.remmerw.thor.cobra.html.js.Window.JSRunnableTask;
import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate;
import io.github.remmerw.thor.cobra.js.JavaScript;
import io.github.remmerw.thor.cobra.ua.NetworkRequest;
import io.github.remmerw.thor.cobra.ua.UserAgentContext;
import io.github.remmerw.thor.cobra.ua.UserAgentContext.Request;
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind;
import io.github.remmerw.thor.cobra.util.DOMExceptions;
import io.github.remmerw.thor.cobra.util.Urls;

public class XMLHttpRequest extends AbstractScriptableDelegate {
    // TODO: See reference:
    // http://www.xulplanet.com/references/objref/XMLHttpRequest.html

    private static final Logger logger = Logger.getLogger(XMLHttpRequest.class.getName());
    // excluded as per https://dvcs.w3.org/hg/xhr/raw-file/default/xhr-1/Overview.html
    private static final List<String> excludedResponseHeadersLowerCase = Arrays.asList(
            "set-cookie",
            "set-cookie2"
    );
    private static final String[] prohibitedMethods = {
            "CONNECT", "TRACE", "TRACK"
    };
    private static final String[] upperCaseMethods = {
            "DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT"
    };
    // This list comes from https://dvcs.w3.org/hg/xhr/raw-file/tip/Overview.html#the-setrequestheader()-method
    // It has been lower-cased for faster comparison
    private static final String[] prohibitedHeaders = {
            "accept-charset",
            "accept-encoding",
            "access-control-request-headers",
            "access-control-request-method",
            "connection",
            "content-length",
            "cookie",
            "cookie2",
            "date",
            "dnt",
            "expect",
            "host",
            "keep-alive",
            "origin",
            "referer",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade",
            "user-agent",
            "via"
    };
    private final NetworkRequest request;
    private final UserAgentContext pcontext;
    private final Scriptable scope;
    private final URL codeSource;
    // TODO: This is a quick hack
    private final Window window;
    private Function onreadystatechange;
    private boolean listenerAdded;
    private Function onLoad;

    public XMLHttpRequest(final UserAgentContext pcontext, final URL codeSource, final Scriptable scope, final Window window) {
        this.request = pcontext.createHttpRequest();
        this.pcontext = pcontext;
        this.scope = scope;
        this.codeSource = codeSource;
        this.window = window;
    }

    private static String checkAndAdjustMethod(final String method) {
        for (final String p : prohibitedMethods) {
            if (p.equalsIgnoreCase(method)) {
                throw DOMExceptions.ExtendedError.SecurityError.createException();
            }
        }

        for (final String u : upperCaseMethods) {
            if (u.equalsIgnoreCase(method)) {
                return u;
            }
        }

        return method;
    }

    private static boolean isSameOrigin(final URL url1, final URL url2) {
        return url1.getHost().equals(url2.getHost()) &&
                (url1.getPort() == (url2.getPort())) &&
                url1.getProtocol().equals(url2.getProtocol());

    }

    private static boolean isProhibited(final String header) {
        final String headerTL = header.toLowerCase();
        for (final String prohibitedHeader : prohibitedHeaders) {
            if (prohibitedHeader.equals(headerTL)) {
                return true;
            }
        }
        final boolean prohibitedPrefixMatch = headerTL.startsWith("proxy-") || headerTL.startsWith("sec-");
        return prohibitedPrefixMatch;
    }

    private static boolean isWellFormattedHeaderValue(final String header, final String value) {
        // TODO Needs implementation as per https://dvcs.w3.org/hg/xhr/raw-file/tip/Overview.html#the-setrequestheader()-method
        return true;
    }

    public void abort() {
        request.abort();
    }

    @NotGetterSetter
    public String getAllResponseHeaders() {
        // TODO: Need to also filter out based on CORS
        return request.getAllResponseHeaders(excludedResponseHeadersLowerCase);
    }

    public int getReadyState() {
        return request.getReadyState();
    }

    public byte[] getResponseBytes() {
        return request.getResponseBytes();
    }

    public String getResponseHeader(final String headerName) {
        // TODO: Need to also filter out based on CORS
        if (excludedResponseHeadersLowerCase.contains(headerName.toLowerCase())) {
            return request.getResponseHeader(headerName);
        } else {
            return null;
        }
    }

    public String getResponseText() {
        return request.getResponseText();
    }

    public Document getResponseXML() {
        return request.getResponseXML();
    }

    public int getStatus() {
        return request.getStatus();
    }

    public String getStatusText() {
        return request.getStatusText();
    }

    private @NonNull URL getFullURL(final String relativeUrl) throws MalformedURLException {
        return Urls.createURL(this.codeSource, relativeUrl);
    }

    public void open(final String method, final String url, final boolean asyncFlag, final String userName, final String password)
            throws IOException {
        final String adjustedMethod = checkAndAdjustMethod(method);
        try {
            request.open(adjustedMethod, this.getFullURL(url), asyncFlag, userName, password);
        } catch (final MalformedURLException mfe) {
            throw ScriptRuntime.typeError("url malformed");
        }
    }

    public void open(final String method, final String url, final boolean asyncFlag, final String userName) throws IOException {
        final String adjustedMethod = checkAndAdjustMethod(method);
        request.open(adjustedMethod, this.getFullURL(url), asyncFlag, userName);
    }

    public void open(final String method, final String url, final boolean asyncFlag) throws IOException {
        final String adjustedMethod = checkAndAdjustMethod(method);
        request.open(adjustedMethod, this.getFullURL(url), asyncFlag);
    }

    public void open(final String method, final String url) throws IOException {
        final String adjustedMethod = checkAndAdjustMethod(method);
        request.open(adjustedMethod, this.getFullURL(url));
    }
    // private boolean listenerAddedLoad;

    public void send(final String content) throws IOException {
        final Optional<URL> urlOpt = request.getURL();
        if (urlOpt.isPresent()) {
            final URL url = urlOpt.get();
            if (isSameOrigin(url, codeSource)) {
                // final URLPermission urlPermission = new URLPermission(url.toExternalForm());
                // final SocketPermission socketPermission = new SocketPermission(url.getHost() + ":" + Urls.getPort(url), "connect,resolve");
                // final StoreHostPermission storeHostPermission = StoreHostPermission.forURL(url);

                request.send(content, new Request(url, RequestKind.XHR));

                // }
            } else {
                final String msg = String.format("Failed to execute 'send' on 'XMLHttpRequest': Failed to load '%s'", url.toExternalForm());
                throw DOMExceptions.ExtendedError.NetworkError.createException(msg);
            }
        }
    }

    public Function getOnreadystatechange() {
        synchronized (this) {
            return this.onreadystatechange;
        }
    }

    public void setOnreadystatechange(final Function value) {
        synchronized (this) {
            this.onreadystatechange = value;
            if ((value != null) && !this.listenerAdded) {
                this.request.addNetworkRequestListener(netEvent -> executeReadyStateChange());
                this.listenerAdded = true;
            }
        }
    }

    public void setOnload(final Function value) {
        synchronized (this) {
            this.onLoad = value;
            if ((value != null) && !this.listenerAdded) {
                this.request.addNetworkRequestListener(netEvent -> executeReadyStateChange());
                this.listenerAdded = true;
            }
        }
    }

    private void executeReadyStateChange() {
        // Not called in GUI thread to ensure consistency of readyState.
        try {
            final Function f = XMLHttpRequest.this.getOnreadystatechange();
            if (f != null) {
                window.addJSTask(new JSRunnableTask(0, "xhr ready state changed: " + request.getReadyState(), () -> {
                    final Context ctx = Executor.createContext(this.codeSource, this.pcontext, window.getContextFactory());
                    try {
                        final Scriptable newScope = (Scriptable) JavaScript.getInstance().getJavascriptObject(XMLHttpRequest.this, this.scope);
                        f.call(ctx, newScope, newScope, new Object[0]);
                    } finally {
                        Context.exit();
                    }
                }));
            }
        } catch (final Exception err) {
            logger.log(Level.WARNING, "Error processing ready state change.", err);
            Executor.logJSException(err);
        }

        if (request.getReadyState() == NetworkRequest.STATE_COMPLETE) {
            try {
                final Function f = this.onLoad;
                if (f != null) {
                    window.addJSTaskUnchecked(new JSRunnableTask(0, "xhr on load : ", () -> {
                        final Context ctx = Executor.createContext(this.codeSource, this.pcontext, window.getContextFactory());
                        try {
                            final Scriptable newScope = (Scriptable) JavaScript.getInstance().getJavascriptObject(XMLHttpRequest.this, this.scope);
                            f.call(ctx, newScope, newScope, new Object[0]);
                        } finally {
                            Context.exit();
                        }
                    }));
                }
            } catch (final Exception err) {
                logger.log(Level.WARNING, "Error processing ready state change.", err);
            }
        }
    }

    // As per: http://www.w3.org/TR/XMLHttpRequest2/#the-setrequestheader-method
    public void setRequestHeader(final String header, final String value) {
        final int readyState = request.getReadyState();
        if (readyState == NetworkRequest.STATE_LOADING) {
            if (isWellFormattedHeaderValue(header, value)) {
                if (!isProhibited(header)) {
                    request.addRequestedHeader(header, value);
                } else {
                    // TODO: Throw exception?
                    System.out.println("Prohibited header: " + header);
                }
            } else {
                throw new DOMException(DOMException.SYNTAX_ERR, "header or value not well formatted");
            }
        } else {
            throw new DOMException(DOMException.INVALID_STATE_ERR, "Can't set header when request state is: " + readyState);
        }
    }

}
