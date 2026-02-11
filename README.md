# Errorz

A multi-module error handling library for Android that brings structure to API error management. Type-safe failure hierarchies, a `Result` monad, and pluggable exception mapping — all with clean separation between infrastructure and domain errors.

[![CI](https://github.com/alicankorkmaz-sudo/errorz/actions/workflows/ci.yml/badge.svg)](https://github.com/alicankorkmaz-sudo/errorz/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## Modules

| Module | Artifact | Description |
|--------|----------|-------------|
| **core** | `com.alicankorkmaz.errorz:core` | Failure types, Result monad, exception mapping, `safeCall` |
| **retrofit-adapter** | `com.alicankorkmaz.errorz:retrofit-adapter` | Retrofit bridge — `safeApiCall`, response wrapping |
| **serialization-kotlinx** | `com.alicankorkmaz.errorz:serialization-kotlinx` | Kotlinx Serialization error body parser |

## Installation

### GitHub Packages

Add the GitHub Packages repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/alicankorkmaz-sudo/errorz")
            credentials {
                username = providers.gradleProperty("gpr.user").getOrElse(System.getenv("GITHUB_ACTOR"))
                password = providers.gradleProperty("gpr.key").getOrElse(System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}
```

Add dependencies to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Core (required)
    implementation("com.alicankorkmaz.errorz:core:0.1.0")

    // Retrofit adapter (if using Retrofit)
    implementation("com.alicankorkmaz.errorz:retrofit-adapter:0.1.0")

    // Kotlinx Serialization support (if using kotlinx.serialization)
    implementation("com.alicankorkmaz.errorz:serialization-kotlinx:0.1.0")
}
```

## Quick Start

### 1. Configure exception mapping

Initialize `ExceptionMapperConfig` in your `Application.onCreate()`:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ExceptionMapperConfig.configure(
            mappers = listOf(
                NetworkExceptionMapper(),          // IOException → NoConnection, SocketTimeout → Timeout
                HttpExceptionMapper(),             // HttpException → ServerError / Unauthorized
                KotlinxSerializationExceptionMapper(), // SerializationException → ParseError
            ),
            errorBodyParser = KotlinxSerializationErrorBodyParser(),
        )
    }
}
```

### 2. Use `safeApiCall` in your repository

```kotlin
class UserProfileRepositoryImpl(
    private val service: UserProfileService,
) : UserProfileRepository {

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return safeApiCall { service.getProfile(userId) }
            .map { it.toDomain() }
            .mapError { it.toDomainFailure() }
    }
}
```

### 3. Handle results

```kotlin
viewModelScope.launch {
    repository.getProfile(userId)
        .onSuccess { profile ->
            _state.value = ProfileState.Loaded(profile)
        }
        .onError { failure ->
            _state.value = when (failure) {
                is UserFailure.NotFound -> ProfileState.NotFound
                is InfrastructureFailure.NoConnection -> ProfileState.Offline
                else -> ProfileState.Error(failure.toString())
            }
        }
}
```

## Architecture

### Failure Hierarchy

```
Failure (sealed interface)
├── Failure.Domain          — Business logic errors (your sealed interfaces)
├── Failure.Infrastructure  — Technical errors
│   ├── NoConnection
│   ├── Timeout
│   ├── ServerError(code, apiError?)
│   ├── Unauthorized(apiError?)
│   └── ParseError(cause?)
└── UnknownFailure(throwable) — Catch-all
```

### Domain Error Mapping

Define domain-specific failures by implementing `Failure.Domain`:

```kotlin
sealed interface UserFailure : Failure.Domain {
    data object NotFound : UserFailure
    data object Suspended : UserFailure
    data class ValidationFailed(val errors: List<String>) : UserFailure
}
```

Map infrastructure errors to domain errors at the repository level:

```kotlin
private fun Failure.toDomainFailure(): Failure = when (this) {
    is InfrastructureFailure.ServerError -> when (code) {
        404 -> UserFailure.NotFound
        403 -> UserFailure.Suspended
        422 -> UserFailure.ValidationFailed(apiError?.errors.orEmpty())
        else -> this
    }
    else -> this
}
```

### Result Extensions

```kotlin
// Transform success data
result.map { dto -> dto.toDomain() }

// Chain operations that return Result
result.flatMap { id -> repository.findById(id) }

// Transform failures
result.mapError { failure -> failure.toDomainFailure() }

// Side effects
result
    .onSuccess { data -> log("Got: $data") }
    .onError { failure -> log("Failed: $failure") }
```

### Custom Exception Mappers

Implement `ExceptionMapper` to handle your own exception types:

```kotlin
class FirebaseExceptionMapper : ExceptionMapper {
    override fun map(throwable: Throwable): Failure? = when (throwable) {
        is FirebaseAuthException -> InfrastructureFailure.Unauthorized()
        is FirebaseNetworkException -> InfrastructureFailure.NoConnection
        else -> null  // return null to pass to next mapper
    }
}
```

Register it in `ExceptionMapperConfig.configure()`.

### HTTP-agnostic `safeCall`

Use `safeCall` for non-Retrofit operations (database, file I/O, etc.):

```kotlin
suspend fun readFromCache(key: String): Result<String> {
    return safeCall { cache.get(key) }
}
```

## Package Structure

```
:core
  failure/    Failure, InfrastructureFailure, UnknownFailure
  result/     Result, map, flatMap, mapError, onSuccess, onError
  api/        ApiError, RawApiError, ApiResponseContract, ErrorBodyParser
  mapper/     ExceptionMapper, CompositeExceptionMapper, NetworkExceptionMapper, ExceptionMapperConfig
  SafeCall.kt safeCall(), handleApiResponse()

:retrofit-adapter
  adapter/    RetrofitApiResponse
  mapper/     HttpExceptionMapper
  SafeApiCall.kt safeApiCall(), safeApiCallNullable()

:serialization-kotlinx
  parser/     ApiErrorDto, KotlinxSerializationErrorBodyParser
  mapper/     KotlinxSerializationExceptionMapper
```

## Requirements

- Android minSdk 26
- Kotlin 2.1+
- Java 11

## License

```
Copyright 2025 alicankorkmaz-sudo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
