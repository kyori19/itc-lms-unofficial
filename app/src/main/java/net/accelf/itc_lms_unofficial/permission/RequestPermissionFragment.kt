package net.accelf.itc_lms_unofficial.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.ui.NormalText
import net.accelf.itc_lms_unofficial.ui.Values
import net.accelf.itc_lms_unofficial.ui.compose

class RequestPermissionFragment : Fragment() {

    private lateinit var permission: Permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            permission = Permission.fromId(getInt(ARG_PERMISSION_ID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return compose {
            RequestPermissionFragmentContent(permission)
        }
    }

    @Composable
    @Preview
    private fun PreviewRequestPermissionFragmentContent() {
        RequestPermissionFragmentContent(Permission.WRITE_EXTERNAL_STORAGE)
    }

    @Composable
    private fun RequestPermissionFragmentContent(permission: Permission) {
        Column(
            modifier = Modifier
                .padding(Values.Spacing.around)
                .verticalScroll(rememberScrollState()),
        ) {
            NormalText(
                text = stringResource(id = permission.title),
                modifier = Modifier.padding(Values.Spacing.around),
                style = MaterialTheme.typography.h5,
            )
            NormalText(
                text = stringResource(id = R.string.text_permission_used_to),
                modifier = Modifier.padding(Values.Spacing.around),
                style = MaterialTheme.typography.body2,
            )
            NormalText(
                text = stringResource(id = permission.usage),
                modifier = Modifier.padding(Values.Spacing.around),
                style = MaterialTheme.typography.body1,
            )
            Button(
                onClick = { (requireActivity() as RequestPermissionActivity).request(permission) },
                modifier = Modifier
                    .padding(Values.Spacing.around)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_grant_permission),
                )
            }
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
