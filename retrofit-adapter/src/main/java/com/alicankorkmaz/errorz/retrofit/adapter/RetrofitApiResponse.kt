package com.alicankorkmaz.errorz.retrofit.adapter

import com.alicankorkmaz.errorz.core.api.ApiResponseContract
import retrofit2.Response

class RetrofitApiResponse<T>(
    private val response: Response<T>,
) : ApiResponseContract<T> {

    override val isSuccessful: Boolean get() = response.isSuccessful
    override val code: Int get() = response.code()
    override val body: T? get() = response.body()
    override val errorBody: String? get() = response.errorBody()?.string()
}
