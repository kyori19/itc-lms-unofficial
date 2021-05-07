package net.accelf.itc_lms_unofficial.util

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import net.accelf.itc_lms_unofficial.BaseActivity
import net.accelf.itc_lms_unofficial.LoadingFragment
import net.accelf.itc_lms_unofficial.StartLoginFragment
import retrofit2.HttpException

fun <T> mutableRequestOf() = mutableLiveDataOf<Request<T>>()

fun <T> mutableLiveDataOf(default: T) = MutableLiveData(default)

fun <T> mutableLiveDataOf() = MutableLiveData<T>()

fun <T> LiveData<Request<T>>.withResponse(
    activity: AppCompatActivity,
    @StringRes loadingText: Int? = null,
    onSuccess: (T) -> Unit,
) {
    val swipeRefreshEnabled = activity is BaseActivity && activity.swipeRefreshEnabled
    val loadingFragment =
        LoadingFragment.newInstance(loadingText?.let { activity.getString(loadingText) })
    observe(activity) {
        when (it) {
            is Success -> {
                if (swipeRefreshEnabled) {
                    (activity as BaseActivity).binding.swipeRefresh.isRefreshing = false
                }
                onSuccess(it.data)
            }
            is Loading -> {
                if (!(swipeRefreshEnabled && (activity as BaseActivity).binding.swipeRefresh.isRefreshing)) {
                    activity.replaceFragment(loadingFragment)
                }
            }
            is Error -> {
                if (!(swipeRefreshEnabled && (activity as BaseActivity).binding.swipeRefresh.isRefreshing)) {
                    if (it.throwable is HttpException && it.throwable.code() == 302) {
                        activity.replaceFragment(StartLoginFragment.newInstance())
                    } else {
                        activity.replaceErrorFragment(it.throwable)
                    }
                } else {
                    activity.binding.swipeRefresh.isRefreshing = false
                }
            }
            else -> {
            }
        }
    }
}

fun <T> LiveData<Request<T>>.onSuccess(owner: LifecycleOwner, onSuccess: (T) -> Unit) {
    observe(owner) {
        when (it) {
            is Success -> onSuccess(it.data)
            else -> {
            }
        }
    }
}

fun <T> LiveData<Request<T>?>.withSnackProgressBar(
    owner: LifecycleOwner,
    snackProgressBar: SnackProgressBar,
    snackProgressBarManager: SnackProgressBarManager,
    onError: (Throwable) -> Unit,
    onSuccess: (T) -> Unit,
) {
    observe(owner) {
        if (it == null) {
            return@observe
        }

        when (it) {
            is Loading -> {
                snackProgressBarManager.show(snackProgressBar,
                    SnackProgressBarManager.LENGTH_INDEFINITE)
            }
            is Success -> {
                snackProgressBarManager.dismiss()
                onSuccess(it.data)
            }
            is Error -> {
                snackProgressBarManager.dismiss()
                onError(it.throwable)
            }
            else -> {
            }
        }
    }
}
