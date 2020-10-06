package net.accelf.itc_lms_unofficial.util

sealed class Request<T>

class Success<T>(
    val data: T,
) : Request<T>()

class Loading<T> : Request<T>()

class Error<T>(
    val throwable: Throwable,
) : Request<T>()

class Progress<T>(
    val progress: Float,
) : Request<T>()
