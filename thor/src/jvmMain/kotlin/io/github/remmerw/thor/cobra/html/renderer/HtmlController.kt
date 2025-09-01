package io.github.remmerw.thor.cobra.html.renderer

import io.github.remmerw.thor.cobra.html.FormInput
import io.github.remmerw.thor.cobra.html.dom.ElementImpl
import io.github.remmerw.thor.cobra.html.dom.HTMLAbstractUIElement
import io.github.remmerw.thor.cobra.html.dom.HTMLButtonElementImpl
import io.github.remmerw.thor.cobra.html.dom.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.dom.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.dom.HTMLInputElementImpl
import io.github.remmerw.thor.cobra.html.dom.HTMLLinkElementImpl
import io.github.remmerw.thor.cobra.html.dom.HTMLSelectElementImpl
import io.github.remmerw.thor.cobra.html.dom.ModelNode
import io.github.remmerw.thor.cobra.html.dom.NodeImpl
import io.github.remmerw.thor.cobra.html.js.Event
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
                val formInputs: Array<FormInput>?
                val name = node.getName()
                if (name == null) {
                    formInputs = null
                } else {
                    formInputs = arrayOf(FormInput(name, node.getValue()))
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
        val parent = node.parentModelNode()
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
        val parent = node.parentModelNode()
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

            val rcontext = node.htmlRendererContext
            if (rcontext != null) {
                // Needs to be done after Javascript, so the script
                // is able to prevent it.
                if (!rcontext.onContextMenu(node, event)) {
                    return false
                }
            }
        }
        val parent = node.parentModelNode()
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

                    val rcontext = node.htmlRendererContext
                    if (rcontext != null) {
                        rcontext.onMouseOver(node, event)
                    }
                }
                node = node.parentModelNode()
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

                    val rcontext = node.htmlRendererContext
                    if (rcontext != null) {
                        rcontext.onMouseOut(node, event)
                    }
                }
                node = node.parentModelNode()
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

            val rcontext = node.htmlRendererContext
            if (rcontext != null) {
                if (!rcontext.onDoubleClick(node, event)) {
                    return false
                }
            }
        }
        val parent = node.parentModelNode()
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
        val parent = node.parentModelNode()
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

        }
        if (node is HTMLLinkElementImpl) {
            node.getCurrentStyle().overlayColor = ("#9090FF80")
            return false
        }
        if (!pass) {
            return false
        }
        val parent = node.parentModelNode()
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

        }
        if (node is HTMLLinkElementImpl) {
            node.getCurrentStyle().overlayColor = (null)
            return false
        }
        if (!pass) {
            return false
        }
        val parent = node.parentModelNode()
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
                val extraFormInputs: Array<FormInput> = arrayOf<FormInput>(
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
                    val cursorOpt = rs.getCursor()
                    if (rcontext != null) {
                        if (cursorOpt!!.isPresent) {
                            rcontext.setCursor(cursorOpt)
                            break
                        } else {
                            if (node.parentModelNode() === limit) {
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
                node = node.parentModelNode()
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
                    val cursorOpt = rs.getCursor()
                    foundCursorOpt = cursorOpt!!
                    if (cursorOpt.isPresent) {
                        break
                    }
                }
                node = node.parentModelNode()
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
