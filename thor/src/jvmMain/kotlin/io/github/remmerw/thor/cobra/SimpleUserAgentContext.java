package io.github.remmerw.thor.cobra;

import java.net.URL;

import io.github.remmerw.thor.cobra.ua.NetworkRequest;
import io.github.remmerw.thor.cobra.ua.UserAgentContext;

public class SimpleUserAgentContext implements UserAgentContext {
    @Override
    public boolean isRequestPermitted(Request request) {
        return false;
    }

    @Override
    public NetworkRequest createHttpRequest() {
        return null;
    }

    @Override
    public String getAppCodeName() {
        return "";
    }

    @Override
    public String getAppName() {
        return "";
    }

    @Override
    public String getAppVersion() {
        return "";
    }

    @Override
    public String getAppMinorVersion() {
        return "";
    }

    @Override
    public String getBrowserLanguage() {
        return "";
    }

    @Override
    public boolean isCookieEnabled() {
        return false;
    }

    @Override
    public boolean isScriptingEnabled() {
        return false;
    }

    @Override
    public boolean isExternalCSSEnabled() {
        return false;
    }

    @Override
    public boolean isInternalCSSEnabled() {
        return false;
    }

    @Override
    public String getPlatform() {
        return "";
    }

    @Override
    public String getUserAgent() {
        return "";
    }

    @Override
    public String getCookie(URL url) {
        return "";
    }

    @Override
    public void setCookie(URL url, String cookieSpec) {

    }

    @Override
    public int getScriptingOptimizationLevel() {
        return 0;
    }

    @Override
    public boolean isMedia(String mediaName) {
        return false;
    }

    @Override
    public String getVendor() {
        return "";
    }

    @Override
    public String getProduct() {
        return "";
    }
}
