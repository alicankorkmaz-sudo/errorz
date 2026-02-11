package com.alicankorkmaz.errorz.feature.profile.domain

import com.alicankorkmaz.errorz.core.result.Result

interface UserProfileRepository {
    suspend fun getProfile(userId: String): Result<UserProfile>
}
