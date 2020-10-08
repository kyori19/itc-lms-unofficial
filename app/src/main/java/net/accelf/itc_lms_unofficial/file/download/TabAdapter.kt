package net.accelf.itc_lms_unofficial.file.download

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.dialog_download_doc.*
import kotlinx.android.synthetic.main.dialog_download_tree.*
import net.accelf.itc_lms_unofficial.R

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TreeFragment()
            1 -> DocFragment()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = 2

    class TreeFragment : Fragment(R.layout.dialog_download_tree) {

        private val viewModel by activityViewModels<DownloadDialogViewModel>()

        private lateinit var launcher: ActivityResultLauncher<Uri>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            launcher =
                registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
                    viewModel.targetDirectoryUri.postValue(uri)
                }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            viewModel.targetDirectoryUri.observe(viewLifecycleOwner) {
                if (textTargetDir.text.toString() != it?.path) {
                    textTargetDir.setText(it?.path)
                }
            }

            textTargetDir.setOnClickListener {
                launcher.launch(viewModel.targetDirectoryUri.value ?: Uri.EMPTY)
            }

            viewModel.fileName.observe(viewLifecycleOwner) {
                if (editTextFileName.text.toString() != it) {
                    editTextFileName.setText(it)
                }
            }

            editTextFileName.addTextChangedListener {
                viewModel.fileName.postValue(it.toString())
            }
        }
    }

    class DocFragment : Fragment(R.layout.dialog_download_doc) {

        private val viewModel by activityViewModels<DownloadDialogViewModel>()

        private lateinit var launcher: ActivityResultLauncher<String>

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            launcher = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
                viewModel.targetDocumentUri.postValue(uri)
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            viewModel.targetDocumentUri.observe(viewLifecycleOwner) {
                if (textTargetDoc.text.toString() != it?.path) {
                    textTargetDoc.setText(it?.path)
                }
            }

            textTargetDoc.setOnClickListener {
                launcher.launch(viewModel.defaultFileName)
            }
        }
    }
}
