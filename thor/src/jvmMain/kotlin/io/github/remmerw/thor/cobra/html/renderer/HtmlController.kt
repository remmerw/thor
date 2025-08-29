package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.FormInput
import io.github.remmerw.thor.cobra.html.domimpl.ElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLAbstractUIElement
import io.github.remmerw.thor.cobra.html.domimpl.HTMLButtonElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLInputElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLLinkElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLSelectElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.ModelNode
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.js.Event
import io.github.remmerw.thor.cobra.html.js.Executor.executeFunction
import org.mozilla.javascript.ContextFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.Cursor
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.Locale
import java.util.Optional
import java.util.logging.Level
import java.util.logging.Logger

internal class HtmlController {
    /**
     * @return True to propagate further and false if the event was consumed.
     */
    fun onEnterPressed(node: ModelNode?, event: InputEvent?): Boolean {
        if (node is HTMLInputElementImpl) {
            if (node.isSubmittableWithEnterKey) {
                node.submitForm(null)
                return false
            }
        }
        // No propagation
        return false
    }

    /**
     * @return True to propagate further and false if the event was consumed.
     */
    @JvmOverloads
    fun onMouseClick(
        node: ModelNode,
        event: MouseEvent?,
        x: Int,
        y: Int,
        eventDispatched: Boolean = false
    ): Boolean {
        var eventDispatched = eventDispatched
        if (logger.isLoggable(Level.INFO)) {
            logger.info("onMouseClick(): node=" + node + ",class=" + node.javaClass.name)
        }

        // System.out.println("HtmlController.onMouseClick(): " + node + " already dispatched: " + eventDispatched);

        // Get the node which is a valid Event target
        /*{
      NodeImpl target = (NodeImpl)node;
      while(target.getParentNode() != null) {
        if (target instanceof Element || target instanceof Document) { //  TODO || node instanceof Window) {
          break;
        }
        target = (NodeImpl) target.getParentNode();
      }
      final Event jsEvent = new Event("click", target, event, x, y);
      target.dispatchEvent(jsEvent);
    }*/
        if (node is HTMLAbstractUIElement) {
            val jsEvent = Event("click", node, event, x, y)

            // System.out.println("Ui element: " + uiElement.getId());
            // uiElement.dispatchEvent(jsEvent);
            val f = node.onclick
            /* TODO: This is the original code which would return immediately if f returned false. */
            if (f != null) {
                // Changing argument to uiElement instead of event
                if (!executeFunction(node, f, jsEvent, getWindowFactory(node))) {
                    // if (!Executor.executeFunction(uiElement, f, uiElement, getWindowFactory(uiElement))) {
                    return false
                }
            }

            /*
      // Alternate JS Task version:
      if (f != null) {
        runFunction(uiElement, f, jsEvent);
      }*/
            val rcontext = node.htmlRendererContext
            if (rcontext != null) {
                if (!rcontext.onMouseClick(node, event)) {
                    return false
                }
            }
        }
        if (node is HTMLLinkElementImpl) {
            val navigated = node.navigate()
            if (navigated) {
                return false
            }
        } else if (node is HTMLButtonElementImpl) {
            val rawType = node.getAttribute("type")
            val type: String?
            if (rawType == null) {
                type = "submit"
            } else {
                type = rawType.trim { it <= ' ' }.lowercase(Locale.getDefault())
            }
            if ("submit" == type) {
                val formInputs: Array<FormInput?>?
                val name = node.name
                if (name == null) {
                    formInputs = null
                } else {
                    formInputs = arrayOf<FormInput>(FormInput(name, node.value))
                }
                node.submitForm(formInputs)
                return false
            } else if ("reset" == type) {
                node.resetForm()
                return false
            } else if ("button" == type) {
                println("Button TODO;")
            } else {
                // NOP for "button"!
            }
        }
        if (!eventDispatched) {
            // Get the node which is a valid Event target
            if ((node is Element) || (node is Document)) { //  TODO || node instanceof Window) {
                // System.out.println("Click accepted on " + node);
                val target = node as NodeImpl
                val jsEvent = Event("click", target, event, x, y)
                target.dispatchEvent(jsEvent)
                eventDispatched = true
            }
        }
        // } else {
        // System.out.println("Bumping click to parent");
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onMouseClick(parent, event, x, y, eventDispatched)
        // }
        // return false;
        /*
    final ModelNode parent = node.getParentModelNode();
    if (parent == null) {
      return true;
    }
    return this.onMouseClick(parent, event, x, y);*/
    }

    fun onMiddleClick(node: ModelNode, event: MouseEvent?, x: Int, y: Int): Boolean {
        if (node is HTMLAbstractUIElement) {
            val rcontext = node.htmlRendererContext
            if (rcontext != null) {
                // Needs to be done after Javascript, so the script
                // is able to prevent it.
                if (!rcontext.onMiddleClick(node, event)) {
                    return false
                }
            }
        }
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onMiddleClick(parent, event, x, y)
    }

