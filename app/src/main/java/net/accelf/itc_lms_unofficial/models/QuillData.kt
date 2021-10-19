package net.accelf.itc_lms_unofficial.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuillData(
    @SerializedName("ops") val content: List<QuillElement>,
) : Serializable {

    data class QuillAttributes(
        val color: String?,
        val link: String?,
    ) : Serializable

    data class QuillElement(
        @SerializedName("insert") val text: String,
        val attributes: QuillAttributes?,
    ) : Serializable

    companion object {
        val sample = QuillData(
            content = listOf(
                QuillElement("test text ", QuillAttributes(null, null)),
                QuillElement("with red text", QuillAttributes("red", null)),
                QuillElement("\n", QuillAttributes(null, null)),
                QuillElement("Colored link could appear",
                    QuillAttributes("#00ff00", "https://example.com")),
            ),
        )

        fun String.parseQuill(gson: Gson): QuillData? {
            return gson.fromJson(this, QuillData::class.java)
        }
    }
}
