package net.accelf.itc_lms_unofficial.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.util.notify

interface PermissionRequestable {

    val permissionRequestLauncher: ActivityResultLauncher<String>

    companion object {

        fun Fragment.preparePermissionRequest(
            getPermission: () -> Permission,
            onComplete: (Boolean) -> Unit,
        ): ActivityResultLauncher<String> {
            return registerForActivityResult(ActivityResultContracts.RequestPermission()) onComplete@{
                val permission = getPermission()
                if (permission.granted(requireContext())) {
                    onComplete(it)
                    return@onComplete
                }

                val (id, notification) = RequestPermissionActivity.permissionRequiredNotification(
                    requireContext(),
                    permission,
                )
                requireContext().notify(id, notification)
            }
        }

        fun AppCompatActivity.preparePermissionRequest(
            getPermission: () -> Permission,
            onComplete: (Boolean) -> Unit,
        ): ActivityResultLauncher<String> {
            return registerForActivityResult(ActivityResultContracts.RequestPermission()) onComplete@{
                val permission = getPermission()
                if (permission.granted(applicationContext)) {
                    onComplete(it)
                    return@onComplete
                }

                val (id, notification) = RequestPermissionActivity.permissionRequiredNotification(
                    applicationContext,
                    permission,
                )
                applicationContext.notify(id, notification)
            }
        }
    }
}
