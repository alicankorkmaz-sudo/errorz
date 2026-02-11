package com.alicankorkmaz.errorz.feature.profile.data

import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.result.Result
import com.alicankorkmaz.errorz.core.result.map
import com.alicankorkmaz.errorz.core.result.mapError
import com.alicankorkmaz.errorz.feature.profile.domain.UserFailure
import com.alicankorkmaz.errorz.feature.profile.domain.UserProfile
import com.alicankorkmaz.errorz.feature.profile.domain.UserProfileRepository
import com.alicankorkmaz.errorz.retrofit.safeApiCall

class UserProfileRepositoryImpl(
    private val service: UserProfileService,
) : UserProfileRepository {

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return safeApiCall { service.getProfile(userId) }
            .map { it.toDomain() }
            .mapError { it.toDomainFailure() }
    }
}

private fun Failure.toDomainFailure(): Failure = when (this) {
    is InfrastructureFailure.ServerError -> when (code) {
        404 -> UserFailure.NotFound
        403 -> UserFailure.Suspended
        422 -> UserFailure.ValidationFailed(apiError?.errors.orEmpty())
        else -> this
    }
    else -> this
}
