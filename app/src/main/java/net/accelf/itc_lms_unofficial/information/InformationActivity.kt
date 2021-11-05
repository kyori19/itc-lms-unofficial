package net.accelf.itc_lms_unofficial.information

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.network.lmsHostUrl
import net.accelf.itc_lms_unofficial.util.replaceFragment
import net.accelf.itc_lms_unofficial.util.withResponse
import okhttp3.HttpUrl

@AndroidEntryPoint
class InformationActivity : BaseActivity(true), BaseActivity.ProvidesUrl {

    private val viewModel by viewModels<InformationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.load()
        }

        val informationFragment = InformationFragment.newInstance()

        viewModel.information.withResponse(this, R.string.loading_information) {
            replaceFragment(informationFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        menu.findItem(R.id.actionLmsInformation)?.isVisible = false
        return true
    }

    override fun url(): HttpUrl {
        return lmsHostUrl.newBuilder()
            .addPathSegments("login")
            .build()
    }
}
