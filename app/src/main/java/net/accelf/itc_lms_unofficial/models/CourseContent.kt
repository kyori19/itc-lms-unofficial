package net.accelf.itc_lms_unofficial.models

import java.io.Serializable
import java.util.*

data class CourseContent(
    val title: String,
    val from: Date,
    val until: Date,
    val summary: String,
    val materials: List<Material>
) : Serializable {
    class Builder {

        lateinit var title: String
        lateinit var from: Date
        lateinit var until: Date
        lateinit var summary: String
        val materials = mutableListOf<Material>()

        fun build(): CourseContent {
            return CourseContent(title, from, until, summary, materials)
        }
    }
}
