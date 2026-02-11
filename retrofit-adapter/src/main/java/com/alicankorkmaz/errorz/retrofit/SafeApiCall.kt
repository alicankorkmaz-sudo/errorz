package com.alicankorkmaz.errorz.retrofit

import com.alicankorkmaz.errorz.core.api.ApiResponseContract
import com.alicankorkmaz.errorz.core.handleApiResponse
import com.alicankorkmaz.errorz.core.mapper.ExceptionMapperConfig
import com.alicankorkmaz.errorz.core.result.Result
import com.alicankorkmaz.errorz.retrofit.adapter.RetrofitApiResponse
import kotlinx.coroutines.CancellationException
import retrofit2.Response

suspend fun <T> safeApiCall(block: suspend () -> Response<T>): Result<T> {
    return try {
        val response = block()
        handleApiResponse(RetrofitApiResponse(response))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.Error(ExceptionMapperConfig.compositeMapper.map(e))
    }
}

suspend fun <T> safeApiCallNullable(block: suspend () -> Response<T?>): Result<T?> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            val wrapped = RetrofitApiResponse(response)
            handleApiResponse(
                object : ApiResponseContract<T?> {
                    override val isSuccessful: Boolean get() = wrapped.isSuccessful
                    override val code: Int get() = wrapped.code
                    override val body: T? get() = wrapped.body
                    override val errorBody: String? get() = wrapped.errorBody
                }
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.Error(ExceptionMapperConfig.compositeMapper.map(e))
    }
}
