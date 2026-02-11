package com.alicankorkmaz.errorz.feature.profile.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserProfileService {

    @GET("users/{id}")
    suspend fun getProfile(@Path("id") id: String): Response<UserProfileDto>
}
