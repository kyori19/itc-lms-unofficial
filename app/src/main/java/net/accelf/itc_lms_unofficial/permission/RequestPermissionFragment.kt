package net.accelf.itc_lms_unofficial.permission

import android.os.Bundle
import android.view.View
import net.accelf.itc_lms_unofficial.BaseFragment
import net.accelf.itc_lms_unofficial.databinding.FragmentRequestPermissionBinding

class RequestPermissionFragment :
    BaseFragment<FragmentRequestPermissionBinding>(FragmentRequestPermissionBinding::class.java) {

    private lateinit var permission: Permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            permission = Permission.fromId(getInt(ARG_PERMISSION_ID))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titlePermissionName.setText(permission.title)

        binding.textPermissionUsage.setText(permission.usage)

        binding.buttonGrantPermission.setOnClickListener {
            val activity = requireActivity() as RequestPermissionActivity
            activity.request(permission)
        }
    }

    companion object {
        private const val ARG_PERMISSION_ID = "permission_id"

        @JvmStatic
        fun newInstance(permissionId: Int): RequestPermissionFragment {
            return RequestPermissionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PERMISSION_ID, permissionId)
                }
            }
        }
    }
}
