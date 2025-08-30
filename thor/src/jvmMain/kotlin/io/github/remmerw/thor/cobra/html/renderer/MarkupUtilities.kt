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
 * Created on Apr 16, 2005
 */
package io.github.remmerw.thor.cobra.html.renderer

import java.awt.Point
import java.awt.Rectangle

//import java.util.logging.*;
/**
 * @author J. H. S.
 */
internal object MarkupUtilities {
    fun findRenderable(
        renderables: Array<Renderable?>,
        point: Point,
        vertical: Boolean
    ): BoundableRenderable? {
        return findRenderable(renderables, point, 0, renderables.size, vertical)
    }

    fun findRenderable(
        renderables: Array<Renderable?>,
        x: Int,
        y: Int,
        vertical: Boolean
    ): BoundableRenderable? {
        return findRenderable(renderables, x, y, 0, renderables.size, vertical)
    }

    private fun findRenderable(
        renderables: Array<Renderable?>, point: Point, firstIndex: Int,
        length: Int, vertical: Boolean
    ): BoundableRenderable? {
        return findRenderable(renderables, point.x, point.y, firstIndex, length, vertical)
    }

    private fun findRenderable(
        renderables: Array<Renderable?>, x: Int, y: Int, firstIndex: Int,
        length: Int, vertical: Boolean
    ): BoundableRenderable? {
        for (i in firstIndex + length - 1 downTo firstIndex) {
            val br2 = renderables[i]
            if (br2 is BoundableRenderable) {
                if ((!br2.isDelegated) && br2.contains(x, y)) {
                    return br2
                }
            }
        }
        return null
    }

    // Linear scan version
    fun findRenderables(
        renderables: Array<Renderable?>,
        x: Int,
        y: Int,
        vertical: Boolean
    ): MutableList<BoundableRenderable?>? {
        var found: MutableList<BoundableRenderable?>? = null
        for (i in renderables.indices) {
            val br = renderables[i]
            if (br is BoundableRenderable) {
                if ((!br.isDelegated) && br.contains(x, y)) {
                    if (found == null) {
                        found = ArrayList<BoundableRenderable?>()
                    }
                    found.add(br)
                }
            }
        }
        return found
    }

    fun findRenderables(
        renderables: Array<Renderable?>,
        clipArea: Rectangle,
        vertical: Boolean
    ): Range {
        return findRenderables(renderables, clipArea, 0, renderables.size, vertical)
    }

    private fun findRenderables(
        renderables: Array<Renderable?>, clipArea: Rectangle, firstIndex: Int, length: Int,
        vertical: Boolean
    ): Range {
        if (length == 0) {
            return Range(0, 0)
        }
        var offset1 = findFirstIndex(renderables, clipArea, firstIndex, length, vertical)
        var offset2 = findLastIndex(renderables, clipArea, firstIndex, length, vertical)
        if ((offset1 == -1) && (offset2 == -1)) {
            // if(logger.isLoggable(Level.INFO))logger.info("findRenderables(): Range not found for clipArea="
            // + clipArea + ",length=" + length);
            // for(int i = firstIndex; i < length; i++) {
            // logger.info("findRenderables(): renderable.bounds=" +
            // renderables[i].getBounds());
            // }
            return Range(0, 0)
        }
        if (offset1 == -1) {
            offset1 = firstIndex
        }
        if (offset2 == -1) {
            offset2 = (firstIndex + length) - 1
        }
        return Range(offset1, (offset2 - offset1) + 1)
    }

