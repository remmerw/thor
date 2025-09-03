package io.github.remmerw.thor.dom

import io.github.remmerw.thor.parser.HtmlParser
import org.w3c.dom.UserDataHandler
import java.lang.Boolean
import kotlin.Any
import kotlin.String

class HTMLTitleElementModel(name: String) : HTMLElementModel(name) {
    override fun setUserData(key: String, data: Any?, handler: UserDataHandler?): Any? {
        if (HtmlParser.MODIFYING_KEY == key && (data == Boolean.FALSE)) {
            val document = this.document
            if (document is HTMLDocumentImpl) {
                val textContent = this.getTextContent()
                val title = textContent?.trim { it <= ' ' }
                document.title = title
            }
        }
        return super.setUserData(key, data, handler)
    }
}
