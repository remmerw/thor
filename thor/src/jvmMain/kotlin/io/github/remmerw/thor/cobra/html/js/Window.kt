/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net
 */
/*
 * Created on Nov 12, 2005
 */
package io.github.remmerw.thor.cobra.html.js

import io.github.remmerw.thor.cobra.html.HtmlRendererContext
import io.github.remmerw.thor.cobra.html.domimpl.CanvasPath2D
import io.github.remmerw.thor.cobra.html.domimpl.CommentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLDivElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLDocumentImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLIFrameElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLImageElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLOptionElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLScriptElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.HTMLSelectElementImpl
import io.github.remmerw.thor.cobra.html.domimpl.NodeImpl
import io.github.remmerw.thor.cobra.html.domimpl.TextImpl
import io.github.remmerw.thor.cobra.js.AbstractScriptableDelegate
import io.github.remmerw.thor.cobra.js.HideFromJS
import io.github.remmerw.thor.cobra.js.JavaClassWrapper
import io.github.remmerw.thor.cobra.js.JavaClassWrapperFactory
import io.github.remmerw.thor.cobra.js.JavaInstantiator
import io.github.remmerw.thor.cobra.js.JavaObjectWrapper
import io.github.remmerw.thor.cobra.js.JavaScript
import io.github.remmerw.thor.cobra.ua.UserAgentContext
import io.github.remmerw.thor.cobra.ua.UserAgentContext.RequestKind
import io.github.remmerw.thor.cobra.util.ID
import org.mozilla.javascript.ClassShutter
import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.ScriptRuntime
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.CSS2Properties
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventException
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLCollection
import org.w3c.dom.html.HTMLElement
import org.w3c.dom.views.AbstractView
import org.w3c.dom.views.DocumentView
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.lang.ref.WeakReference
import java.net.MalformedURLException
import java.net.URL
import java.util.WeakHashMap
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.Timer
import kotlin.concurrent.Volatile

