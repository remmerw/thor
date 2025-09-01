package io.github.remmerw.thor.parser

import io.github.remmerw.thor.dom.DOMImplementationImpl
import io.github.remmerw.thor.dom.HTMLDocumentImpl
import io.github.remmerw.thor.dom.HtmlRendererContext
import io.github.remmerw.thor.ua.UserAgentContext
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.xml.sax.EntityResolver
import org.xml.sax.ErrorHandler
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Logger
import javax.xml.parsers.DocumentBuilder

class DocumentModelBuilder : DocumentBuilder {
    private val context: UserAgentContext
    private val renderer: HtmlRendererContext?
    var resolver: EntityResolver? = null
        private set
    private var errorHandler: ErrorHandler? = null
    private var domImplementation: DOMImplementation? = null

    /**
     * Constructs a `DocumentBuilderImpl`. This constructor should be
     * used when only the parsing functionality (without rendering) is required.
     *
     * @param context An instance of [org.cobraparser.html.UserAgentContext],
     * which may be an instance of
     * [org.cobraparser.html.test.SimpleUserAgentContext].
     */
    constructor(context: UserAgentContext) {
        this.renderer = null
        this.context = context
    }

    /**
     * Constructs a `DocumentBuilderImpl`. This constructor should be
     * used when rendering is expected.
     *
     * @param ucontext An instance of [org.cobraparser.html.UserAgentContext],
     * which may be an instance of
     * [org.cobraparser.html.test.SimpleUserAgentContext].
     * @param rcontext An instance of [io.github.remmerw.thor.cobra.html.HtmlRendererContext],
     * which may be an instance of
     * [org.cobraparser.html.test.SimpleHtmlRendererContext].
     */
    constructor(ucontext: UserAgentContext, rcontext: HtmlRendererContext?) {
        this.renderer = rcontext
        this.context = ucontext
    }

    /**
     * Constructs a `DocumentBuilderImpl`. This constructor should be
     * used when rendering is expected.
     *
     * @param rcontext An instance of [io.github.remmerw.thor.cobra.html.HtmlRendererContext],
     * which may be an instance of
     * [org.cobraparser.html.test.SimpleHtmlRendererContext].
     */
    constructor(rcontext: HtmlRendererContext) {
        this.renderer = rcontext
        this.context = rcontext.userAgentContext()
    }

    /**
     * Parses an HTML document. Note that this method will read the entire input
     * source before returning a `Document` instance.
     *
     * @param `is` The input source, which may be an instance of
     * [InputSourceImpl].
     * @see .createDocument
     */
    @Throws(SAXException::class, IOException::class)
    override fun parse(inputSource: InputSource): Document {
        val document = this.createDocument(inputSource, "") as HTMLDocumentImpl
        document.load()
        return document
    }

    /**
     * Creates a document without parsing the input provided, so the document
     * object can be used for incremental rendering.
     *
     * @param `is` The input source, which may be an instance of
     * [InputSourceImpl]. The input
     * source must provide either an input stream or a reader.
     * @see HTMLDocumentImpl.load
     */
    @Throws(SAXException::class, IOException::class)
    fun createDocument(inputSource: InputSource, contentType: String?): Document {
        val encoding = inputSource.encoding
        var charset = encoding
        if (charset == null) {
            charset = "US-ASCII"
        }
        val uri = inputSource.systemId
        if (uri == null) {
            logger.warning("parse(): InputSource has no SystemId (URI); document item URLs will not be resolvable.")
        }
        val wis: WritableLineReader?
        val inputStream = inputSource.byteStream!!
        wis = WritableLineReader(InputStreamReader(inputStream, charset))

        val document = HTMLDocumentImpl(
            this.context, this.renderer,
            wis, uri!!, contentType
        )
        return document
    }

    override fun isNamespaceAware(): Boolean {
        return false
    }

    override fun isValidating(): Boolean {
        return false
    }

    override fun setEntityResolver(er: EntityResolver?) {
        this.resolver = er
    }

    override fun newDocument(): Document {
        return HTMLDocumentImpl(this.context)
    }

    override fun getDOMImplementation(): DOMImplementation {
        synchronized(this) {
            if (this.domImplementation == null) {
                this.domImplementation = DOMImplementationImpl(this.context)
            }
            return this.domImplementation!!
        }
    }

    fun getErrorHandler(): ErrorHandler? {
        return errorHandler
    }

    override fun setErrorHandler(eh: ErrorHandler?) {
        this.errorHandler = eh
    }

    companion object {
        private val logger: Logger = Logger.getLogger(DocumentModelBuilder::class.java.name)
    }
}
