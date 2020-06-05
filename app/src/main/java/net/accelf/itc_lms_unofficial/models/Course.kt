package net.accelf.itc_lms_unofficial.models

import java.io.Serializable

data class Course(
    val id: String,
    val name: String,
    val teachers: List<String>,
    val temp: Boolean
) : Serializable
