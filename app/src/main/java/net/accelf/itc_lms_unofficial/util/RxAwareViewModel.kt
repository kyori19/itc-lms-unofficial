package net.accelf.itc_lms_unofficial.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Copied from https://gist.github.com/aykuttasil/ff97723de6f9182f00369f1eb64adb8b
 */
open class RxAwareViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    fun Disposable.autoDispose() = disposables.add(this)

    fun <T : Any> Single<T>.toLiveData(target: MutableLiveData<Request<T>>) {
        target.postValue(Loading())
        subscribe(
            {
                target.postValue(Success(it))
            }, {
                target.postValue(Error(it))
            }
        )
            .autoDispose()
    }

    @JvmName("toLiveDataNullable")
    fun <T : Any> Single<T>.toLiveData(target: MutableLiveData<Request<T>?>) {
        target.postValue(Loading())
        subscribe(
            {
                target.postValue(Success(it))
            }, {
                target.postValue(Error(it))
                it.printStackTrace()
            }
        )
            .autoDispose()
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }
}
