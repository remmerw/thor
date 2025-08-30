package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import org.mozilla.javascript.Function
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventException
import org.w3c.dom.events.EventListener
import java.util.IdentityHashMap
import java.util.LinkedList

class EventTargetManager(private val window: Window) {
    private val nodeOnEventListeners: MutableMap<NodeImpl?, MutableMap<String?, MutableList<EventListener?>?>?> =
        IdentityHashMap<NodeImpl?, MutableMap<String?, MutableList<EventListener?>?>?>()

    // private final Map<String, List<Function>> onEventHandlers = new HashMap<>();
    private val nodeOnEventFunctions: MutableMap<NodeImpl?, MutableMap<String?, MutableList<Function?>?>?> =
        IdentityHashMap<NodeImpl?, MutableMap<String?, MutableList<Function?>?>?>()

    fun addEventListener(
        node: NodeImpl?,
        type: String?,
        listener: EventListener?,
        useCapture: Boolean
    ) {
        val handlerList = getListenerList(type, node, true)
        handlerList!!.add(listener)
    }

    private fun getListenerList(
        type: String?,
        node: NodeImpl?,
        createIfNotExist: Boolean
    ): MutableList<EventListener?>? {
        val onEventListeners = getEventListeners(node, createIfNotExist)

        if (onEventListeners != null) {
            if (onEventListeners.containsKey(type)) {
                return onEventListeners.get(type)
            } else if (createIfNotExist) {
                val handlerList: MutableList<EventListener?> = ArrayList<EventListener?>()
                onEventListeners.put(type, handlerList)
                return handlerList
            } else {
                return null
            }
        } else {
            return null
        }
    }

    private fun getEventListeners(
        node: NodeImpl?,
        createIfNotExist: Boolean
    ): MutableMap<String?, MutableList<EventListener?>?>? {
        if (nodeOnEventListeners.containsKey(node)) {
            return nodeOnEventListeners.get(node)
        } else {
            if (createIfNotExist) {
                val onEventListeners: MutableMap<String?, MutableList<EventListener?>?> =
                    HashMap<String?, MutableList<EventListener?>?>()
                nodeOnEventListeners.put(node, onEventListeners)
                return onEventListeners
            } else {
                return null
            }
        }
    }

    fun removeEventListener(
        node: NodeImpl?,
        type: String?,
        listener: EventListener?,
        useCapture: Boolean
    ) {
        val onEventListeners = getEventListeners(node, false)
        if (onEventListeners != null) {
            if (onEventListeners.containsKey(type)) {
                onEventListeners.get(type)!!.remove(listener)
            }
        }
    }

    private fun getFunctionList(
        type: String?,
        node: NodeImpl?,
        createIfNotExist: Boolean
    ): MutableList<Function?>? {
        val onEventListeners = getEventFunctions(node, createIfNotExist)

        if (onEventListeners != null) {
            if (onEventListeners.containsKey(type)) {
                return onEventListeners.get(type)
            } else if (createIfNotExist) {
                val handlerList: MutableList<Function?> = ArrayList<Function?>()
                onEventListeners.put(type, handlerList)
                return handlerList
            } else {
                return null
            }
        } else {
            return null
        }
    }

    private fun getEventFunctions(
        node: NodeImpl?,
        createIfNotExist: Boolean
    ): MutableMap<String?, MutableList<Function?>?>? {
        if (nodeOnEventFunctions.containsKey(node)) {
            return nodeOnEventFunctions.get(node)
        } else {
            if (createIfNotExist) {
                val onEventListeners: MutableMap<String?, MutableList<Function?>?> =
                    HashMap<String?, MutableList<Function?>?>()
                nodeOnEventFunctions.put(node, onEventListeners)
                return onEventListeners
            } else {
                return null
            }
        }
    }

    @Throws(EventException::class)
    fun dispatchEvent(node: NodeImpl?, evt: Event): Boolean {

        return false
    }

