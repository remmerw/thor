package io.github.remmerw.thor.style

import androidx.compose.ui.graphics.Color
import java.util.Locale
import java.util.StringTokenizer
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.min

/**
 * @author J. H. S.
 */
class ColorFactory private constructor() {
    private val colorMap: MutableMap<String, Color> = HashMap(256)

    init {
        val colorMap = this.colorMap
        synchronized(this) {
            colorMap.put("transparent", TRANSPARENT)
            // http://www.w3schools.com/css/css_colornames.asp
            colorMap.put("aliceblue", Color(0xf0f8ff))
            colorMap.put("antiquewhite", Color(0xfaebd7))
            colorMap.put("aqua", Color(0x00ffff))
            colorMap.put("aquamarine", Color(0x7fffd4))
            colorMap.put("azure", Color(0xf0ffff))
            colorMap.put("beige", Color(0xf5f5dc))
            colorMap.put("bisque", Color(0xffe4c4))
            colorMap.put("black", Color(0x000000))
            colorMap.put("blanchedalmond", Color(0xffebcd))
            colorMap.put("blue", Color(0x0000ff))
            colorMap.put("blueviolet", Color(0x8a2be2))
            colorMap.put("brown", Color(0xa52a2a))
            colorMap.put("burlywood", Color(0xdeb887))
            colorMap.put("cadetblue", Color(0x5f9ea0))
            colorMap.put("chartreuse", Color(0x7fff00))
            colorMap.put("chocolate", Color(0xd2691e))
            colorMap.put("coral", Color(0xff7f50))
            colorMap.put("cornflowerblue", Color(0x6495ed))
            colorMap.put("cornsilk", Color(0xfff8dc))
            colorMap.put("crimson", Color(0xdc143c))
            colorMap.put("cyan", Color(0x00ffff))
            colorMap.put("darkblue", Color(0x00008b))
            colorMap.put("darkcyan", Color(0x008b8b))
            colorMap.put("darkgoldenrod", Color(0xb8860b))
            colorMap.put("darkgray", Color(0xa9a9a9))
            colorMap.put("darkgrey", Color(0xa9a9a9))
            colorMap.put("darkgreen", Color(0x006400))
            colorMap.put("darkkhaki", Color(0xbdb76b))
            colorMap.put("darkmagenta", Color(0x8b008b))
            colorMap.put("darkolivegreen", Color(0x556b2f))
            colorMap.put("darkorange", Color(0xff8c00))
            colorMap.put("darkorchid", Color(0x9932cc))
            colorMap.put("darkred", Color(0x8b0000))
            colorMap.put("darksalmon", Color(0xe9967a))
            colorMap.put("darkseagreen", Color(0x8fbc8f))
            colorMap.put("darkslateblue", Color(0x483d8b))
            colorMap.put("darkslategray", Color(0x2f4f4f))
            colorMap.put("darkslategrey", Color(0x2f4f4f))
            colorMap.put("darkturquoise", Color(0x00ced1))
            colorMap.put("darkviolet", Color(0x9400d3))
            colorMap.put("deeppink", Color(0xff1493))
            colorMap.put("deepskyblue", Color(0x00bfff))
            colorMap.put("dimgray", Color(0x696969))
            colorMap.put("dimgrey", Color(0x696969))
            colorMap.put("dodgerblue", Color(0x1e90ff))
            colorMap.put("firebrick", Color(0xb22222))
            colorMap.put("floralwhite", Color(0xfffaf0))
            colorMap.put("forestgreen", Color(0x228b22))
            colorMap.put("fuchsia", Color(0xff00ff))
            colorMap.put("gainsboro", Color(0xdcdcdc))
            colorMap.put("ghostwhite", Color(0xf8f8ff))
            colorMap.put("gold", Color(0xffd700))
            colorMap.put("goldenrod", Color(0xdaa520))
            colorMap.put("gray", Color(0x808080))
            colorMap.put("grey", Color(0x808080))
            colorMap.put("green", Color(0x008000))
            colorMap.put("greenyellow", Color(0xadff2f))
            colorMap.put("honeydew", Color(0xf0fff0))
            colorMap.put("hotpink", Color(0xff69b4))
            colorMap.put("indianred", Color(0xcd5c5c))
            colorMap.put("indigo", Color(0x4b0082))
            colorMap.put("ivory", Color(0xfffff0))
            colorMap.put("khaki", Color(0xf0e68c))
            colorMap.put("lavender", Color(0xe6e6fa))
            colorMap.put("lavenderblush", Color(0xfff0f5))
            colorMap.put("lawngreen", Color(0x7cfc00))
            colorMap.put("lemonchiffon", Color(0xfffacd))
            colorMap.put("lightblue", Color(0xadd8e6))
            colorMap.put("lightcoral", Color(0xf08080))
            colorMap.put("lightcyan", Color(0xe0ffff))
            colorMap.put("lightgoldenrodyellow", Color(0xfafad2))
            colorMap.put("lightgray", Color(0xd3d3d3))
            colorMap.put("lightgrey", Color(0xd3d3d3))
            colorMap.put("lightgreen", Color(0x90ee90))
            colorMap.put("lightpink", Color(0xffb6c1))
            colorMap.put("lightsalmon", Color(0xffa07a))
            colorMap.put("lightseagreen", Color(0x20b2aa))
            colorMap.put("lightskyblue", Color(0x87cefa))
            colorMap.put("lightslategray", Color(0x778899))
            colorMap.put("lightslategrey", Color(0x778899))
            colorMap.put("lightsteelblue", Color(0xb0c4de))
            colorMap.put("lightyellow", Color(0xffffe0))
            colorMap.put("lime", Color(0x00ff00))
            colorMap.put("limegreen", Color(0x32cd32))
            colorMap.put("linen", Color(0xfaf0e6))
            colorMap.put("magenta", Color(0xff00ff))
            colorMap.put("maroon", Color(0x800000))
            colorMap.put("mediumaquamarine", Color(0x66cdaa))
            colorMap.put("mediumblue", Color(0x0000cd))
            colorMap.put("mediumorchid", Color(0xba55d3))
            colorMap.put("mediumpurple", Color(0x9370d8))
            colorMap.put("mediumseagreen", Color(0x3cb371))
            colorMap.put("mediumslateblue", Color(0x7b68ee))
            colorMap.put("mediumspringgreen", Color(0x00fa9a))
            colorMap.put("mediumturquoise", Color(0x48d1cc))
            colorMap.put("mediumvioletred", Color(0xc71585))
            colorMap.put("midnightblue", Color(0x191970))
            colorMap.put("mintcream", Color(0xf5fffa))
            colorMap.put("mistyrose", Color(0xffe4e1))
            colorMap.put("moccasin", Color(0xffe4b5))
            colorMap.put("navajowhite", Color(0xffdead))
            colorMap.put("navy", Color(0x000080))
            colorMap.put("oldlace", Color(0xfdf5e6))
            colorMap.put("olive", Color(0x808000))
            colorMap.put("olivedrab", Color(0x6b8e23))
            colorMap.put("orange", Color(0xffa500))
            colorMap.put("orangered", Color(0xff4500))
            colorMap.put("orchid", Color(0xda70d6))
            colorMap.put("palegoldenrod", Color(0xeee8aa))
            colorMap.put("palegreen", Color(0x98fb98))
            colorMap.put("paleturquoise", Color(0xafeeee))
            colorMap.put("palevioletred", Color(0xd87093))
            colorMap.put("papayawhip", Color(0xffefd5))
            colorMap.put("peachpuff", Color(0xffdab9))
            colorMap.put("peru", Color(0xcd853f))
            colorMap.put("pink", Color(0xffc0cb))
            colorMap.put("plum", Color(0xdda0dd))
            colorMap.put("powderblue", Color(0xb0e0e6))
            colorMap.put("purple", Color(0x800080))
            colorMap.put("red", Color(0xff0000))
            colorMap.put("rosybrown", Color(0xbc8f8f))
            colorMap.put("royalblue", Color(0x4169e1))
            colorMap.put("saddlebrown", Color(0x8b4513))
            colorMap.put("salmon", Color(0xfa8072))
            colorMap.put("sandybrown", Color(0xf4a460))
            colorMap.put("seagreen", Color(0x2e8b57))
            colorMap.put("seashell", Color(0xfff5ee))
            colorMap.put("sienna", Color(0xa0522d))
            colorMap.put("silver", Color(0xc0c0c0))
            colorMap.put("skyblue", Color(0x87ceeb))
            colorMap.put("slateblue", Color(0x6a5acd))
            colorMap.put("slategray", Color(0x708090))
            colorMap.put("slategrey", Color(0x708090))
            colorMap.put("snow", Color(0xfffafa))
            colorMap.put("springgreen", Color(0x00ff7f))
            colorMap.put("steelblue", Color(0x4682b4))
            colorMap.put("tan", Color(0xd2b48c))
            colorMap.put("teal", Color(0x008080))
            colorMap.put("thistle", Color(0xd8bfd8))
            colorMap.put("tomato", Color(0xff6347))
            colorMap.put("turquoise", Color(0x40e0d0))
            colorMap.put("violet", Color(0xee82ee))
            colorMap.put("wheat", Color(0xf5deb3))
            colorMap.put("white", Color(0xffffff))
            colorMap.put("whitesmoke", Color(0xf5f5f5))
            colorMap.put("yellow", Color(0xffff00))
            colorMap.put("yellowgreen", Color(0x9acd32))
        }
    }

