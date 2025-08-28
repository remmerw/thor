package io.github.remmerw.thor.cobra.ua;

import java.net.URL;

import io.github.remmerw.thor.cobra.html.HtmlRendererContext;
import io.github.remmerw.thor.cobra.html.parser.DocumentBuilderImpl;

/**
 * Provides information about the user agent (browser) driving the parser and/or
 * renderer.
 * <p>
 * A simple implementation of this interface is provided in
 * {@link org.cobraparser.html.test.SimpleUserAgentContext}.
 *
 * @see HtmlRendererContext#getUserAgentContext()
 * @see DocumentBuilderImpl#DocumentBuilderImpl(UserAgentContext)
 */
public interface UserAgentContext {
    boolean isRequestPermitted(final Request request);

    /**
     * Creates an instance of {@link org.cobraparser.html.HttpRequest} which can
     * be used by the renderer to load images, scripts, external style sheets, and
     * implement the Javascript XMLHttpRequest class (AJAX).
     */
    NetworkRequest createHttpRequest();

    /**
     * Gets browser "code" name.
     */
    String getAppCodeName();

    /**
     * Gets browser application name.
     */
    String getAppName();

    /**
     * Gets browser application version.
     */
    String getAppVersion();

    /**
     * Gets browser application minor version.
     */
    String getAppMinorVersion();

    /**
     * Gets browser language code. See <a
     * href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1
     * codes</a>.
     */
    String getBrowserLanguage();

    /**
     * Returns a boolean value indicating whether cookies are enabled in the user
     * agent. This value is used for reporting purposes only. TODO: Remove
     */
    boolean isCookieEnabled();

    /**
     * Returns a boolean value indicating whether scripting is enabled in the user
     * agent. If this value is <code>false</code>, the parser will not process
     * scripts and Javascript element attributes will have no effect. TODO: Remove
     */
    boolean isScriptingEnabled();

    /**
     * Returns a boolean value indicating whether remote (non-inline) CSS
     * documents should be loaded. TODO: Remove
     */
    boolean isExternalCSSEnabled();

    /**
     * Returns a boolean value indicating whether STYLE tags should be processed.
     * TODO: Remove
     */
    boolean isInternalCSSEnabled();

    /**
     * Gets the name of the user's operating system.
     */
    String getPlatform();

    /**
     * Should return the string used in the User-Agent header.
     */
    String getUserAgent();

    /**
     * Method used to implement Javascript <code>document.cookie</code> property.
     */
    String getCookie(URL url);

    /**
     * Method used to implement <code>document.cookie</code> property.
     *
     * @param cookieSpec Specification of cookies, as they would appear in the Set-Cookie
     *                   header value of HTTP.
     */
    void setCookie(URL url, String cookieSpec);


    /**
     * Gets the scripting optimization level, which is a value equivalent to
     * Rhino's optimization level.
     */
    int getScriptingOptimizationLevel();

    /**
     * Returns true if the current media matches the name provided.
     *
     * @param mediaName Media name, which may be <code>screen</code>, <code>tty</code>,
     *                  etc. (See <a href=
     *                  "http://www.w3.org/TR/REC-html40/types.html#type-media-descriptors"
     *                  >HTML Specification</a>).
     */
    boolean isMedia(String mediaName);

    String getVendor();

    String getProduct();

    enum RequestKind {
        Image("Img"), CSS("CSS"), Cookie("Cookie"), JavaScript("JS"), Frame("Frame"), XHR("XHR"), Referrer("Referrer");

        private static final RequestKind[] VALUES = RequestKind.values();
        public final String shortName;

        RequestKind(final String shortName) {
            this.shortName = shortName;
        }

        public static RequestKind forOrdinal(final int o) {
            return VALUES[o];
        }

        public static int numKinds() {
            return values().length;
        }
    }

    class Request {
        final public RequestKind kind;
        final public URL url;

        public Request(final URL url, final RequestKind kind) {
            this.kind = kind;
            this.url = url;
        }

        @Override
        public String toString() {
            return kind.toString() + ": " + url;
        }
    }
}
