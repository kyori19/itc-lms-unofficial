package net.accelf.itc_lms_unofficial.util

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Elements.second(): Element {
    return (if (size > 1) get(1) else null)!!
}
