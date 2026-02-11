package com.alicankorkmaz.errorz.feature.profile.domain

import com.alicankorkmaz.errorz.core.failure.Failure

sealed interface UserFailure : Failure.Domain {
    data object NotFound : UserFailure
    data object Suspended : UserFailure
    data class ValidationFailed(val errors: List<String>) : UserFailure
}
