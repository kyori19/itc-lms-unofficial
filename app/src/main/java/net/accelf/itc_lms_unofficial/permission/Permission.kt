package net.accelf.itc_lms_unofficial.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.StringRes
import net.accelf.itc_lms_unofficial.R

enum class Permission(
    val id: Int,
    @StringRes val title: Int,
    @StringRes val usage: Int,
    val androidName: String,
) {
    WRITE_EXTERNAL_STORAGE(1,
        R.string.text_permission_name_write_external_storage,
        R.string.text_permission_usage_write_external_storage,
        Manifest.permission.WRITE_EXTERNAL_STORAGE),
    ;

    fun granted(context: Context): Boolean {
        return context.checkSelfPermission(androidName) == PERMISSION_GRANTED
    }

    fun request(requestable: PermissionRequestable) {
        requestable.permissionRequestLauncher.launch(androidName)
    }

    companion object {
        fun fromId(id: Int): Permission {
            return values().first { id == it.id }
        }
    }
}
