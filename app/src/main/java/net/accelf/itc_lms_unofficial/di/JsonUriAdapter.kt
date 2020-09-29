package net.accelf.itc_lms_unofficial.di

import android.net.Uri
import com.google.gson.*
import java.lang.reflect.Type

internal class JsonUriAdapter : JsonSerializer<Uri?>, JsonDeserializer<Uri?> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): Uri {
        return Uri.parse(json.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement? {
        return JsonPrimitive(src.toString())
    }
}
