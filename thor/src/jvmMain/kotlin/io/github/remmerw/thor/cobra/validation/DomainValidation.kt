package io.github.remmerw.thor.cobra.validation

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory
import io.github.remmerw.thor.cobra.util.Urls
import java.net.MalformedURLException
import java.net.URL
import java.util.LinkedList
import java.util.Locale

object DomainValidation {
    private val suffixList: PublicSuffixList = PublicSuffixListFactory().build()

    @JvmStatic
    fun isValidCookieDomain(domain: String, requestHostName: String): Boolean {
        var domain = domain
        val plainDomain: String?
        if (!domain.startsWith(".")) {
            // Valid domains must start with a dot according to RFC 2109, but
            // RFC 2965 specifies a dot is prepended in the Set-Cookie2 header.
            plainDomain = domain
            domain = ".$domain"
        } else {
            plainDomain = domain.substring(1)
        }
        val plainDomainTL = plainDomain.lowercase(Locale.getDefault())
        val hostNameTL = requestHostName.lowercase(Locale.getDefault())
        if (plainDomainTL == hostNameTL) {
            return true
        } else {
            if (!hostNameTL.endsWith(plainDomainTL)) {
                return false
            } else {
                // plainDomainTL is a suffix of hostName TL. Now ensure the first non-common character is a '.',
                // and there is a residual character after that
                val nonCommonLength = hostNameTL.length - plainDomainTL.length
                val residualCharacterExists = nonCommonLength >= 2
                if (!residualCharacterExists) {
                    return false
                } else {
                    val firstNonCommonCharacter = hostNameTL.get(nonCommonLength - 1)
                    if (firstNonCommonCharacter != '.') {
                        return false
                    }
                }
            }
        }

        return !isPublicSuffix(plainDomain)
    }

    /**
     * Returns true if the given domain is a public suffix.
     *
     * @param domain The domain to check. Expected **not** to have any leading or
     * trailing '.'
     * @return true if the given domain is a public suffix
     */
    fun isPublicSuffix(domain: String): Boolean {
        return suffixList.isPublicSuffix(domain)
    }

    /**
     * Returns a collection of domains that are acceptable for cookies originating
     * from the given hostname
     */
    fun getPossibleDomains(hostName: String): MutableCollection<String?> {
        // TODO: reuse collection object instead of creating a new one per recursive call.
        val domains: MutableCollection<String?> = LinkedList<String?>()
        domains.add(hostName)
        val dotIdx = hostName.indexOf('.', 1)
        if (dotIdx == -1) {
            return domains
        }
        val testDomain = hostName.substring(dotIdx)
        if (!isValidCookieDomain(testDomain, hostName)) {
            return domains
        }
        domains.addAll(getPossibleDomains(testDomain.substring(1)))
        return domains
    }

    fun isLikelyHostName(name: String): Boolean {
        val nameTL = name.lowercase(Locale.getDefault())
        if (nameTL.startsWith("www.")) {
            return true
        }
        if (endsWithGTLD(name)) {
            return true
        }
        val lastDotIdx = nameTL.lastIndexOf('.')
        if (lastDotIdx == -1) {
            return false
        }
        // Check for country code.
        return lastDotIdx == (nameTL.length - 3)
    }

    private fun endsWithGTLD(name: String): Boolean {
        if (name.length == 0) {
            return false
        } else if (isPublicSuffix(name)) {
            return true
        } else {
            val sepIndex = name.indexOf('.')
            if (sepIndex < 0) {
                return false
            } else {
                return endsWithGTLD(name.substring(sepIndex + 1))
            }
        }
    }

    @Throws(MalformedURLException::class)
    fun guessURL(baseURL: URL?, spec: String): URL {
        var baseURL = baseURL
        var spec = spec
        var finalURL: URL
        try {
            if (baseURL != null) {
                val colonIdx = spec.indexOf(':')
                val newProtocol = if (colonIdx == -1) null else spec.substring(0, colonIdx)
                if ((newProtocol != null) && !newProtocol.equals(
                        baseURL.protocol,
                        ignoreCase = true
                    )
                ) {
                    baseURL = null
                }
            }
            finalURL = Urls.createURL(baseURL, spec)
        } catch (mfu: MalformedURLException) {
            spec = spec.trim { it <= ' ' }
            val idx = spec.indexOf(':')
            if (idx == -1) {
                val slashIdx = spec.indexOf('/')
                if (slashIdx == 0) {
                    // A file, absolute
                    finalURL = URL("file:" + spec)
                } else {
                    if (slashIdx == -1) {
                        // No slash, no colon, must be host.
                        finalURL = URL(baseURL, "http://" + spec)
                    } else {
                        val possibleHost =
                            spec.substring(0, slashIdx).lowercase(Locale.getDefault())
                        finalURL = guessProtocol(baseURL, spec, possibleHost)
                    }
                }
            } else {
                if (idx == 1) {
                    // Likely a drive
                    finalURL = URL(baseURL, "file:" + spec)
                } else {
                    val possibleHost = spec.substring(0, idx).lowercase(Locale.getDefault())
                    finalURL = guessProtocol(baseURL, spec, possibleHost)
                }
            }
        }
        if ("" != finalURL.host && (finalURL.toExternalForm().indexOf(' ') != -1)) {
            throw MalformedURLException("There are blanks in the URL: " + finalURL.toExternalForm())
        }
        return finalURL
    }

    @Throws(MalformedURLException::class)
    private fun guessProtocol(baseURL: URL?, spec: String?, possibleHost: String): URL {
        if (isLikelyHostName(possibleHost)) {
            // TODO: Use https when possible
            return URL(baseURL, "http://" + spec)
        } else {
            // TODO: Should file URLs be guessed? Probably not.
            return URL(baseURL, "file:" + spec)
        }
    }

    @Throws(MalformedURLException::class)
    fun guessURL(spec: String): URL {
        return guessURL(null, spec)
    }
}
