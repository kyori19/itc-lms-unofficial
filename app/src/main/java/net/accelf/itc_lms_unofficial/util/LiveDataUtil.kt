package net.accelf.itc_lms_unofficial.util

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.accelf.itc_lms_unofficial.LoadingFragment
import net.accelf.itc_lms_unofficial.StartLoginFragment
import retrofit2.HttpException

fun <T> mutableRequestOf() = mutableLiveDataOf<Request<T>>()

fun <T> mutableLiveDataOf() = MutableLiveData<T>()

fun <T> LiveData<Request<T>>.withResponse(
    activity: AppCompatActivity,
    @StringRes loadingText: Int? = null,
    onSuccess: (T) -> Unit,
) {
    val loadingFragment =
        LoadingFragment.newInstance(loadingText?.let { activity.getString(loadingText) })
    observe(activity) {
        when (it) {
            is Success -> onSuccess(it.data)
            is Loading -> {
                activity.replaceFragment(loadingFragment)
            }
            is Error -> {
                if (it.throwable is HttpException && it.throwable.code() == 302) {
                    activity.replaceFragment(StartLoginFragment.newInstance())
                } else {
                    activity.replaceErrorFragment(it.throwable)
                }
            }
        }
    }
}

fun <T> LiveData<Request<T>>.onSuccess(owner: LifecycleOwner, onSuccess: (T) -> Unit) {
    observe(owner) {
        when (it) {
            is Success -> onSuccess(it.data)
        }
    }
}
