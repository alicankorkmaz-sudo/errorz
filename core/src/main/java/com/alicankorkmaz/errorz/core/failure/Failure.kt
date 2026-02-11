package com.alicankorkmaz.errorz.core.failure

sealed interface Failure {
    interface Domain : Failure
    interface Infrastructure : Failure
}