    fun isColor(colorSpec: String): Boolean {
        if (colorSpec.startsWith("#")) {
            return true
        }
        val normalSpec = colorSpec.lowercase(Locale.getDefault())
        if (normalSpec.startsWith(RGB_START)) {
            return true
        }
        synchronized(this) {
            return colorMap.containsKey(normalSpec)
        }
    }

    fun getColor(colorSpec: String): Color? {
        val normalSpec = colorSpec.lowercase(Locale.getDefault())
        synchronized(this) {
            var color = colorMap.get(normalSpec)
            if (color == null) {
                if (normalSpec.startsWith(RGB_START)) {
                    // CssParser produces this format.
                    val endIdx = normalSpec.lastIndexOf(')')
                    val commaValues =
                        if (endIdx == -1) normalSpec.substring(RGB_START.length) else normalSpec.substring(
                            RGB_START.length,
                            endIdx
                        )
                    val tok = StringTokenizer(commaValues, ",")
                    var r = 0
                    var g = 0
                    var b = 0
                    if (tok.hasMoreTokens()) {
                        val rstr = tok.nextToken().trim { it <= ' ' }
                        try {
                            r = rstr.toInt()
                        } catch (nfe: NumberFormatException) {
                            // ignore
                        }
                        if (tok.hasMoreTokens()) {
                            val gstr = tok.nextToken().trim { it <= ' ' }
                            try {
                                g = gstr.toInt()
                            } catch (nfe: NumberFormatException) {
                                // ignore
                            }
                            if (tok.hasMoreTokens()) {
                                val bstr = tok.nextToken().trim { it <= ' ' }
                                try {
                                    b = bstr.toInt()
                                } catch (nfe: NumberFormatException) {
                                    // ignore
                                }
                            }
                        }
                    }
                    color = Color(r, g, b)
                } else if (normalSpec.startsWith("#")) {
                    // TODO: OPTIMIZE: It would be more efficient to
                    // create new Color(hex), but CssParser doesn't
                    // give us values formatted with "#" either way.
                    val len = normalSpec.length
                    val rgba = IntArray(4)
                    rgba[3] = 255
                    if (len == 4) {
                        for (i in 1..3) {
                            val hexText = normalSpec.substring(i, i + min(1, len - i))
                            try {
                                val singleDigitValue = hexText.toInt(16)
                                rgba[i - 1] = (singleDigitValue shl 4) or singleDigitValue
                            } catch (nfe: NumberFormatException) {
                                // Ignore
                            }
                        }
                    } else {
                        for (i in rgba.indices) {
                            val idx = (2 * i) + 1
                            if (idx < len) {
                                val hexText = normalSpec.substring(idx, idx + min(2, len - idx))
                                try {
                                    rgba[i] = hexText.toInt(16)
                                } catch (nfe: NumberFormatException) {
                                    // Ignore
                                }
                            }
                        }
                    }
                    color = Color(rgba[0], rgba[1], rgba[2], rgba[3])
                } else if (normalSpec.startsWith(RGBA_START)) {
                    val endIdx = normalSpec.lastIndexOf(')')
                    val commaValues =
                        if (endIdx == -1) normalSpec.substring(RGBA_START.length) else normalSpec.substring(
                            RGBA_START.length,
                            endIdx
                        )
                    val tok = StringTokenizer(commaValues, ",")
                    try {
                        if (tok.hasMoreTokens()) {
                            val rstr = tok.nextToken().trim { it <= ' ' }
                            val r = rstr.toInt()
                            if (tok.hasMoreTokens()) {
                                val gstr = tok.nextToken().trim { it <= ' ' }
                                val g = gstr.toInt()
                                if (tok.hasMoreTokens()) {
                                    val bstr = tok.nextToken().trim { it <= ' ' }
                                    val b = bstr.toInt()
                                    if (tok.hasMoreTokens()) {
                                        val astr = tok.nextToken().trim { it <= ' ' }
                                        val a = astr.toFloat()
                                        color = Color(r / 255.0f, g / 255.0f, b / 255.0f, a)
                                    }
                                }
                            }
                        }
                    } catch (nfe: NumberFormatException) {
                        // ignore
                    }
                } else {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.warning("getColor(): Color spec [" + normalSpec + "] unknown.")
                    }
                    return Color.Red
                }
                colorMap.put(normalSpec, color!!)
            }
            return color
        }
    }

    companion object {
        val TRANSPARENT: Color = Color(0, 0, 0, 0)
        private val logger: Logger = Logger.getLogger(ColorFactory::class.java.name)
        private const val RGB_START = "rgb("
        private const val RGBA_START = "rgba("
        var instance: ColorFactory? = null
            get() {
                if (field == null) {
                    synchronized(ColorFactory::class.java) {
                        if (field == null) {
                            field = ColorFactory()
                        }
                    }
                }
                return field
            }
            private set
    }
}