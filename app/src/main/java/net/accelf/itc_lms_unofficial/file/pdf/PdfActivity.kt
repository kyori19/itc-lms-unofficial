package net.accelf.itc_lms_unofficial.file.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.file.download.Downloadable
import net.accelf.itc_lms_unofficial.util.*

@AndroidEntryPoint
class PdfActivity : BaseActivity(false) {

    private val viewModel by viewModels<PdfViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pdfFragment = PdfFragment.newInstance()
        val loadingFragment = LoadingProgressFragment.newInstance()

        viewModel.pdfFile.observe(this) {
            when (it) {
                is Success -> {
                    replaceFragment(pdfFragment)
                    invalidateOptionsMenu()
                }
                is Loading -> {
                    replaceFragment(loadingFragment)
                    invalidateOptionsMenu()
                }
                is Error -> {
                    replaceErrorFragment(it.throwable)
                    invalidateOptionsMenu()
                }
            }
        }
    }

    companion object {
        const val EXTRA_DOWNLOADABLE = "downloadable"

        fun intent(context: Context, downloadable: Downloadable): Intent {
            return Intent(context, PdfActivity::class.java).apply {
                putExtra(EXTRA_DOWNLOADABLE, downloadable)
            }
        }
    }
}
