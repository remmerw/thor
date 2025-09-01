package io.github.remmerw.thor.cobra

import io.github.remmerw.thor.cobra.ua.NetworkRequest
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import java.net.URL

class SimpleUserAgentContext : UserAgentContext {
    override fun isRequestPermitted(request: UserAgentContext.Request?): Boolean {
        return false
    }

    override fun createHttpRequest(): NetworkRequest? {
        return null
    }

    override fun getAppCodeName(): String {
        return ""
    }

    override fun getAppName(): String {
        return ""
    }

    override fun getAppVersion(): String {
        return ""
    }

    override fun getAppMinorVersion(): String {
        return ""
    }

    override fun getBrowserLanguage(): String {
        return ""
    }

    override fun isCookieEnabled(): Boolean {
        return false
    }

    override fun isScriptingEnabled(): Boolean {
        return false
    }

    override fun isExternalCSSEnabled(): Boolean {
        return true
    }

    override fun isInternalCSSEnabled(): Boolean {
        return true
    }

    override fun getPlatform(): String {
        return ""
    }

    override fun getUserAgent(): String {
        return ""
    }

    override fun getCookie(url: URL?): String {
        return ""
    }

    override fun setCookie(url: URL?, cookieSpec: String?) {
    }

    override fun getScriptingOptimizationLevel(): Int {
        return 0
    }

    override fun isMedia(mediaName: String?): Boolean {
        return false
    }

    override fun getVendor(): String {
        return ""
    }

    override fun getProduct(): String {
        return ""
    }
}