    // private void dispatchEventToHandlers(final NodeImpl node, final Event event, final List<EventListener> handlers) {
    private fun dispatchEventToHandlers(
        node: NodeImpl?,
        event: io.github.remmerw.thor.cobra.html.js.Event
    ) {
        val handlers = getListenerList(event.type, node, false)
        if (handlers != null) {
            // We clone the collection and check if original collection still contains
            // the handler before dispatching
            // This is to avoid ConcurrentModificationException during dispatch
            val handlersCopy = _root_ide_package_.java.util.ArrayList<EventListener>(handlers)
            for (h in handlersCopy) {
                // TODO: Not sure if we should stop calling handlers after propagation is stopped
                // if (event.isPropagationStopped()) {
                // return;
                // }

                if (handlers.contains(h)) {
                    // window.addJSTask(new JSRunnableTask(0, "Event dispatch for: " + event, new Runnable(){
                    // public void run() {
                    h.handleEvent(event)

                    // }
                    // }));
                    // h.handleEvent(event);

                    // Executor.executeFunction(node, h, event);
                }
            }
        }
    }

    // protected void dispatchEventToJSHandlers(final NodeImpl node, final Event event, final List<Function> handlers) {
    private fun dispatchEventToJSHandlers(
        node: NodeImpl?,
        event: io.github.remmerw.thor.cobra.html.js.Event
    ) {
        val handlers = getFunctionList(event.type, node, false)
        if (handlers != null) {
            // We clone the collection and check if original collection still contains
            // the handler before dispatching
            // This is to avoid ConcurrentModificationException during dispatch
            val handlersCopy = ArrayList<Function?>(handlers)
            for (h in handlersCopy) {
                // TODO: Not sure if we should stop calling handlers after propagation is stopped
                // if (event.isPropagationStopped()) {
                // return;
                // }

                if (handlers.contains(h)) {
                    // window.addJSTask(new JSRunnableTask(0, "Event dispatch for " + event, new Runnable(){
                    // public void run() {
                    //Executor.executeFunction(node, h, event, window.contextFactory)
                    // }
                    // }));
                    // Executor.executeFunction(node, h, event);
                }
            }
        }
    }

    @JvmOverloads
    fun addEventListener(
        node: NodeImpl?,
        type: String?,
        listener: Function?,
        useCapture: Boolean = false
    ) {
        // TODO
        // System.out.println("node by name: " + node.getNodeName() + " adding Event listener of type: " + type);

        /*
    List<Function> handlerList = null;
    if (onEventHandlers.containsKey(type)) {
      handlerList = onEventHandlers.get(type);
    } else {
      handlerList = new ArrayList<>();
      onEventHandlers.put(type, handlerList);
    }*/
        // final Map<String, List<Function>> handlerList = getEventFunctions(node, true);

        val handlerList = getFunctionList(type, node, true)
        handlerList!!.add(listener)
    }

    fun removeEventListener(
        node: NodeImpl?,
        type: String?,
        listener: Function?,
        useCapture: Boolean
    ) {
        val onEventListeners = getEventFunctions(node, false)
        if (onEventListeners != null) {
            if (onEventListeners.containsKey(type)) {
                onEventListeners.get(type)!!.remove(listener)
            }
        }
    }

    fun reset() {
        nodeOnEventFunctions.clear()
        nodeOnEventListeners.clear()
    }

    companion object {
        private fun getPropagationPath(node: NodeImpl?): MutableList<NodeImpl?> {
            var node = node
            val nodes: MutableList<NodeImpl?> = LinkedList<NodeImpl?>()
            while (node != null) {
                if ((node is Element) || (node is Document)) { //  TODO || node instanceof Window) {
                    nodes.add(node)
                }
                node = node.getParentNode() as NodeImpl?
            }

            // TODO
            // nodes.add(window);
            return nodes
        }
    }
}
