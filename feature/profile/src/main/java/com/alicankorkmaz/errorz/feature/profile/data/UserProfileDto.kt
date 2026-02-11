package com.alicankorkmaz.errorz.feature.profile.data

import com.alicankorkmaz.errorz.feature.profile.domain.UserProfile
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
) {
    fun toDomain(): UserProfile = UserProfile(
        id = id,
        name = name,
        email = email,
    )
}
