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
package io.github.remmerw.thor.cobra.html.dom

import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Arc2D
import java.awt.geom.Line2D
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Path2D
import java.awt.geom.Point2D

class CanvasPath2D {
    var path2D: Path2D = Path2D.Double()
    private var needNewSubpath = true
    private var currPoint: Point2D? = null

    fun moveTo(x: Double, y: Double) {
        moveToWithTransform(x, y, null)
    }

    fun moveToWithTransform(x: Double, y: Double, aft: AffineTransform?) {
        if (aft == null) {
            path2D.moveTo(x, y)
        } else {
            val p1 = aft.transform(Point2D.Double(x, y), null)
            path2D.moveTo(p1.x, p1.y)
        }
        currPoint = Point2D.Double(x, y)
        needNewSubpath = false
    }

    fun closePath() {
        if (!needNewSubpath) {
            path2D.closePath()
        }
        needNewSubpath = currPoint == null
    }

    fun lineTo(x: Double, y: Double) {
        lineToWithTransform(x, y, null)
    }

    fun lineToWithTransform(x: Double, y: Double, aft: AffineTransform?) {
        if (needNewSubpath) {
            ensureSubpathWithTransform(x, y, aft)
        } else {
            if (aft == null) {
                path2D.lineTo(x, y)
            } else {
                val p1 = aft.transform(Point2D.Double(x, y), null)
                path2D.lineTo(p1.x, p1.y)
            }
        }
        currPoint = Point2D.Double(x, y)
        needNewSubpath = false
    }

    fun quadraticCurveTo(x1: Double, y1: Double, x2: Double, y2: Double) {
        quadraticCurveToWithTransform(x1, y1, x2, y2, null)
    }

    fun quadraticCurveToWithTransform(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double,
        aft: AffineTransform?
    ) {
        if (aft == null) {
            path2D.quadTo(x1, y1, x2, y2)
        } else {
            val p1 = aft.transform(Point2D.Double(x1, y1), null)
            val p2 = aft.transform(Point2D.Double(x2, y2), null)
            path2D.quadTo(p1.x, p1.y, p2.x, p2.y)
        }
        currPoint = Point2D.Double(x2, y2)
        needNewSubpath = false
    }

    fun bezierCurveTo(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) {
        bezierCurveToWithTransform(x1, y1, x2, y2, x3, y3, null)
    }

    fun bezierCurveToWithTransform(
        x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double,
        aft: AffineTransform?
    ) {
        if (aft == null) {
            path2D.quadTo(x1, y1, x2, y2)
        } else {
            val p1 = aft.transform(Point2D.Double(x1, y1), null)
            val p2 = aft.transform(Point2D.Double(x2, y2), null)
            val p3 = aft.transform(Point2D.Double(x3, y3), null)
            path2D.curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
        }
        currPoint = Point2D.Double(x3, y3)
        needNewSubpath = false
    }

    @JvmOverloads
    fun arc(
        x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double,
        antiClockwise: Boolean = false
    ) {
        arcWithTransform(x, y, radius, startAngle, endAngle, antiClockwise, null)
    }

    fun arcWithTransform(
        x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double,
        antiClockwise: Boolean, aft: AffineTransform?
    ) {
        ellipseWithTransform(x, y, radius, radius, 0.0, startAngle, endAngle, antiClockwise, aft)
    }

    private fun appendWithTransform(shape: Shape, aft: AffineTransform?, connect: Boolean) {
        if (aft != null) {
            val pi = shape.getPathIterator(aft)
            path2D.append(pi, connect)
        } else {
            path2D.append(shape, connect)
        }
    }

    private fun setCurrPoint(aft: AffineTransform?) {
        if (aft != null) {
            try {
                currPoint = aft.createInverse().transform(path2D.currentPoint, null)
            } catch (e: NoninvertibleTransformException) {
                throw IllegalArgumentException(e)
            }
        } else {
            currPoint = path2D.currentPoint
        }
    }

