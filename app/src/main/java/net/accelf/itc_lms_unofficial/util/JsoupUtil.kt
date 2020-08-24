package net.accelf.itc_lms_unofficial.util

import org.jsoup.nodes.Element
import org.jsoup.select.Elements

fun Elements.second(): Element {
    return secondOrNull()!!
}

fun Elements.secondOrNull(): Element? {
    return (if (size > 1) get(1) else null)
}

fun Elements.thirdOrNull(): Element? {
    return (if (size > 2) get(2) else null)
}
