package io.github.remmerw.thor.cobra.ssl

import io.github.remmerw.thor.cobra.util.HexDump
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.X509Certificate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class SslCertificate private constructor(
    validNotBefore: Date?, validNotAfter: Date?,
    /**
     * The original source certificate, if available.
     */
    val x509Certificate: X509Certificate?
) {
    /**
     * @return Issued-to distinguished name or null if none has been set
     */
    /**
     * Name of the entity this certificate is issued to
     */
    val issuedTo: DName

    /**
     * @return Issued-by distinguished name or null if none has been set
     */
    /**
     * Name of the entity this certificate is issued by
     */
    val issuedBy: DName

    /**
     * Not-before date from the validity period
     */
    private val mValidNotBefore: Date?

    /**
     * Not-after date from the validity period
     */
    private val mValidNotAfter: Date?

    /**
     * @return The `X509Certificate` used to create this `SslCertificate`
     */

    /**
     * Creates a new SSL certificate object from an X509 certificate
     *
     * @param certificate X509 certificate
     */
    constructor(certificate: X509Certificate) : this(
        certificate.notBefore,
        certificate.notAfter,
        certificate
    )

    init {
        this.issuedTo = DName.Companion.createSubjectDName(
            x509Certificate
        )
        this.issuedBy = DName.Companion.createIssuerDName(
            x509Certificate
        )
        mValidNotBefore = cloneDate(validNotBefore)
        mValidNotAfter = cloneDate(validNotAfter)
    }

    val validNotBeforeDate: Date?
        /**
         * @return Not-before date from the certificate validity period or
         * "" if none has been set
         */
        get() = cloneDate(mValidNotBefore)

    val validNotAfterDate: Date?
        /**
         * @return Not-after date from the certificate validity period or
         * "" if none has been set
         */
        get() = cloneDate(mValidNotAfter)

    /**
     * @return A string representation of this certificate for debugging
     */
    override fun toString(): String {
        return (("Issued to: " + issuedTo.getDName() + ";\n"
                + "Issued by: " + issuedBy.getDName() + ";\n"))
    }

    companion object {
        /**
         * SimpleDateFormat pattern for an ISO 8601 date
         */
        private const val ISO_8601_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ"

        private fun getSerialNumber(x509Certificate: X509Certificate?): String {
            if (x509Certificate == null) {
                return ""
            }

            val serialNumber = x509Certificate.serialNumber
            if (serialNumber == null) {
                return ""
            }

            return fingerprint(serialNumber.toByteArray())
        }

        private fun getDigest(x509Certificate: X509Certificate?, algorithm: String): String {
            if (x509Certificate == null) {
                return ""
            }

            try {
                val bytes = x509Certificate.encoded
                val md = MessageDigest.getInstance(algorithm)
                val digest = md.digest(bytes)
                return fingerprint(digest)
            } catch (ignored: CertificateEncodingException) {
                return ""
            } catch (ignored: NoSuchAlgorithmException) {
                return ""
            }
        }

        private fun fingerprint(bytes: ByteArray?): String {
            if (bytes == null) {
                return ""
            }

            val sb = StringBuilder()
            for (i in bytes.indices) {
                val b = bytes[i]
                HexDump.appendByteAsHex(sb, b, true)
                if (i + 1 != bytes.size) {
                    sb.append(':')
                }
            }

            return sb.toString()
        }

        /**
         * Parse an ISO 8601 date converting ParseExceptions to a null result;
         */
        private fun parseDate(string: String?): Date? {
            try {
                return SimpleDateFormat(ISO_8601_DATE_FORMAT).parse(string)
            } catch (e: ParseException) {
                return null
            }
        }

        /**
         * Format a date as an ISO 8601 string, return "" for a null date
         */
        private fun formatDate(date: Date?): String {
            if (date == null) {
                return ""
            }

            return SimpleDateFormat(ISO_8601_DATE_FORMAT).format(date)
        }

        /**
         * Clone a possibly null Date
         */
        private fun cloneDate(date: Date?): Date? {
            if (date == null) {
                return null
            }

            return date.clone() as Date
        }
    }
}