    /*
  private static int findFirstIndex(final Renderable[] renderables, final Rectangle clipArea, final int index, final int length,
      final boolean vertical) {
    Diagnostics.Assert(length > 0, "length=" + length);
    if (length == 1) {
      final Renderable r = renderables[index];
      Rectangle rbounds;
      if (r instanceof BoundableRenderable) {
        rbounds = ((BoundableRenderable) r).getVisualBounds();
      } else {
        return -1;
      }
      if (intersects(rbounds, clipArea, vertical)) {
        return index;
      } else {
        return -1;
      }
    } else {
      final int middleIndex = index + (length / 2);
      final Renderable r = renderables[middleIndex];
      Rectangle rbounds;
      if (r instanceof BoundableRenderable) {
        rbounds = ((BoundableRenderable) r).getVisualBounds();
      } else {
        final int leftIndex = findFirstIndex(renderables, clipArea, index, middleIndex - index, vertical);
        if (leftIndex != -1) {
          return leftIndex;
        }
        return findFirstIndex(renderables, clipArea, middleIndex + 1, length - ((middleIndex - index) + 1), vertical);
      }
      if (vertical) {
        if ((rbounds.y + rbounds.height) < clipArea.y) {
          final int newLen = length - ((middleIndex - index) + 1);
          return newLen == 0 ? -1 : findFirstIndex(renderables, clipArea, middleIndex + 1, newLen, vertical);
        } else {
          final int newLen = middleIndex - index;
          final int resultIdx = newLen == 0 ? -1 : findFirstIndex(renderables, clipArea, index, newLen, vertical);
          if (resultIdx == -1) {
            if (intersects(clipArea, rbounds, vertical)) {
              return middleIndex;
            }
          }
          return resultIdx;
        }
      } else {
        if ((rbounds.x + rbounds.width) < clipArea.x) {
          return findFirstIndex(renderables, clipArea, middleIndex + 1, length - (middleIndex - index), vertical);
        } else {
          final int resultIdx = findFirstIndex(renderables, clipArea, index, middleIndex - index, vertical);
          if (resultIdx == -1) {
            if (intersects(clipArea, rbounds, vertical)) {
              return middleIndex;
            }
          }
          return resultIdx;
        }
      }
    }
  } */
    private fun findFirstIndex(
        renderables: Array<Renderable?>, clipArea: Rectangle, index: Int, length: Int,
        vertical: Boolean
    ): Int {
        for (i in index..<length) {
            val ri = renderables[i]
            if (ri is BoundableRenderable) {
                if (intersects(clipArea, ri.visualBounds()!!, vertical)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun findLastIndex(
        renderables: Array<Renderable?>, clipArea: Rectangle, index: Int, length: Int,
        vertical: Boolean
    ): Int {
        for (i in index + length - 1 downTo index) {
            val ri = renderables[i]
            if (ri is BoundableRenderable) {
                if (intersects(clipArea, ri.visualBounds()!!, vertical)) {
                    return i
                }
            }
        }
        return -1
    }

    /*
  private static int findLastIndex(final Renderable[] renderables, final Rectangle clipArea, final int index, final int length,
      final boolean vertical) {
    Diagnostics.Assert(length > 0, "length<=0");
    if (length == 1) {
      final Renderable r = renderables[index];
      Rectangle rbounds;
      if (r instanceof BoundableRenderable) {
        rbounds = ((BoundableRenderable) r).getVisualBounds();
      } else {
        return -1;
      }
      if (intersects(clipArea, rbounds, vertical)) {
        return index;
      } else {
        return -1;
      }
    } else {
      final int middleIndex = index + (length / 2);
      final Renderable r = renderables[middleIndex];
      Rectangle rbounds;
      if (r instanceof BoundableRenderable) {
        rbounds = ((BoundableRenderable) r).getVisualBounds();
      } else {
        final int rightIndex = findLastIndex(renderables, clipArea, middleIndex + 1, length - ((middleIndex - index) + 1), vertical);
        if (rightIndex != -1) {
          return rightIndex;
        }
        return findLastIndex(renderables, clipArea, index, middleIndex - index, vertical);
      }
      if (vertical) {
        if (rbounds.y > (clipArea.y + clipArea.height)) {
          return findLastIndex(renderables, clipArea, index, middleIndex - index, vertical);
        } else {
          final int newLen = length - ((middleIndex - index) + 1);
          final int resultIdx = newLen == 0 ? -1 : findLastIndex(renderables, clipArea, middleIndex + 1, newLen, vertical);
          if (resultIdx == -1) {
            if (intersects(clipArea, rbounds, vertical)) {
              return middleIndex;
            }
          }
          return resultIdx;
        }
      } else {
        if (rbounds.x > (clipArea.x + clipArea.width)) {
          return findLastIndex(renderables, clipArea, index, middleIndex - index, vertical);
        } else {
          final int resultIdx = findLastIndex(renderables, clipArea, middleIndex + 1, length - ((middleIndex - index) + 1), vertical);
          if (resultIdx == -1) {
            if (intersects(clipArea, rbounds, vertical)) {
              return middleIndex;
            }
          }
          return resultIdx;
        }
      }
    }
  } */
    private fun intersects(rect1: Rectangle, rect2: Rectangle, vertical: Boolean): Boolean {
        if (vertical) {
            return !((rect1.y > (rect2.y + rect2.height)) || (rect2.y > (rect1.y + rect1.height)))
        } else {
            return !((rect1.x > (rect2.x + rect2.width)) || (rect2.x > (rect1.x + rect1.width)))
        }
    }
}
