package ua.dev.krava.speedtest.presentation.utils

import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.nio.charset.Charset
import android.util.Xml



/**
 * Created by evheniikravchyna on 02.01.2018.
 */


fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}

fun InputStream.createXmlParser(): XmlPullParser {
    val parser = Xml.newPullParser()
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
    parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false)
    parser.setInput(this, null)
    parser.nextTag()

    return parser
}