    fun arcTo(x1: Double, y1: Double, x2: Double, y2: Double, radius: Double) {
        arcToWithTransform(x1, y1, x2, y2, radius, null)
    }

    fun arcToWithTransform(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double,
        radius: Double,
        aft: AffineTransform?
    ) {
        val p0 = ensureSubpathWithTransform(x1, y1, aft)
        val p1: Point2D = Point2D.Double(x1, y1)
        val p2: Point2D = Point2D.Double(x2, y2)
        val l1: Line2D = Line2D.Double(p0, p2)
        if (p0 == p1 || p1 == p2) {
            lineToWithTransform(x1, y1, aft)
        } else if (l1.contains(p1)) {
            lineToWithTransform(x1, y1, aft)
        } else {
            val arcTo = Arc2D.Double()
            arcTo.setArcByTangent(p0, p1, p2, radius)
            appendWithTransform(arcTo, aft, true)
        }
    }

    @JvmOverloads
    fun ellipse(
        x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double,
        startAngle: Double, endAngle: Double, antiClockwise: Boolean = false
    ) {
        ellipseWithTransform(
            x,
            y,
            radiusX,
            radiusY,
            rotation,
            startAngle,
            endAngle,
            antiClockwise,
            null
        )
    }

    fun ellipseWithTransform(
        x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double,
        startAngle: Double, endAngle: Double, antiClockwise: Boolean, aft: AffineTransform?
    ) {
        val start: Double
        val end: Double
        val extent: Double
        val diffAngle = if (antiClockwise) (startAngle - endAngle) else (endAngle - startAngle)

        if (diffAngle >= TWO_PI) {
            start = 0.0
            end = TWO_PI
            extent = TWO_PI
        } else {
            start = tweakStart(0.0, -startAngle % TWO_PI, TWO_PI)
            end = tweakEnd(start, -endAngle % TWO_PI, TWO_PI + start)
            extent = if (antiClockwise) (end - start) else -(TWO_PI + (start - end))
        }

        val ellipse = Arc2D.Double(
            x - radiusX, y - radiusY, 2 * radiusX, 2 * radiusY, Math.toDegrees(start),
            Math.toDegrees(extent), Arc2D.OPEN
        )
        val rotatedT: AffineTransform?
        if (aft != null) {
            rotatedT = AffineTransform(aft)
            rotatedT.rotate(rotation, x, y)
        } else {
            rotatedT = AffineTransform.getRotateInstance(rotation, x, y)
        }
        appendWithTransform(ellipse, rotatedT, true)
        setCurrPoint(aft)
        needNewSubpath = false
    }

    fun rect(x: Double, y: Double, width: Double, height: Double) {
        rectWithTransform(x, y, width, height, null)
    }

    fun rectWithTransform(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        aft: AffineTransform?
    ) {
        // Note: We can't use Rectangle2D because it doesn't support negative width, height, nor can we adjust x, y for negative
        // widths / heights because the clockwise / anti-clockwise nature of the path isn't preserved
        moveToWithTransform(x, y, aft)
        lineToWithTransform(x + width, y, aft)
        lineToWithTransform(x + width, y + height, aft)
        lineToWithTransform(x, y + height, aft)
        closePath()
        moveToWithTransform(x, y, aft)
    }

    private fun ensureSubpathWithTransform(x: Double, y: Double, aft: AffineTransform?): Point2D {
        if (needNewSubpath) {
            moveToWithTransform(x, y, aft)
            return Point2D.Double(x, y)
        } else {
            return currPoint!!
        }
    }

    companion object {
        private val TWO_PI = 2 * Math.PI
        private fun tweakStart(start: Double, value: Double, end: Double): Double {
            var value = value
            while (value < start) {
                value += (TWO_PI)
            }
            while (value > end) {
                value -= (TWO_PI)
            }
            return value
        }

        private fun tweakEnd(start: Double, value: Double, end: Double): Double {
            var value = value
            while (value <= start) {
                value += (TWO_PI)
            }
            while (value > end) {
                value -= (TWO_PI)
            }
            return value
        }
    }
}