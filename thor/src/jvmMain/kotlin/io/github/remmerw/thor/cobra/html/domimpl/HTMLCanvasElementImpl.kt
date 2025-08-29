/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2015 Uproot Labs India Pvt Ltd

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

 */
package io.github.remmerw.thor.cobra.html.domimpl

import io.github.remmerw.thor.cobra.html.js.NotGetterSetter
import io.github.remmerw.thor.cobra.util.gui.ColorFactory
import org.mozilla.javascript.typedarrays.NativeUint8ClampedArray
import org.w3c.dom.html.HTMLElement
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.LinearGradientPaint
import java.awt.Paint
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Base64
import java.util.Locale
import java.util.Stack
import javax.imageio.ImageIO
import kotlin.math.min

class HTMLCanvasElementImpl : HTMLAbstractUIElement("CANVAS"), HTMLElement {
    private val canvasContext = CanvasContext()
    var width: Int = 0
        private set
    var height: Int = 0
        private set
    private var image: BufferedImage? = null
    private var offsetX = 0
    private var offsetY = 0

    init {
        // The default width and height are defined by the spec to 300 x 150
        setBounds(0, 0, 300, 150)
    }

    @JvmOverloads
    fun toDataURL(type: String = "image/png", encoderOptions: Double = 1.0): String {
        var format = "png"
        if ("image/png" == type) {
            format = "png"
        } else if ("image/gif" == type) {
            format = "gif"
        } else if ("image/jpeg" == type) {
            format = "jpg"
        }

        if (this.width == 0 || this.height == 0) {
            return "data:,"
        }

        try {
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, format, outputStream)
            val outputStr = Base64.getEncoder().encodeToString(outputStream.toByteArray())
            return "data:" + type + ";base64," + outputStr
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Unexpected exception while encoding canvas to data-url")
        }
    }

    fun setHeight(height: Double) {
        this.height = (height.toInt())
        this.setAttribute("height", "" + this.height)
        refreshImageDimension()
    }

    fun setWidth(width: Double) {
        this.width = (width.toInt())
        this.setAttribute("width", "" + this.width)
        refreshImageDimension()
    }


    fun paintComponent(g: Graphics) {
        if (image != null) {
            // Draw a grid if debugging
            /** TODO debug
             * if (CobraParser.isDebugOn) {
             * final Graphics newG = g.create(offsetX, offsetY, computedWidth, computedHeight);
             * try {
             * drawGrid(newG);
             * } finally {
             * newG.dispose();
             * }
             * } */

            g.drawImage(image, offsetX, offsetY, null)
        }
    }


    fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        offsetX = x
        offsetY = y

        this.width = width
        this.height = height
        refreshImageDimension()
    }

    private fun refreshImageDimension() {
        if (image == null) {
            createNewImage(this.width, this.height)
        } else if (image!!.getWidth(null) != this.width || image!!.getHeight(null) != this.height) {
            createNewImage(this.width, this.height)
        }
    }

    private fun createNewImage(width: Int, height: Int) {
        if (width != 0 && height != 0) {
            image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            canvasContext.invalidate()
        } else {
            // TODO: Need to handle the case when width or height is zero. Buffered image doesn't accept zero width / height.
        }
    }

    private fun repaint() {
        uINode?.repaint(this@HTMLCanvasElementImpl)
    }

    private fun drawGrid(g: Graphics?) {
        val g2 = g as Graphics2D
        val height = image!!.getHeight(null)
        val width = image!!.getWidth(null)

        g2.color = gridColor

        run {
            var i = 0
            while (i < height) {
                g2.drawLine(0, i, width, i)
                i += GRID_SIZE
            }
        }

        var i = 0
        while (i < width) {
            g2.drawLine(i, 0, i, height)
            i += GRID_SIZE
        }
    }

    fun getContext(type: String?): CanvasContext {
        return canvasContext
    }

    class ImageData(val width: Int, val height: Int, val data: NativeUint8ClampedArray?)

    inner class CanvasContext {
        private val drawingStateStack: Stack<CanvasState> = Stack<CanvasState>()
        private var cpath2D = CanvasPath2D()
        private var rule = AlphaComposite.SRC_OVER
        private var cachedGraphics: Graphics2D? = null
        private var currDrawingState = CanvasState()

        fun fillRect(x: Int, y: Int, width: Int, height: Int) {
            val g2 = this.graphics
            g2.paint = currDrawingState.paintFill
            g2.fillRect(x, y, width, height)
            repaint()
        }

        fun clearRect(x: Int, y: Int, width: Int, height: Int) {
            val g2 = this.graphics
            g2.clearRect(x, y, width, height)
            repaint()
        }

        private val currentTransformMatrix: AffineTransform?
            get() {
                val g2 = this.graphics
                return g2.transform
            }

        private val currClip: Shape?
            get() {
                val g2 = this.graphics
                return g2.clip
            }

        fun scale(x: Double, y: Double) {
            val g2 = this.graphics
            g2.scale(x, y)
        }

        fun rotate(angle: Double) {
            val g2 = this.graphics
            g2.rotate(angle)
        }

        fun translate(x: Double, y: Double) {
            val g2 = this.graphics
            g2.translate(x, y)
        }

        fun transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
            val g2 = this.graphics
            val tx = AffineTransform(a, b, c, d, e, f)
            g2.transform(tx)
        }

        fun setTransform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
            val g2 = this.graphics
            val tx = AffineTransform(a, b, c, d, e, f)
            g2.transform = tx
        }

        fun resetTransform() {
            val g2 = this.graphics
            g2.transform = AffineTransform()
        }

        fun beginPath() {
            cpath2D = CanvasPath2D()
        }

        fun closePath() {
            cpath2D.closePath()
        }

        fun moveTo(x: Double, y: Double) {
            cpath2D.moveToWithTransform(x, y, this.currentTransformMatrix)
        }

        fun lineTo(x: Int, y: Int) {
            cpath2D.lineToWithTransform(x.toDouble(), y.toDouble(), this.currentTransformMatrix)
        }

        fun quadraticCurveTo(x1: Double, y1: Double, x2: Double, y2: Double) {
            cpath2D.quadraticCurveToWithTransform(x1, y1, x2, y2, this.currentTransformMatrix)
        }

        fun bezierCurveTo(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) {
            cpath2D.bezierCurveToWithTransform(x1, y1, x2, y2, x3, y3, this.currentTransformMatrix)
        }

        @JvmOverloads
        fun arc(
            x: Int,
            y: Int,
            radius: Int,
            startAngle: Double,
            endAngle: Double,
            antiClockwise: Boolean = false
        ) {
            cpath2D.arcWithTransform(
                x.toDouble(), y.toDouble(), radius.toDouble(), startAngle, endAngle, antiClockwise,
                this.currentTransformMatrix
            )
        }

        fun arcTo(x1: Double, y1: Double, x2: Double, y2: Double, radius: Double) {
            cpath2D.arcToWithTransform(x1, y1, x2, y2, radius, this.currentTransformMatrix)
        }

        @JvmOverloads
        fun ellipse(
            x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double,
            startAngle: Double, endAngle: Double, antiClockwise: Boolean = false
        ) {
            cpath2D.ellipseWithTransform(
                x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise,
                this.currentTransformMatrix
            )
        }

        fun rect(x: Double, y: Double, width: Double, height: Double) {
            cpath2D.rectWithTransform(x, y, width, height, this.currentTransformMatrix)
        }

        fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
            val g2 = this.graphics
            g2.paint = currDrawingState.paintStroke
            g2.draw(Rectangle2D.Double(x, y, w, h))
        }

        fun stroke() {
            val g2 = this.graphics
            val currAFT = g2.transform
            resetTransform()
            stroke(cpath2D)
            g2.transform = currAFT
        }

        fun stroke(cpath2D: CanvasPath2D) {
            val g2 = this.graphics
            g2.paint = currDrawingState.paintStroke
            g2.draw(cpath2D.path2D)
            repaint()
        }

        fun fill() {
            val g2 = this.graphics
            val currAFT = g2.transform
            resetTransform()
            fill(cpath2D)
            g2.transform = currAFT
        }

        fun fill(cpath2D: CanvasPath2D) {
            val g2 = this.graphics
            g2.paint = currDrawingState.paintFill
            g2.fill(cpath2D.path2D)
            repaint()
        }

        @JvmOverloads
        fun clip(cpath2D: CanvasPath2D = this.cpath2D) {
            val g2 = this.graphics
            g2.clip(cpath2D.path2D)
        }

        fun resetClip() {
            val g2 = this.graphics
            g2.clip = null
        }

        private fun toHex(r: Int, g: Int, b: Int): String {
            return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b)
        }

        private fun toBrowserHexValue(number: Int): String {
            val builder = StringBuilder(Integer.toHexString(number and 0xff))
            while (builder.length < 2) {
                builder.append("0")
            }
            return builder.toString().lowercase(Locale.getDefault())
        }

        var fillStyle: Any?
            get() = formatStyle(currDrawingState.paintFill)
            // TODO: Check if polymorphism can be handled in JavaObjectWrapper
            set(style) {
                if (style is String) {
                    currDrawingState.paintFill =
                        parseColor(style)
                } else if (style is CanvasGradient) {
                    currDrawingState.paintFill = style.toPaint()
                } else {
                    throw UnsupportedOperationException("Fill style not recognized")
                }
            }

        private fun formatStyle(paint: Paint?): Any? {
            if (paint is Color) {
                if (paint.alpha == 1) {
                    return toHex(paint.red, paint.green, paint.blue)
                } else {
                    println("Alpha: " + paint.alpha)
                    return "rgba(" + paint.red + ", " + paint.green + ", " + paint.blue + ", " + (paint.alpha / 255.0) + ")"
                }
            }
            // TODO: Handle canvas pattern and canvas gradient
            return null
        }

        var strokeStyle: Any?
            get() = formatStyle(currDrawingState.paintStroke)
            // TODO: Check if polymorphism can be handled in JavaObjectWrapper
            set(style) {
                if (style is String) {
                    currDrawingState.paintStroke =
                        parseColor(style)
                } else if (style is CanvasGradient) {
                    currDrawingState.paintStroke = style.toPaint()
                } else {
                    throw UnsupportedOperationException("Stroke style not recognized")
                }
            }

        val globalAlpha: Float
            get() = currDrawingState.globalAlpha

        fun setGlobalAlpha(alpha: Double) {
            val g2 = this.graphics
            currDrawingState.globalAlpha = alpha.toFloat()
            val a = AlphaComposite.getInstance(rule, currDrawingState.globalAlpha)
            g2.composite = a
        }

        var globalCompositeOperation: String?
            get() = currDrawingState.globalCompositeOperation
            set(composition) {
                val g2 = this.graphics
                currDrawingState.globalCompositeOperation = composition

                if ("source-atop" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.SRC_ATOP
                } else if ("source-in" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.SRC_IN
                } else if ("source-out" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.SRC_OUT
                } else if ("source-over" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.SRC_OVER
                } else if ("destination-atop" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.DST_ATOP
                } else if ("destination-in" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.DST_IN
                } else if ("destination-out" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.DST_OUT
                } else if ("destination-over" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.DST_OVER
                } else if ("xor" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.XOR
                } else if ("clear" == currDrawingState.globalCompositeOperation) {
                    rule = AlphaComposite.CLEAR
                }

                val a =
                    AlphaComposite.getInstance(rule, currDrawingState.globalAlpha)
                g2.setComposite(a)
            }

        var lineWidth: Double
            get() = currDrawingState.lineWidth.toDouble()
            set(width) {
                currDrawingState.lineWidth = width.toFloat()
                setStroke()
            }

        var lineCap: String?
            get() {
                if (currDrawingState.lineCap == BasicStroke.CAP_BUTT) {
                    return "butt"
                } else if (currDrawingState.lineCap == BasicStroke.CAP_ROUND) {
                    return "round"
                } else if (currDrawingState.lineCap == BasicStroke.CAP_SQUARE) {
                    return "square"
                }
                return null
            }
            set(cap) {
                if ("butt" == cap) {
                    currDrawingState.lineCap = BasicStroke.CAP_BUTT
                } else if ("round" == cap) {
                    currDrawingState.lineCap = BasicStroke.CAP_ROUND
                } else if ("square" == cap) {
                    currDrawingState.lineCap = BasicStroke.CAP_SQUARE
                }

                setStroke()
            }

        var lineJoin: String?
            get() {
                if (currDrawingState.lineJoin == BasicStroke.JOIN_MITER) {
                    return "miter"
                } else if (currDrawingState.lineCap == BasicStroke.JOIN_BEVEL) {
                    return "bevel"
                } else if (currDrawingState.lineCap == BasicStroke.JOIN_ROUND) {
                    return "round"
                }
                return null
            }
            set(join) {
                if ("round" == join) {
                    currDrawingState.lineJoin = BasicStroke.JOIN_ROUND
                } else if ("bevel" == join) {
                    currDrawingState.lineJoin = BasicStroke.JOIN_BEVEL
                } else if ("miter" == join) {
                    currDrawingState.lineJoin = BasicStroke.JOIN_MITER
                }
                setStroke()
            }

        val miterLimit: Float
            get() = currDrawingState.miterLimit

        fun setMiterLimit(miterLimit: Double) {
            currDrawingState.miterLimit = miterLimit.toFloat()
            setStroke()
        }

        @get:NotGetterSetter
        @set:NotGetterSetter
        var lineDash: DoubleArray
            get() {
                val lineDash1 =
                    DoubleArray(currDrawingState.lineDash!!.size)
                for (i in currDrawingState.lineDash!!.indices) {
                    lineDash1[i] = currDrawingState.lineDash!![i].toDouble()
                }
                return lineDash1
            }
            set(segments) {
                currDrawingState.lineDash = FloatArray(segments.size)
                for (i in segments.indices) {
                    currDrawingState.lineDash!![i] = segments[i].toFloat()
                }
                setStroke()
            }

        var lineDashOffset: Double
            get() = currDrawingState.lineDashOffset.toDouble()
            set(lineDashOffset) {
                currDrawingState.lineDashOffset = lineDashOffset.toFloat()
                setStroke()
            }

        private fun setStroke() {
            val g2 = this.graphics
            g2.stroke = BasicStroke(
                currDrawingState.lineWidth,
                currDrawingState.lineCap,
                currDrawingState.lineJoin,
                currDrawingState.miterLimit,
                currDrawingState.lineDash,
                currDrawingState.lineDashOffset
            )
        }

        fun createImageData(width: Int, height: Int): ImageData {
            val data = NativeUint8ClampedArray(width * height * 4)
            return ImageData(width, height, data)
        }

        fun createImageData(imgdata: ImageData): ImageData {
            val width = imgdata.width
            val height = imgdata.height
            val data = NativeUint8ClampedArray(width * height * 4)
            return ImageData(width, height, data)
        }

        fun getImageData(x: Int, y: Int, width: Int, height: Int): ImageData {
            val argbArray = IntArray(width * height)
            image!!.getRGB(x, y, width, height, argbArray, 0, width)
            val clampedBuffer = NativeUint8ClampedArray(width * height * 4)
            val clampedByteBuffer = clampedBuffer.buffer.getBuffer()
            var i = 0
            var j = 0
            while (i < argbArray.size) {
                val argb = argbArray[i]
                clampedByteBuffer[j] = ((argb shr 16) and 0xff).toByte()
                clampedByteBuffer[j + 1] = ((argb shr 8) and 0xff).toByte()
                clampedByteBuffer[j + 2] = ((argb) and 0xff).toByte()
                clampedByteBuffer[j + 3] = ((argb shr 24) and 0xff).toByte()
                i++
                j += 4
            }
            return ImageData(width, height, clampedBuffer)
        }

        @JvmOverloads
        fun putImageData(
            imgData: ImageData,
            x: Int,
            y: Int,
            width: Int = imgData.width,
            height: Int = imgData.height
        ) {
            println(
                "putImageData(imgData, x, y, width, height)" + arrayOf<Any>(
                    x,
                    y,
                    width,
                    height
                ).contentToString()
            )
            if (x >= 0 && y >= 0) {
                val dataBytes = imgData.data!!.buffer.getBuffer()
                val argbArray = IntArray(imgData.width * imgData.height)
                var i = 0
                var j = 0
                while (i < argbArray.size) {
                    argbArray[i] = packBytes2Int(
                        dataBytes[j + 3], dataBytes[j],
                        dataBytes[j + 1], dataBytes[j + 2]
                    )
                    i++
                    j += 4
                }
                image!!.setRGB(
                    x,
                    y,
                    min(width, imgData.width),
                    min(height, imgData.height),
                    argbArray,
                    0,
                    imgData.width
                )
                repaint()
            }
        }

        @Synchronized
        fun invalidate() {
            cachedGraphics = null
        }

        @get:Synchronized
        private val graphics: Graphics2D
            get() {
                if (cachedGraphics == null) {
                    cachedGraphics = image!!.graphics as Graphics2D?
                    cachedGraphics!!.background = Color(0, 0, 0, 0)
                    cachedGraphics!!.paint = Color.BLACK
                    cachedGraphics!!.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                    )
                }
                return cachedGraphics!!
            }

        fun createLinearGradient(x0: Float, y0: Float, x1: Float, y1: Float): CanvasGradient {
            val linearGradient = LinearCanvasGradient(x0, y0, x1, y1)
            return linearGradient
        }



        fun restore() {
            if (drawingStateStack.empty()) {
                // Do nothing
            } else {
                currDrawingState = drawingStateStack.pop()
                this.setGlobalAlpha(currDrawingState.globalAlpha.toDouble())
                this.globalCompositeOperation = currDrawingState.globalCompositeOperation
                this.setStroke()
                this.graphics.transform = currDrawingState.currTransformMatrix
                this.graphics.clip = currDrawingState.currClippingRegion
            }
        }

        fun fillText(s: String, x: Double, y: Double) {
            val chars = s.toCharArray()
            val g2 = this.graphics
            g2.paint = currDrawingState.paintFill
            g2.drawChars(chars, 0, chars.size, x.toInt(), y.toInt())
        }

        private inner class CanvasState : Cloneable {
            var currTransformMatrix: AffineTransform? = null
            var currClippingRegion: Shape? = null
            var paintFill: Paint? = Color.BLACK
            var paintStroke: Paint? = Color.BLACK
            var lineWidth = 1f
            var lineCap = BasicStroke.CAP_BUTT
            var lineJoin = BasicStroke.JOIN_MITER
            var miterLimit = 10f
            var lineDash: FloatArray? = null
            var lineDashOffset = 0f
            var globalAlpha = 1f
            var globalCompositeOperation: String? = "source-over"

            @Throws(CloneNotSupportedException::class)
            override fun clone(): Any {
                return super.clone()
            }
        }
    }

    abstract inner class CanvasGradient {
        protected val offsets: ArrayList<Float?> = ArrayList<Float?>()
        protected val colors: ArrayList<Color?> = ArrayList<Color?>()

        fun addColorStop(offset: Float, color: String) {
            this.offsets.add(offset)
            this.colors.add(parseColor(color))
        }

        abstract fun toPaint(): Paint?
    }

    inner class LinearCanvasGradient internal constructor(
        private val x0: Float,
        private val y0: Float,
        private val x1: Float,
        private val y1: Float
    ) : CanvasGradient() {
        override fun toPaint(): Paint? {
            if (colors.size == 0) {
                return Color(0, 0, 0, 0)
            } else if (colors.size == 1) {
                return colors.get(0)
            } else {
                // TODO: See if this can be optimized
                val offsetsArray = FloatArray(offsets.size)
                for (i in offsets.indices) {
                    offsetsArray[i] = offsets.get(i)!!
                }
                return LinearGradientPaint(
                    x0,
                    y0,
                    x1,
                    y1,
                    offsetsArray,
                    colors.toTypedArray<Color?>()
                )
            }
        }
    }

    companion object {
        private val gridColor = Color(30, 30, 30, 30)
        private const val GRID_SIZE = 10
        private fun packBytes2Int(a: Byte, b: Byte, c: Byte, d: Byte): Int {
            return (a.toInt() shl 24) or ((b.toInt() and 0xff) shl 16) or ((c.toInt() and 0xff) shl 8) or (d.toInt() and 0xff)
        }

        private fun parseColor(color: String): Color? {
            return ColorFactory.instance?.getColor(color)
        }
    }
}