class Window // TODO: Probably need to create a new Window instance
// for every document. Sharing of Window state between
// different documents is not correct.
    (val htmlRendererContext: HtmlRendererContext?, val userAgentContext: UserAgentContext) :
    AbstractScriptableDelegate(), AbstractView, EventTarget {
    private val windowContextFactory = MyContextFactory()
    val eventTargetManager: EventTargetManager = EventTargetManager(this)

    // TODO: Move job scheduling logic into Window class
    private val jobsOver = AtomicBoolean(false)
    var navigator: Navigator? = null
        get() {
            synchronized(this) {
                var nav = field
                if (nav == null) {
                    nav = Navigator(this.userAgentContext)
                    field = nav
                }
                return nav
            }
        }
        private set

    // private volatile HTMLDocumentImpl document;
    var screen: Screen? = null
        get() {
            synchronized(this) {
                var nav = field
                if (nav == null) {
                    nav = Screen()
                    field = nav
                }
                return nav
            }
        }
        private set
    var location: Location? = null
        get() {
            synchronized(this) {
                var loc = field
                if (loc == null) {
                    loc = Location(this)
                    field = loc
                }
                return loc
            }
        }
        private set
    private var taskMap: MutableMap<Int?, TaskWrapper?>? = null

    // private Timer getTask(Long timeoutID) {
    // synchronized(this) {
    // Map taskMap = this.taskMap;
    // if(taskMap != null) {
    // return (Timer) taskMap.get(timeoutID);
    // }
    // }
    // return null;
    // }
    @Volatile
    var documentNode: Document? = null
        private set

    @Volatile
    private var jsScheduler: JSScheduler? = JSScheduler(this)
    private var windowScope: Scriptable? = null
    var length: Int = 0
        /**
         * Gets the number of frames.
         */
        get() {
            if (this.lengthSet) {
                return field
            } else {
                val frames = this.frames
                return if (frames == null) 0 else frames.length
            }
        }
        set(length) {
            this.lengthSet = true
            field = length
        }
    private var lengthSet = false
    var history: History? = null
        get() {
            synchronized(this) {
                var hist = field
                if (hist == null) {
                    hist = History(this)
                    field = hist
                }
                return hist
            }
        }
        private set
    var onunload: org.mozilla.javascript.Function? = null
    private var onWindowLoadHandler: org.mozilla.javascript.Function? = null

    private fun clearState() {
        synchronized(this) {
            // windowClosing = true;
            if (this.documentNode is HTMLDocumentImpl) {
                (this.documentNode as HTMLDocumentImpl).stopEverything()
            }
            jsScheduler!!.stopAndWindUp(true)
            jsScheduler = JSScheduler(this)
            eventTargetManager.reset()
            this.onWindowLoadHandler = null

            this.forgetAllTasks()

            // Commenting out call to getWindowScope() since that creates a new scope which is wasteful
            // if we are going to destroy it anyway.
            // final Scriptable s = this.getWindowScope();
            val s = this.windowScope
            if (s != null) {
                val ids = s.ids
                for (id in ids) {
                    if (id is String) {
                        s.delete(id)
                    } else if (id is Int) {
                        s.delete(id)
                    }
                }
            }

            // This will ensure that a fresh scope will be created by getWindowScope() on the next call
            this.windowScope = null
        }
    }

    override fun getDocument(): DocumentView? {
        return this.documentNode as DocumentView?
    }

    @HideFromJS
    fun setDocument(document: Document) {
        synchronized(this) {
            val prevDocument = this.documentNode
            if (prevDocument !== document) {
                val onunload = this.onunload
                if (onunload != null) {
                    val oldDoc = prevDocument as HTMLDocumentImpl
                    Executor.executeFunction(
                        this.getWindowScope(),
                        onunload,
                        oldDoc.getDocumentURL(),
                        this.userAgentContext,
                        windowContextFactory
                    )
                    this.onunload = null
                }

                // TODO: Should clearing of the state be done when window "unloads"?
                if (prevDocument != null) {
                    // Only clearing when the previous document was not null
                    // because state might have been set on the window before
                    // the very first document is added.
                    this.clearState()
                }
                // this.forgetAllTasks();
                this.initWindowScope(document)

                jobsOver.set(false)
                jsScheduler!!.start()

                this.documentNode = document
                // eventTargetManager.setNode(document);
            }
        }
    }

    private val currURL: URL?
        get() {
            try {
                return URL(htmlRendererContext?.currentURL)
            } catch (e: MalformedURLException) {
                return null
            }
        }

    @HideFromJS
    fun addJSTask(task: JSTask) {
        /*
      final URL urlContext = new URL(rcontext.getCurrentURL());
      if (document != null) {
        final URL urlDoc = document.getDocumentURL();
        if (!urlDoc.equals(urlContext)) {
          throw new RuntimeException(String.format("doc url(%s) is different from context url (%s)", urlDoc, urlContext));
        }
      }*/
        val urlContext = this.currURL
        if (urlContext != null) {
            if (userAgentContext.isRequestPermitted(
                    UserAgentContext.Request(
                        urlContext,
                        RequestKind.JavaScript
                    )
                )
            ) {
                // System.out.println("Adding task: " + task);
                synchronized(this) {
                    jsScheduler!!.addJSTask(task)
                }
            }
        } else {
            // TODO: This happens when the URL is not accepted by okhttp
            println("Not adding task because url context is null")
        }
    }

    // TODO: Also look at GH #149
    // TODO: Try to refactor this so that all tasks are checked here rather than in caller
    // TODO: Some tasks are added unchecked for various reasons that need to be reviewed:
    //       1. Timer task. The logic is that a script that was permitted to create the timer already has the permission to execute it.
    //          But it would be better if their permission is checked again to account for subsequent changes through RequestManager,
    //          or if RequestManager assures that page is reloaded for *any* permission change.
    //       2. Event listeners. Logic is similar to Timer task
    //       3. Script elements. They are doing the checks themselves, but it would better to move the check here.
    //       4. XHR handler. Logic similar to timer task.
    @HideFromJS
    fun addJSTaskUnchecked(task: JSTask) {
        // System.out.println("Adding task: " + task);
        synchronized(this) {
            jsScheduler!!.addJSTask(task)
        }
    }

    @HideFromJS
    fun addJSUniqueTask(oldId: Int, task: JSTask): Int {
        println("Adding unique task: " + task)

        synchronized(this) {
            return jsScheduler!!.addUniqueJSTask(oldId, task)
        }
    }

    private fun putAndStartTask(timeoutID: Int?, timer: Timer, retained: Any?) {
        var oldTaskWrapper: TaskWrapper? = null
        synchronized(this) {
            var taskMap = this.taskMap
            if (taskMap == null) {
                taskMap = HashMap<Int?, TaskWrapper?>(4)
                this.taskMap = taskMap
            } else {
                oldTaskWrapper = taskMap.get(timeoutID)
            }
            taskMap.put(timeoutID, TaskWrapper(timer, retained))
        }
        // Do this outside synchronized block, just in case.
        if (oldTaskWrapper != null) {
            oldTaskWrapper.timer.stop()
        }
        timer.start()
    }

    private fun forgetTask(timeoutID: Int?, cancel: Boolean) {
        var oldTimer: TaskWrapper? = null
        synchronized(this) {
            val taskMap = this.taskMap
            if (taskMap != null) {
                oldTimer = taskMap.remove(timeoutID)
            }
        }
        if ((oldTimer != null) && cancel) {
            oldTimer.timer.stop()
        }
    }

    private fun forgetAllTasks() {
        var oldTaskWrappers: Array<TaskWrapper>? = null
        synchronized(this) {
            val taskMap = this.taskMap
            if (taskMap != null) {
                oldTaskWrappers = taskMap.values.toTypedArray() as Array<TaskWrapper>?
                this.taskMap = null
            }
        }
        if (oldTaskWrappers != null) {
            for (taskWrapper in oldTaskWrappers) {
                taskWrapper.timer.stop()
            }
        }
    }

    /**
     * @param aFunction Javascript function to invoke on each loop.
     * @param aTimeInMs Time in millisecund between each loop. TODO: Can this be converted
     * to long type?
     * @return Return the timer ID to use as reference
     * @todo Make proper and refactore with
     * [Window.setTimeout].
     * @see [Window.setInterval
     * interface definition](http://developer.mozilla.org/en/docs/DOM:window.setInterval)
     */
    fun setInterval(aFunction: org.mozilla.javascript.Function?, aTimeInMs: Double): Int {
        require(!((aTimeInMs > Int.Companion.MAX_VALUE) || (aTimeInMs < 0))) { "Timeout value " + aTimeInMs + " is not supported." }
        val timeID: Int = generateTimerID()
        println("Created interval timer: " + timeID)
        val timeIDInt = timeID
        val task: ActionListener = FunctionTimerTask(this, timeIDInt, aFunction, false)
        var t = aTimeInMs.toInt()
        if (t < 1) {
            t = 1
        }
        val timer = Timer(t, task)
        timer.isRepeats = true // The only difference with setTimeout
        this.putAndStartTask(timeIDInt, timer, aFunction)
        return timeID
    }

    /**
     * @param aExpression Javascript expression to invoke on each loop.
     * @param aTimeInMs   Time in millisecund between each loop.
     * @return Return the timer ID to use as reference
     * @todo Make proper and refactore with
     * [Window.setTimeout].
     * @see [Window.setInterval
     * interface definition](http://developer.mozilla.org/en/docs/DOM:window.setInterval)
     */
    fun setInterval(aExpression: String?, aTimeInMs: Double): Int {
        require(!((aTimeInMs > Int.Companion.MAX_VALUE) || (aTimeInMs < 0))) { "Timeout value " + aTimeInMs + " is not supported." }
        val timeID: Int = generateTimerID()
        val timeIDInt = (timeID)
        val task: ActionListener = ExpressionTimerTask(this, timeIDInt, aExpression, false)
        var t = aTimeInMs.toInt()
        if (t < 1) {
            t = 1
        }
        val timer = Timer(t, task)
        timer.isRepeats = false // The only difference with setTimeout
        this.putAndStartTask(timeIDInt, timer, null)
        return timeID
    }

    /**
     * @param aTimerID Timer ID to stop.
     * @see [Window.clearInterval
     * interface Definition](http://developer.mozilla.org/en/docs/DOM:window.clearInterval)
     */
    fun clearInterval(aTimerID: Int) {
        val key = (aTimerID)
        this.forgetTask(key, true)
    }

    fun clearInterval(unused: Any?) {
        // Happens when jQuery calls this with a null parameter;
        // TODO: Check if there are other cases
        if (unused is Int) {
            clearInterval(unused)
            return
        }
        println("Clear interval : ignoring " + unused)
        // TODO: Should this be throwing an exception?
        // throw new UnsupportedOperationException();
    }

    /*
  private Object evalInScope(final String javascript) {
    final Context ctx = Executor.createContext(document.getDocumentURL(), this.uaContext);
    try {
      final String scriptURI = "window.eval";
      return ctx.evaluateString(getWindowScope(), javascript, scriptURI, 1, null);
    } finally {
      Context.exit();
    }
  }

  / * Removing because this eval method interferes with the default eval() method.
   * The context of the JS eval() call is not preserved by this method.
  public Object eval(final String javascript) {
    final HTMLDocumentImpl document = (HTMLDocumentImpl) this.document;
    if (document == null) {
      throw new IllegalStateException("Cannot evaluate if document is not set.");
    }
    final Context ctx = Executor.createContext(document.getDocumentURL(), this.uaContext);
    try {
      final Scriptable scope = this.getWindowScope();
      if (scope == null) {
        throw new IllegalStateException("Scriptable (scope) instance was expected to be keyed as UserData to document using "
            + Executor.SCOPE_KEY);
      }
      final String scriptURI = "window.eval";
      if (logger.isLoggable(Level.INFO)) {
        logger.info("eval(): javascript follows...\r\n" + javascript);
      }
      return ctx.evaluateString(scope, javascript, scriptURI, 1, null);
    } finally {
      Context.exit();
    }
  }
   */
    fun alert(message: String?) {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.alert(message)
        }
    }

    fun back() {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.back()
        }
    }

    fun blur() {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.blur()
        }
    }

    fun clearTimeout(someObj: Any?) {
        if (someObj is Int) {
            clearTimeout(someObj)
        } else {
            println("Window.clearTimeout() : Ignoring: " + someObj)
        }
    }

    private fun clearTimeout(timeoutID: Int) {
        println("Clearing timeout: " + timeoutID)
        val key = (timeoutID)
        this.forgetTask(key, true)
    }

    fun close() {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.close()
        }
    }

    fun confirm(message: String?): Boolean {
        val rc = this.htmlRendererContext
        if (rc != null) {
            return rc.confirm(message)
        } else {
            return false
        }
    }

    // Making public for link element
    @HideFromJS
    fun evalInScope(javascript: String?) {
        addJSTask(JSRunnableTask(0, object : Runnable {
            override fun run() {
                try {
                    "window.eval"
                    /* TODO
                    val ctx = Executor.createContext(
                        this.currURL, this@Window.userAgentContext, windowContextFactory
                    )
                    ctx.evaluateString(getWindowScope(), javascript, scriptURI, 1, null)
                    */
                } finally {
                    Context.exit()
                }
            }
        }))
    }

    fun focus() {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.focus()
        }
    }

    @get:HideFromJS
    val contextFactory: ContextFactory
        get() = windowContextFactory

    private fun initWindowScope(doc: Document) {
        // Special Javascript class: XMLHttpRequest
        val ws = this.getWindowScope()
        val xi: JavaInstantiator = object : JavaInstantiator {
            override fun newInstance(args: Array<Any>): Any {
                val d = doc
                checkNotNull(d) { "Cannot perform operation when document is unset." }
                val hd: HTMLDocumentImpl?
                try {
                    hd = d as HTMLDocumentImpl
                } catch (err: ClassCastException) {
                    throw IllegalStateException("Cannot perform operation with documents of type " + d.javaClass.name + ".")
                }
                return XMLHttpRequest(userAgentContext, hd.documentURL!!, ws, this@Window)
            }
        }
        defineInstantiator(ws, "XMLHttpRequest", XMLHTTPREQUEST_WRAPPER!!, xi)

        val pi: JavaInstantiator = object : JavaInstantiator {
            override fun newInstance(args: Array<Any>): Any {
                return CanvasPath2D()
            }
        }
        defineInstantiator(ws, "Path2D", PATH2D_WRAPPER!!, pi)

        val ei: JavaInstantiator = object : JavaInstantiator {
            override fun newInstance(args: Array<Any>): Any {
                if (args.size > 0) {
                    return Event(args[0].toString(), doc)
                }
                throw ScriptRuntime.constructError("TypeError", "An event name must be provided")
            }
        }
        defineInstantiator(ws, "Event", EVENT_WRAPPER!!, ei)

        // We can use a single shared instance since it is dummy for now
        ScriptableObject.putProperty(ws, "localStorage", STORAGE)
        ScriptableObject.putProperty(ws, "sessionStorage", STORAGE)

        // ScriptableObject.defineClass(ws, org.mozilla.javascript.ast.Comment.class);
        defineElementClass(ws, doc, "Comment", "comment", CommentImpl::class.java)

        // HTML element classes
        defineElementClass(ws, doc, "Image", "img", HTMLImageElementImpl::class.java)
        defineElementClass(ws, doc, "Script", "script", HTMLScriptElementImpl::class.java)
        defineElementClass(ws, doc, "IFrame", "iframe", HTMLIFrameElementImpl::class.java)
        defineElementClass(ws, doc, "Option", "option", HTMLOptionElementImpl::class.java)
        defineElementClass(ws, doc, "Select", "select", HTMLSelectElementImpl::class.java)

        // TODO: Add all similar elements
        defineElementClass(ws, doc, "HTMLDivElement", "div", HTMLDivElementImpl::class.java)

        defineInstantiator(
            ws, "Text", JavaClassWrapperFactory.instance!!.getClassWrapper(
                TextImpl::class.java
            ), object : JavaInstantiator {
                override fun newInstance(args: Array<Any>): Any {
                    val data: String? =
                        if (args.size > 0 && args[0] != null) args[0].toString() else ""
                    return documentNode!!.createTextNode(data)
                }
            })
    }

    @HideFromJS
    fun getWindowScope(): Scriptable {
        synchronized(this) {
            var ws = this.windowScope
            if (ws != null) {
                return ws
            }
            // Context.enter() OK in this particular case.
            // final Context ctx = Context.enter();
            val ctx = windowContextFactory.enterContext()
            try {
                // Window scope needs to be top-most scope.
                ws = JavaScript.instance.getJavascriptObject(this, null) as Scriptable?
                ws = ctx.initSafeStandardObjects(ws as ScriptableObject?)
                val consoleJSObj = JavaScript.instance.getJavascriptObject(Console(), ws)
                ScriptableObject.putProperty(ws, "console", consoleJSObj)
                this.windowScope = ws
                return ws
            } finally {
                Context.exit()
            }
        }
    }

    fun open(
        relativeUrl: String,
        windowName: String?,
        windowFeatures: String?,
        replace: Boolean
    ): Window? {
        val rc = this.htmlRendererContext
        if (rc != null) {
            val url: URL?
            val doc = this.documentNode
            try {
                if (doc is HTMLDocumentImpl) {
                    url = doc.getFullURL(relativeUrl)
                } else {
                    url = URL(relativeUrl)
                }
            } catch (mfu: MalformedURLException) {
                throw IllegalArgumentException("Malformed URI: " + relativeUrl)
            }
            if (replace) {
                this.documentNode = null
                rc.navigate(url, null)
                return this
            } else {
                val newContext = rc.open(url, windowName, windowFeatures, replace)
                return getWindow(newContext)
            }
        } else {
            return null
        }
    }

    @JvmOverloads
    fun open(url: String, windowName: String? = "window:" + ID.generateLong()): Window? {
        return this.open(url, windowName, "", false)
    }

    fun open(url: String, windowName: String?, windowFeatures: String?): Window? {
        return this.open(url, windowName, windowFeatures, false)
    }

    fun prompt(message: String?): String? {
        return this.prompt(message, "")
    }

    fun prompt(message: String?, inputDefault: Int): String? {
        return this.prompt(message, inputDefault.toString())
    }

    fun prompt(message: String?, inputDefault: String?): String? {
        val rcontext = this.htmlRendererContext
        if (rcontext != null) {
            return rcontext.prompt(message, inputDefault)
        } else {
            return null
        }
    }

    fun scrollTo(x: Int, y: Int) {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.scroll(x, y)
        }
    }

    fun scrollBy(x: Int, y: Int) {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.scrollBy(x, y)
        }
    }

    fun resizeTo(width: Int, height: Int) {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.resizeTo(width, height)
        }
    }

    fun resizeBy(byWidth: Int, byHeight: Int) {
        val rc = this.htmlRendererContext
        if (rc != null) {
            rc.resizeBy(byWidth, byHeight)
        }
    }

    @NotGetterSetter
    fun setTimeout(expr: String?, millis: Double): Int {
        require(!((millis > Int.Companion.MAX_VALUE) || (millis < 0))) { "Timeout value " + millis + " is not supported." }
        val timeID: Int = generateTimerID()
        val timeIDInt = (timeID)
        val task: ActionListener = ExpressionTimerTask(this, timeIDInt, expr, true)
        var t = millis.toInt()
        if (t < 1) {
            t = 1
        }
        val timer = Timer(t, task)
        timer.isRepeats = false
        this.putAndStartTask(timeIDInt, timer, null)
        return timeID
    }

    @NotGetterSetter
    fun setTimeout(function: org.mozilla.javascript.Function?, millis: Double): Int {
        require(!((millis > Int.Companion.MAX_VALUE) || (millis < 0))) { "Timeout value " + millis + " is not supported." }
        val timeID: Int = generateTimerID()
        println("Creating timer with id: " + timeID + " in " + documentNode!!.baseURI)
        val timeIDInt = (timeID)
        val task: ActionListener = FunctionTimerTask(this, timeIDInt, function, true)
        var t = millis.toInt()
        if (t < 1) {
            t = 1
        }
        val timer = Timer(t, task)
        timer.isRepeats = false
        this.putAndStartTask(timeIDInt, timer, function)
        return timeID
    }

    @NotGetterSetter
    fun setTimeout(function: org.mozilla.javascript.Function?): Int {
        return setTimeout(function, 0.0)
    }

    @NotGetterSetter
    fun setTimeout(expr: String?): Int {
        return setTimeout(expr, 0.0)
    }

    val isClosed: Boolean
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                return rc.isClosed
            } else {
                return false
            }
        }

    val defaultStatus: String?
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                return rc.defaultStatus
            } else {
                return null
            }
        }

    val frames: HTMLCollection?
        get() {
            val doc = this.documentNode
            if (doc is HTMLDocumentImpl) {
                return doc.frames
            }
            return null
        }

    var name: String?
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                return rc.name
            } else {
                return null
            }
        }
        set(newName) {
            // TODO
            println("TODO: window.setName")
        }

    val parent: Window?
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                val rcontextParent = rc.parent
                if (rcontextParent == null) {
                    return this
                } else {
                    return getWindow(
                        rcontextParent
                    )
                }
            } else {
                return null
            }
        }

    var opener: Window?
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                return getWindow(rc.opener)
            } else {
                return null
            }
        }
        set(opener) {
            val rc = this.htmlRendererContext
            if (rc != null) {
                if (opener == null) {
                    rc.opener = null
                } else {
                    rc.opener = opener.htmlRendererContext
                }
            }
        }

    val self: Window
        get() = this

    var status: String?
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                return rc.status
            } else {
                return null
            }
        }
        set(message) {
            val rc = this.htmlRendererContext
            if (rc != null) {
                rc.status = message
            }
        }

    val top: Window?
        get() {
            val rc = this.htmlRendererContext
            if (rc != null) {
                return getWindow(rc.top)
            } else {
                return null
            }
        }

    val window: Window
        get() = this

    fun setLocation(location: String?) {
        this.location!!.href = (location)
    }

    fun getComputedStyle(element: HTMLElement?, pseudoElement: String?): CSS2Properties {
        if (element is HTMLElementImpl) {
            return element.getComputedStyle(pseudoElement)
        } else {
            throw IllegalArgumentException("Element implementation unknown: " + element)
        }
    }

    /*
    var onload: Function?
        get() {
            val doc = this.documentNode
            if (doc is HTMLDocumentImpl) {
                return doc.onloadHandler
            } else {
                return null
            }
        }
        set(onload) {
            // Note that body.onload overrides
            // window.onload.
            /*
            final Document doc = this.document;
            if (doc instanceof HTMLDocumentImpl) {
              ((HTMLDocumentImpl) doc).setWindowOnloadHandler(onload);
            }*/
            onWindowLoadHandler = onload
        }*/

    fun namedItem(name: String?): Node? {
        // Bug 1928758: Element IDs are named objects in context.
        val doc = this.documentNode
        if (doc == null) {
            return null
        }
        val node: Node? = doc.getElementById(name)
        return node
    }

    @JvmOverloads
    fun addEventListener(
        type: String?,
        listener: org.mozilla.javascript.Function?,
        useCapture: Boolean = false
    ) {
        if (useCapture) {
            throw UnsupportedOperationException()
        }
        /*
    // TODO: Should this delegate completely to document
    if ("load".equals(type)) {
      document.addLoadHandler(listener);
    } else {
      document.addEventListener(type, listener);
    }*/
        println("window Added listener for: " + type)
        eventTargetManager.addEventListener(this.documentNode as NodeImpl?, type, listener)
    }

    fun removeEventListener(
        type: String?,
        listener: org.mozilla.javascript.Function?,
        useCapture: Boolean
    ) {
        // TODO: Should this delegate completely to document
        if ("load" == type) {
            (this.documentNode as HTMLDocumentImpl).removeLoadHandler(listener)
        }
        eventTargetManager.removeEventListener(
            this.documentNode as NodeImpl?,
            type,
            listener,
            useCapture
        )
    }

    @Throws(EventException::class)
    override fun dispatchEvent(evt: Event): Boolean {
        // TODO
        println("TODO: window dispatch event")
        eventTargetManager.dispatchEvent(this.documentNode as NodeImpl?, evt)
        return false
    }

    // TODO: Hide from JS
    fun domContentLoaded(domContentLoadedEvent: Event) {
        eventTargetManager.dispatchEvent(this.documentNode as NodeImpl?, domContentLoadedEvent)
    }

    @HideFromJS
    fun jobsFinished() {
        val windowLoadEvent = Event(
            "load",
            this.documentNode
        )
        eventTargetManager.dispatchEvent(this.documentNode as NodeImpl?, windowLoadEvent)

        val handler = this.onWindowLoadHandler
        if (handler != null) {
            addJSTask(JSRunnableTask(0, object : Runnable {
                override fun run() {
                    Executor.executeFunction(
                        documentNode as NodeImpl,
                        handler,
                        windowLoadEvent,
                        windowContextFactory
                    )
                }
            }))
            // Executor.executeFunction(document, handler, windowLoadEvent);
        }

        jobsOver.set(true)
    }

    @get:PropertyName("Element")
    val element: Class<Element>
        get() = Element::class.java

    @get:PropertyName("Node")
    val node: Class<Node>
        get() = Node::class.java

    fun addEventListener(type: String?, listener: EventListener?) {
        addEventListener(type, listener, false)
    }

    override fun addEventListener(type: String?, listener: EventListener?, useCapture: Boolean) {
        if (useCapture) {
            throw UnsupportedOperationException()
        }
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException();
        eventTargetManager.addEventListener(
            this.documentNode as NodeImpl?,
            type,
            listener,
            useCapture
        )
    }

    override fun removeEventListener(type: String?, listener: EventListener?, useCapture: Boolean) {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException()
    }

    @Throws(EventException::class)
    override fun dispatchEvent(evt: Event?): Boolean {
        // TODO Auto-generated method stub
        throw UnsupportedOperationException()
    }

    private fun shutdown() {
        // TODO: Add the sync below, when/if the scheduleLock is added
        // synchronized (scheduleLock) {
        forgetAllTasks()

        if (jsScheduler != null) {
            jsScheduler!!.stopAndWindUp(false)
            jsScheduler = null
        }
        // }
    }


    @HideFromJS
    fun hasPendingTasks(): Boolean {
        return (!jobsOver.get()) || jsScheduler!!.hasPendingTasks()
    }

    // private Function windowLoadListeners;
    abstract class JSTask(protected val priority: Int, protected val description: String?) :
        Comparable<JSTask> {
        protected val creationTime: Long


        // TODO: Add a context parameter that will be combined with current context, to help with creation of timer tasks
        // public JSTask(final int priority, final Runnable runnable) {
        init {
            this.creationTime = System.nanoTime()
        }

        // TODO: Add a way to stop a task. It should return false if the task can't be stopped in which case a thread kill will be performed by the task scheduler.
        // TODO: Sorting by priority
        override fun compareTo(other: JSTask): Int {
            val diffCreation = (other.creationTime - creationTime)
            if (diffCreation < 0) {
                return 1
            } else if (diffCreation == 0L) {
                return 0
            } else {
                return -1
            }
        }

        abstract fun run()
    }

    class JSRunnableTask(priority: Int, description: String?, private val runnable: Runnable) :
        JSTask(priority, description) {
        constructor(priority: Int, runnable: Runnable) : this(priority, "", runnable)

        override fun toString(): String {
            // return "JSRunnableTask [priority=" + priority + ", runnable=" + runnable + ", creationTime=" + creationTime + "]";
            return "JSRunnableTask [priority=" + priority + ", description=" + description + ", creationTime=" + creationTime + "]"
        }

        override fun run() {
            runnable.run()
        }
    }

    class JSSupplierTask<T>(
        priority: Int,
        private val supplier: Supplier<T?>,
        private val consumer: Consumer<T?>
    ) : JSTask(priority, "supplier description TODO") {
        override fun run() {
            val result = supplier.get()
            consumer.accept(result)
        }
    }

    private class JSScheduler(window: Window) : Thread("JS Scheduler") {
        private val jsQueue: PriorityBlockingQueue<ScheduledTask> =
            PriorityBlockingQueue<ScheduledTask>()
        private val running = AtomicBoolean(false)

        // TODO: This is not water tight for one reason, Windows are reused for different documents.
        // If they are always freshly created, the taskIdCounter will be more reliable.
        private val taskIdCounter = AtomicInteger(0)
        private val name: String

        @Volatile
        var isWindowClosing: Boolean = false
            private set

        init {
            this.name =
                "JS Sched " + (if (window.documentNode == null) "" else window.documentNode!!.baseURI)
        }

        override fun run() {
            while (!this.isWindowClosing) {
                try {
                    val scheduledTask: ScheduledTask?
                    // TODO: uncomment if synchronization is necessary with the add methods
                    // synchronized (this) {
                    scheduledTask =
                        jsQueue.poll(JS_SCHED_POLL_INTERVAL_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                    if (scheduledTask != null) {
                        // System.out.println("In " + window.document.getBaseURI() + "\n  Running task: " + scheduledTask);
                        // System.out.println("In " + name + "\n  Running task: " + scheduledTask);

                        running.set(true)
                        scheduledTask.task.run()

                        // System.out.println("Done task: " + scheduledTask);
                        // System.out.println("  Remaining tasks: " + jsQueue.size());
                    }
                } catch (e: InterruptedException) {
                    val queueSize = jsQueue.size
                    if (queueSize > 0) {
                        System.err.println("JS Scheduler was interrupted. Tasks remaining: " + jsQueue.size)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (wce: WindowClosingError) {
                    // Javascript context detected a request for closing and bailed out.
                    assert(this.isWindowClosing)
                } finally {
                    running.set(false)
                }
            }
            // System.out.println("Exiting loop\n\n");
        }

        fun stopAndWindUp(blocking: Boolean) {
            // System.out.println("Going to stop JS scheduler");
            this.isWindowClosing = true

            /* TODO: If the thread refuses to join(), perhaps the thread could be interrupted and stopped in
             * the catch block of join() below. This could be done immediately, or scheduled for a stopping
             * in a separate collector Thread
             * */
            // this.interrupt();
            if (blocking) {
                try {
                    this.join(JS_SCHED_JOIN_INTERVAL_MILLIS.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            /*
      this.stop();
      */
        }

        fun addJSTask(task: JSTask) {
            // synchronized (this) {
            jsQueue.add(ScheduledTask(0, task))
            // }
        }

        fun addUniqueJSTask(oldId: Int, task: JSTask): Int {
            // synchronized (this) {
            if (oldId != -1) {
                //if (jsQueue.contains(oldId)) {
                //  return oldId
                //}
                /*
        for (ScheduledTask t : jsQueue) {
          if (t.id == oldId) {
            // Task found
            return oldId;
          }
        }*/
            }
            val newId = taskIdCounter.addAndGet(1)
            jsQueue.add(ScheduledTask(newId, task))
            return newId
            // }
        }

        fun hasPendingTasks(): Boolean {
            return (!jsQueue.isEmpty()) || running.get()
        }

        private class ScheduledTask(val id: Int, val task: JSTask) : Comparable<ScheduledTask> {
            override fun compareTo(other: ScheduledTask): Int {
                return task.compareTo(other.task)
            }

            override fun equals(o: Any?): Boolean {
                if (o is Int) {
                    return o == id
                }
                return false
            }

            override fun toString(): String {
                return "Scheduled Task (" + id + ", " + task + ")"
            }
        }
    }

    internal object WindowClosingError : Error() {
        private const val serialVersionUID = 5375592396498284425L
    }

    class Console {
        fun log(obj: Any?) {
            println("> " + obj)
        }
    }

    private abstract class WeakWindowTask(window: Window?) : ActionListener {
        private val windowRef: WeakReference<Window?>

        init {
            this.windowRef = WeakReference<Window?>(window)
        }

        protected val window: Window?
            get() {
                val ref =
                    this.windowRef
                return if (ref == null) null else ref.get()
            }
    }

    private class FunctionTimerTask(
        window: Window?, // Implemented as a static WeakWindowTask to allow the Window
        // to get garbage collected, especially in infinite loop
        // scenarios.
        private val timeIDInt: Int?,
        function: org.mozilla.javascript.Function?,
        private val removeTask: Boolean
    ) : WeakWindowTask(window) {
        private val functionRef: WeakReference<org.mozilla.javascript.Function?>

        init {
            this.functionRef = WeakReference<org.mozilla.javascript.Function?>(function)
        }

        override fun actionPerformed(e: ActionEvent?) {
            println("Timer ID fired: " + timeIDInt + ", oneshot: " + removeTask)
            // This executes in the GUI thread and that's good.
            try {
                val window = this.window
                if (window == null) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("actionPerformed(): Window is no longer available.")
                    }
                    return
                }
                if (this.removeTask) {
                    window.forgetTask(this.timeIDInt, false)
                }
                // final HTMLDocumentImpl doc = (HTMLDocumentImpl) window.getDocument();
                checkNotNull(window.document) { "Cannot perform operation when document is unset." }
                val function = this.functionRef.get()
                checkNotNull(function) { "Cannot perform operation. Function is no longer available." }
                window.addJSTaskUnchecked(
                    JSRunnableTask(
                        0,
                        "timer task for id: " + timeIDInt + ", oneshot: " + removeTask,
                        Runnable {
                            Executor.executeFunction(
                                window.getWindowScope(), function,
                                window.currURL,
                                window.userAgentContext,
                                window.windowContextFactory
                            )
                        })
                )
                // Executor.executeFunction(window.getWindowScope(), function, doc.getDocumentURL(), window.getUserAgentContext(), window.windowFactory);
            } catch (err: Exception) {
                logger.log(Level.WARNING, "actionPerformed()", err)
            }
        }
    }

    private class ExpressionTimerTask(
        window: Window?, // Implemented as a static WeakWindowTask to allow the Window
        // to get garbage collected, especially in infinite loop
        // scenarios.
        private val timeIDInt: Int?,
        private val expression: String?,
        private val removeTask: Boolean
    ) : WeakWindowTask(window) {
        override fun actionPerformed(e: ActionEvent?) {
            // This executes in the GUI thread and that's good.
            try {
                val window = this.window
                if (window == null) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("actionPerformed(): Window is no longer available.")
                    }
                    return
                }
                if (this.removeTask) {
                    window.forgetTask(this.timeIDInt, false)
                }
                // final HTMLDocumentImpl doc = (HTMLDocumentImpl) window.getDocument();
                checkNotNull(window.document) { "Cannot perform operation when document is unset." }
                window.addJSTaskUnchecked(
                    JSRunnableTask(
                        0,
                        "timer task for id: " + timeIDInt,
                        Runnable {
                            window.evalInScope(this.expression)
                        })
                )
                // window.evalInScope(this.expression);
            } catch (err: Exception) {
                logger.log(Level.WARNING, "actionPerformed()", err)
            }
        }
    }

    private class TaskWrapper(
        val timer: Timer, // TODO: The retained object seems to be required to keep timer callback functions from being garbage collected.
        //       The FunctionTimerTask only keeps a weak reference. Need to review this design.
        @field:Suppress("unused") private val retained: Any?
    )

    private inner class MyContextFactory : ContextFactory() {
        private val myClassShutter: ClassShutter = object : ClassShutter {
            override fun visibleToScripts(fullClassName: String): Boolean {
                // System.out.println("class shutter Checking: " + fullClassName);
                if (fullClassName.startsWith("java")) {
                    val isException =
                        (fullClassName.startsWith("java.lang") && fullClassName.endsWith("Exception"))
                    if (fullClassName == "java.lang.Object" || isException) {
                        return true
                    }
                    println("Warning: Something tried to access java classes from javascript.")
                    Thread.dumpStack()
                    return false
                }

                // TODO: Change the default to false
                return true
            }
        }

        // Override {@link #makeContext()}
        override fun makeContext(): Context {
            val cx = super.makeContext()
            cx.setClassShutter(myClassShutter)
            // cx.setOptimizationLevel(9);
            cx.optimizationLevel = -1
            cx.setLanguageVersion(Context.VERSION_1_8)

            // Make Rhino runtime to call observeInstructionCount each 100_000 bytecode instructions
            cx.setInstructionObserverThreshold(100000)

            // cx.setMaximumInterpreterStackDepth(100);
            // cx.seal(null);
            return cx
        }

        override fun observeInstructionCount(cx: Context?, instructionCount: Int) {
            val jsSchedulerLocal = jsScheduler
            if (jsSchedulerLocal != null) {
                if (jsSchedulerLocal.isWindowClosing) {
                    throw java.lang.Exception()
                }
            }
        }

        override fun hasFeature(cx: Context?, featureIndex: Int): Boolean {
            if (featureIndex == Context.FEATURE_V8_EXTENSIONS) {
                return true
            }
            return super.hasFeature(cx, featureIndex)
        }
    }

    companion object {
        private val STORAGE = Storage()

        private val logger: Logger = Logger.getLogger(Window::class.java.name)
        private val CONTEXT_WINDOWS: MutableMap<HtmlRendererContext?, WeakReference<Window?>?> =
            WeakHashMap<HtmlRendererContext?, WeakReference<Window?>?>()

        // private static final JavaClassWrapper IMAGE_WRAPPER =
        // JavaClassWrapperFactory.getInstance().getClassWrapper(Image.class);
        private val XMLHTTPREQUEST_WRAPPER: JavaClassWrapper? =
            JavaClassWrapperFactory.instance!!
                .getClassWrapper(XMLHttpRequest::class.java)

        private val PATH2D_WRAPPER: JavaClassWrapper? = JavaClassWrapperFactory.instance!!
            .getClassWrapper(CanvasPath2D::class.java)

        private val EVENT_WRAPPER: JavaClassWrapper? = JavaClassWrapperFactory.instance!!
            .getClassWrapper(io.github.remmerw.thor.cobra.html.js.Event::class.java)
        private const val JS_SCHED_POLL_INTERVAL_MILLIS = 100
        private val JS_SCHED_JOIN_INTERVAL_MILLIS: Int = JS_SCHED_POLL_INTERVAL_MILLIS * 2

        // Timer ids should begin counting from 1 or more.
        // jQuery's ajax polling handler relies on a non-zero value (uses it as a boolean condition)
        // Chromium 37 starts counting from 1 while Firefox 32 starts counting from 2 (from developer consoles and plugins installed)
        private var timerIdCounter = 1
        private fun generateTimerID(): Int {
            synchronized(logger) {
                return timerIdCounter++
            }
        }

        private fun defineInstantiator(
            ws: Scriptable,
            name: String?,
            wrapper: JavaClassWrapper,
            ji: JavaInstantiator
        ) {
            val constructor = JavaObjectWrapper.getConstructor(name, wrapper, ws, ji)
            ScriptableObject.defineProperty(ws, name, constructor, ScriptableObject.READONLY)
        }

        private fun defineElementClass(
            scope: Scriptable, document: Document, jsClassName: String?,
            elementName: String?,
            javaClass: Class<*>?
        ) {
            val ji: JavaInstantiator = object : JavaInstantiator {
                override fun newInstance(args: Array<Any>): Any {
                    val d = document
                    checkNotNull(d) { "Document not set in current context." }
                    return d.createElement(elementName)
                }
            }
            val classWrapper = JavaClassWrapperFactory.instance!!.getClassWrapper(javaClass!!)
            val constructorFunction =
                JavaObjectWrapper.getConstructor(jsClassName, classWrapper, scope, ji)
            ScriptableObject.defineProperty(
                scope,
                jsClassName,
                constructorFunction,
                ScriptableObject.READONLY
            )
        }

        @HideFromJS
        fun getWindow(rcontext: HtmlRendererContext?): Window? {
            if (rcontext == null) {
                return null
            }
            synchronized(CONTEXT_WINDOWS) {
                val wref: WeakReference<Window?>? = CONTEXT_WINDOWS.get(rcontext)
                if (wref != null) {
                    val window = wref.get()
                    if (window != null) {
                        return window
                    }
                }
                val window = Window(rcontext, rcontext.userAgentContext)
                CONTEXT_WINDOWS.put(rcontext, WeakReference<Window?>(window))
                return window
            }
        }
    }
}
