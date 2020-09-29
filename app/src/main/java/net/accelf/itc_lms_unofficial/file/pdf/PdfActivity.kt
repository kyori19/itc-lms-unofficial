package net.accelf.itc_lms_unofficial.file.pdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.file.download.Downloadable
import net.accelf.itc_lms_unofficial.util.replaceFragment

@AndroidEntryPoint
class PdfActivity : BaseActivity(false) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(EXTRA_DOWNLOADABLE)) {
            val downloadable = intent.getSerializableExtra(EXTRA_DOWNLOADABLE) as Downloadable

            replaceFragment(PdfFragment.newInstance(downloadable))
        }
    }

    companion object {
        private const val EXTRA_DOWNLOADABLE = "downloadable"

        fun intent(context: Context, downloadable: Downloadable): Intent {
            return Intent(context, PdfActivity::class.java).apply {
                putExtra(EXTRA_DOWNLOADABLE, downloadable)
            }
        }
    }
}