    fun onContextMenu(node: ModelNode, event: MouseEvent?, x: Int, y: Int): Boolean {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("onContextMenu(): node=" + node + ",class=" + node.javaClass.name)
        }
        if (node is HTMLAbstractUIElement) {
            val f = node.oncontextmenu
            if (f != null) {
                val jsEvent = Event("contextmenu", node, event, x, y)
                if (!executeFunction(node, f, jsEvent, getWindowFactory(node))) {
                    return false
                }
            }
            val rcontext = node.htmlRendererContext
            if (rcontext != null) {
                // Needs to be done after Javascript, so the script
                // is able to prevent it.
                if (!rcontext.onContextMenu(node, event)) {
                    return false
                }
            }
        }
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onContextMenu(parent, event, x, y)
    }

    fun onMouseOver(
        renderable: BaseBoundableRenderable?, nodeStart: ModelNode?, event: MouseEvent?, x: Int,
        y: Int, limit: ModelNode?
    ) {
        run {
            var node = nodeStart
            while (node != null) {
                if (node === limit) {
                    break
                }
                if (node is HTMLAbstractUIElement) {
                    node.setMouseOver(true)
                    val f = node.onmouseover
                    if (f != null) {
                        val jsEvent = Event("mouseover", node, event, x, y)
                        executeFunction(node, f, jsEvent, getWindowFactory(node))
                    }
                    val rcontext = node.htmlRendererContext
                    if (rcontext != null) {
                        rcontext.onMouseOver(node, event)
                    }
                }
                node = node.parentModelNode
            }
        }

        setMouseOnMouseOver(renderable, nodeStart, limit)
    }

    fun onMouseOut(nodeStart: ModelNode?, event: MouseEvent?, x: Int, y: Int, limit: ModelNode?) {
        run {
            var node = nodeStart
            while (node != null) {
                if (node === limit) {
                    break
                }
                if (node is HTMLAbstractUIElement) {
                    node.setMouseOver(false)
                    val f = node.onmouseout
                    if (f != null) {
                        val jsEvent = Event("mouseout", node, event, x, y)
                        executeFunction(node, f, jsEvent, getWindowFactory(node))
                    }
                    val rcontext = node.htmlRendererContext
                    if (rcontext != null) {
                        rcontext.onMouseOut(node, event)
                    }
                }
                node = node.parentModelNode
            }
        }

        resetCursorOnMouseOut(nodeStart, limit)
    }

    /**
     * @return True to propagate further, false if consumed.
     */
    fun onDoubleClick(node: ModelNode, event: MouseEvent?, x: Int, y: Int): Boolean {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("onDoubleClick(): node=" + node + ",class=" + node.javaClass.name)
        }
        if (node is HTMLAbstractUIElement) {
            val f = node.ondblclick
            if (f != null) {
                val jsEvent = Event("dblclick", node, event, x, y)
                if (!executeFunction(node, f, jsEvent, getWindowFactory(node))) {
                    return false
                }
            }
            val rcontext = node.htmlRendererContext
            if (rcontext != null) {
                if (!rcontext.onDoubleClick(node, event)) {
                    return false
                }
            }
        }
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onDoubleClick(parent, event, x, y)
    }

    /**
     * @return True to propagate further, false if consumed.
     */
    fun onMouseDisarmed(node: ModelNode, event: MouseEvent?): Boolean {
        if (node is HTMLLinkElementImpl) {
            node.getCurrentStyle().overlayColor = (null)
            return false
        }
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onMouseDisarmed(parent, event)
    }

    /**
     * @return True to propagate further, false if consumed.
     */
    fun onMouseDown(node: ModelNode, event: MouseEvent?, x: Int, y: Int): Boolean {
        var pass = true
        if (node is HTMLAbstractUIElement) {
            val f = node.onmousedown
            if (f != null) {
                val jsEvent = Event("mousedown", node, event, x, y)
                pass = executeFunction(node, f, jsEvent, getWindowFactory(node))
            }
        }
        if (node is HTMLLinkElementImpl) {
            node.getCurrentStyle().overlayColor = ("#9090FF80")
            return false
        }
        if (!pass) {
            return false
        }
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onMouseDown(parent, event, x, y)
    }

    /**
     * @return True to propagate further, false if consumed.
     */
    fun onMouseUp(node: ModelNode, event: MouseEvent?, x: Int, y: Int): Boolean {
        var pass = true
        if (node is HTMLAbstractUIElement) {
            val f = node.onmouseup
            if (f != null) {
                val jsEvent = Event("mouseup", node, event, x, y)
                pass = executeFunction(node, f, jsEvent, getWindowFactory(node))
            }
        }
        if (node is HTMLLinkElementImpl) {
            node.getCurrentStyle().overlayColor = (null)
            return false
        }
        if (!pass) {
            return false
        }
        val parent = node.parentModelNode
        if (parent == null) {
            return true
        }
        return this.onMouseUp(parent, event, x, y)
    }

    /**
     * @param node The node generating the event.
     * @param x    For images only, x coordinate of mouse click.
     * @param y    For images only, y coordinate of mouse click.
     * @return True to propagate further, false if consumed.
     */
    fun onPressed(node: ModelNode?, event: InputEvent?, x: Int, y: Int): Boolean {
        if (node is HTMLAbstractUIElement) {
            val f = node.onclick
            if (f != null) {
                val jsEvent = Event("click", node, event, x, y)
                if (!executeFunction(node, f, jsEvent, getWindowFactory(node))) {
                    return false
                }
            }
        }
        if (node is HTMLInputElementImpl) {
            if (node.isSubmitInput) {
                val formInputs: Array<FormInput>?
                val name = node.name
                if (name == null) {
                    formInputs = null
                } else {
                    formInputs = arrayOf<FormInput>(FormInput(name, node.value))
                }
                node.submitForm(formInputs)
            } else if (node.isImageInput) {
                val name = node.name
                val prefix = if (name == null) "" else name + "."
                val extraFormInputs: Array<FormInput?> = arrayOf<FormInput>(
                    FormInput(prefix + "x", x.toString()),
                    FormInput(prefix + "y", y.toString())
                )
                node.submitForm(extraFormInputs)
            } else if (node.isResetInput) {
                node.resetForm()
            }
        }
        if (node is HTMLElementImpl) {
            val evt = Event("click", node, event, x, y)
            node.dispatchEvent(evt)
        }
        // No propagate
        return false
    }

    fun onChange(node: ModelNode?): Boolean {
        if (node is HTMLSelectElementImpl) {
            val f = node.onchange
            if (f != null) {
                val jsEvent = Event("change", node)
                if (!executeFunction(node, f, jsEvent, getWindowFactory(node))) {
                    return false
                }
            }
        }
        // No propagate
        return false
    }

    fun onKeyUp(node: ModelNode?, ke: KeyEvent?): Boolean {
        var pass = true
        if (node is NodeImpl) {
            val jsEvent = Event("keyup", node, ke)
            pass = node.dispatchEvent(jsEvent)
            println("Dispatch result: " + pass)
        }
        /*
    if (node instanceof HTMLAbstractUIElement) {
      final HTMLAbstractUIElement uiElement = (HTMLAbstractUIElement) node;
      final Function f = uiElement.getOnmouseup();
      if (f != null) {
        final Event jsEvent = new Event("keyup", uiElement, ke);
        pass = Executor.executeFunction(uiElement, f, jsEvent);
      }
    }*/
        return pass
    }

    companion object {
        private val logger: Logger = Logger.getLogger(HtmlController::class.java.name)
        val instance: HtmlController = HtmlController()

        // Quick hack
        private fun getWindowFactory(e: ElementImpl): ContextFactory {
            val doc = e.ownerDocument as HTMLDocumentImpl?
            return doc!!.window.contextFactory
        }

        private fun setMouseOnMouseOver(
            renderable: BaseBoundableRenderable?,
            nodeStart: ModelNode?,
            limit: ModelNode?
        ) {
            var node = nodeStart
            while (node != null) {
                if (node === limit) {
                    break
                }
                if (node is NodeImpl) {
                    val rcontext = node.htmlRendererContext
                    val rs = node.getRenderState()
                    val cursorOpt = rs.cursor
                    if (rcontext != null) {
                        if (cursorOpt!!.isPresent()) {
                            rcontext.setCursor(cursorOpt)
                            break
                        } else {
                            if (node.parentModelNode === limit) {
                                if ((renderable is RWord) || (renderable is RBlank)) {
                                    rcontext.setCursor(
                                        Optional.of<Cursor>(
                                            Cursor.getPredefinedCursor(
                                                Cursor.TEXT_CURSOR
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                node = node.parentModelNode
            }
        }

        // Quick hack
        /*
  private static boolean runFunction(final ElementImpl e, final Function f, final Event event) {
    final HTMLDocumentImpl doc = (HTMLDocumentImpl) e.getOwnerDocument();
    final Window window = doc.getWindow();
    window.addJSTask(new JSRunnableTask(0, "function from HTMLController", () -> {
      Executor.executeFunction(e, f, event, window.getContextFactory());
    }));
    return false;
  }
  */
        private fun resetCursorOnMouseOut(nodeStart: ModelNode?, limit: ModelNode?) {
            var foundCursorOpt: Optional<Cursor> = Optional.empty<Cursor>()
            var node = limit
            while (node != null) {
                if (node is NodeImpl) {
                    val rs = node.getRenderState()
                    val cursorOpt = rs.cursor
                    foundCursorOpt = cursorOpt!!
                    if (cursorOpt.isPresent) {
                        break
                    }
                }
                node = node.parentModelNode
            }

            if (nodeStart is NodeImpl) {
                val rcontext = nodeStart.htmlRendererContext
                // rcontext.setCursor(Optional.empty());
                if (rcontext != null) {
                    rcontext.setCursor(foundCursorOpt)
                }
            }
        }
    }
}
