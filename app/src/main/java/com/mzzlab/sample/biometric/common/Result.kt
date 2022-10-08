package com.mzzlab.sample.biometric.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.cancellation.CancellationException

sealed interface Result<out T> {
    data class Loading(val initial:Boolean = false): Result<Nothing>
    data class Success<T>(val data: T?) : Result<T>
    data class Error<T>(val exception: Throwable? = null) : Result<T>

    fun successDataOrNull(): T?{
        if(this is Success){
            return this.data
        }
        return null
    }
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart { emit(Result.Loading()) }
        .catch { emit(Result.Error(it)) }
}

internal inline fun <T> getResult(block: () -> T): Result<T> = try {
    block().let { Result.Success(it) }
} catch (ex: Exception) {
    // propagate cancellation
    if (ex is CancellationException){
        throw ex
    }
    Result.Error(ex)
}

internal inline fun <T> Result<T>.switch(
    success : (T?) -> Unit = {},
    error : (ex: Throwable?) -> Unit = {},
    loading: () -> Unit = {}
){
    when(this){
        is Result.Success -> success(this.data)
        is Result.Error -> error(this.exception)
        is Result.Loading -> loading()
    }
}

