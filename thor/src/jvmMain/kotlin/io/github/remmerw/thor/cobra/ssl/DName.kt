package io.github.remmerw.thor.cobra.ssl

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.asn1.x500.style.IETFUtils
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
import java.security.cert.CertificateEncodingException
import java.security.cert.X509Certificate

/**
 * A distinguished name helper class: a 3-tuple of:
 *
 *  * the most specific common name (CN)
 *  * the most specific organization (O)
 *  * the most specific organizational unit (OU)
 *
 */
class DName private constructor(mCertificate: X509Certificate, subject: Boolean) {
    /**
     * Distinguished name (normally includes CN, O, and OU names)
     */
    private val mDName: String? = null

    /**
     * Common-name (CN) component of the name
     */
    private val mCName: String? = null

    /**
     * Organization (O) component of the name
     */
    private val mOName: String? = null

    /**
     * Organizational Unit (OU) component of the name
     */
    private val mUName: String? = null

    /**
     * Creates a new `DName` from a string. The attributes
     * are assumed to come in most significant to least
     * significant order which is true of human readable values
     * returned by methods such as `X500Principal.getName()`.
     * Be aware that the underlying sources of distinguished names
     * such as instances of `X509Certificate` are encoded in
     * least significant to most significant order, so make sure
     * the value passed here has the expected ordering of
     * attributes.
     */
    init {
        try {
            val x500Name: X500Name

            if (subject) {
                x500Name = JcaX509CertificateHolder(mCertificate).subject
            } else {
                x500Name = JcaX509CertificateHolder(mCertificate).issuer
            }

            val dName = x500Name.getRDNs(BCStyle.DN_QUALIFIER)[0]
            val cn = x500Name.getRDNs(BCStyle.CN)[0]
            val organization = x500Name.getRDNs(BCStyle.O)[0]
            val ou = x500Name.getRDNs(BCStyle.OU)[0]

            this.mDName = IETFUtils.valueToString(dName.getFirst().value)
            this.mCName = IETFUtils.valueToString(cn.getFirst().value)
            this.mOName = IETFUtils.valueToString(organization.getFirst().value)
            this.mUName = IETFUtils.valueToString(ou.getFirst().value)
        } catch (e: CertificateEncodingException) {
            // thrown here if there is an exception.
        }
    }

    val dName: String
        /**
         * @return The distinguished name (normally includes CN, O, and OU names)
         */
        get() = if (mDName != null) mDName else ""

    val cName: String
        /**
         * @return The most specific Common-name (CN) component of this name
         */
        get() = if (mCName != null) mCName else ""

    val oName: String
        /**
         * @return The most specific Organization (O) component of this name
         */
        get() = if (mOName != null) mOName else ""

    val uName: String
        /**
         * @return The most specific Organizational Unit (OU) component of this name
         */
        get() = if (mUName != null) mUName else ""

    companion object {
        fun createSubjectDName(mCertificate: X509Certificate): DName {
            return DName(mCertificate, true)
        }

        fun createIssuerDName(mCertificate: X509Certificate): DName {
            return DName(mCertificate, false)
        }
    }
}
