package net.accelf.itc_lms_unofficial.models

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.Serializable

data class SelectOption(
    val value: String,
    val text: String,
    val selected: Boolean,
) : Serializable {

    companion object {
        fun Elements.toSelectOptions(): List<SelectOption> {
            return map { it.toSelectOption() }
                .filter { it.value != "" }
        }

        private fun Element.toSelectOption(): SelectOption {
            require(tagName() == "option") { "Element must be an option tag" }

            return SelectOption(
                `val`(),
                text(),
                hasAttr("selected"),
            )
        }

        fun List<SelectOption>.toTextStrings(): List<String> {
            return map { it.text }
        }

        fun List<SelectOption>.toValueStrings(): List<String> {
            return map { it.value }
        }

        fun List<SelectOption>.selectedText(): String {
            return firstOrNull { it.selected }?.text ?: ""
        }

        fun List<SelectOption>.selectedValue(): String {
            return firstOrNull { it.selected }?.value ?: ""
        }

        fun List<SelectOption>.valueFor(text: String): String {
            return first { it.text == text }.value
        }
    }
}
