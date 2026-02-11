package com.alicankorkmaz.errorz.core.failure

data class UnknownFailure(val throwable: Throwable) : Failure
