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
import net.accelf.itc_lms_unofficial.BaseFragment
import net.accelf.itc_lms_unofficial.databinding.DialogDownloadDocBinding
import net.accelf.itc_lms_unofficial.databinding.DialogDownloadTreeBinding

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TreeFragment()
            1 -> DocFragment()
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = 2

    class TreeFragment :
        BaseFragment<DialogDownloadTreeBinding>(DialogDownloadTreeBinding::class.java) {

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
                if (binding.textTargetDir.text.toString() != it?.path) {
                    binding.textTargetDir.setText(it?.path)
                }
            }

            binding.textTargetDir.setOnClickListener {
                launcher.launch(viewModel.targetDirectoryUri.value ?: Uri.EMPTY)
            }

            viewModel.fileName.observe(viewLifecycleOwner) {
                if (binding.editTextFileName.text.toString() != it) {
                    binding.editTextFileName.setText(it)
                }
            }

            binding.editTextFileName.addTextChangedListener {
                viewModel.fileName.postValue(it.toString())
            }
        }
    }

    class DocFragment :
        BaseFragment<DialogDownloadDocBinding>(DialogDownloadDocBinding::class.java) {

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
                if (binding.textTargetDoc.text.toString() != it?.path) {
                    binding.textTargetDoc.setText(it?.path)
                }
            }

            binding.textTargetDoc.setOnClickListener {
                launcher.launch(viewModel.defaultFileName)
            }
        }
    }
}
