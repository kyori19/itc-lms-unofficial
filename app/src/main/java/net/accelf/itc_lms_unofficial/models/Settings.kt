package net.accelf.itc_lms_unofficial.models

import net.accelf.itc_lms_unofficial.models.SelectOption.Companion.toSelectOptions
import net.accelf.itc_lms_unofficial.network.DocumentConverterFactory
import okhttp3.ResponseBody

data class Settings(
    val csrf: String,
    val languages: List<SelectOption>,
    val mailType: String,
    val mailAddress1: String,
    val mailAddress2: String,
    val notifyForwardOptions: List<SelectOption>,
    val updateForwardOptions: List<SelectOption>,
) {

    class Converter(baseUrl: String) :
        DocumentConverterFactory.DocumentConverter<Settings>(baseUrl) {

        override fun convert(value: ResponseBody): Settings? {
            document(value).let {
                return Settings(
                    it.select("input[name=_csrf]").first().`val`(),
                    it.select("select[name=langCd] option").toSelectOptions(),
                    it.select("input[name=mailType][checked]").first().`val`(),
                    it.select("input[name=mailAddress1]").first().`val`(),
                    it.select("input[name=mailAddress2]").first().`val`(),
                    it.select("select[name=forwardId11] option").toSelectOptions(),
                    it.select("select[name=forwardId110] option").toSelectOptions(),
                )
            }
        }
    }
}
