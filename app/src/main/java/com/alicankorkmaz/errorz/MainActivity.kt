package com.alicankorkmaz.errorz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alicankorkmaz.errorz.core.api.ApiResponseContract
import com.alicankorkmaz.errorz.core.failure.Failure
import com.alicankorkmaz.errorz.core.failure.InfrastructureFailure
import com.alicankorkmaz.errorz.core.handleApiResponse
import com.alicankorkmaz.errorz.core.result.Result
import com.alicankorkmaz.errorz.core.result.map
import com.alicankorkmaz.errorz.core.result.mapError
import com.alicankorkmaz.errorz.core.result.onError
import com.alicankorkmaz.errorz.core.result.onSuccess
import com.alicankorkmaz.errorz.core.safeCall
import com.alicankorkmaz.errorz.feature.profile.domain.UserFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        findViewById<Button>(R.id.btnSuccess).setOnClickListener { runDemo { demoSuccess() } }
        findViewById<Button>(R.id.btnNetworkError).setOnClickListener { runDemo { demoNetworkError() } }
        findViewById<Button>(R.id.btnTimeout).setOnClickListener { runDemo { demoTimeout() } }
        findViewById<Button>(R.id.btnServerError).setOnClickListener { runDemo { demoServerError() } }
        findViewById<Button>(R.id.btnUnauthorized).setOnClickListener { runDemo { demoUnauthorized() } }
        findViewById<Button>(R.id.btnParseError).setOnClickListener { runDemo { demoParseError() } }
        findViewById<Button>(R.id.btnDomainMapping).setOnClickListener { runDemo { demoDomainMapping() } }
        findViewById<Button>(R.id.btnChaining).setOnClickListener { runDemo { demoChaining() } }
    }

    private fun runDemo(block: suspend () -> String) {
        scope.launch {
            tvResult.text = block()
        }
    }

    private suspend fun demoSuccess(): String {
        val result = safeCall { "User: Ali, email: ali@test.com" }
        return formatResult("Success Case", result)
    }

    private suspend fun demoNetworkError(): String {
        val result = safeCall { throw IOException("No internet") }
        return formatResult("Network Error", result)
    }

    private suspend fun demoTimeout(): String {
        val result = safeCall { throw SocketTimeoutException("Connection timed out") }
        return formatResult("Timeout", result)
    }

    private suspend fun demoServerError(): String {
        val response = fakeErrorResponse<String>(500, """{"message":"Internal error","code":"SRV_500"}""")
        val result = handleApiResponse(response)
        return formatResult("Server Error (500)", result)
    }

    private suspend fun demoUnauthorized(): String {
        val response = fakeErrorResponse<String>(401, """{"message":"Token expired","code":"AUTH_401"}""")
        val result = handleApiResponse(response)
        return formatResult("Unauthorized (401)", result)
    }

    private suspend fun demoParseError(): String {
        // Simulates what happens when kotlinx.serialization fails to parse a response.
        // The KotlinxSerializationExceptionMapper maps this to InfrastructureFailure.ParseError.
        val result = safeCall { throw RuntimeException("Simulated parse failure") }
        return formatResult("Parse Error", result)
    }

    private suspend fun demoDomainMapping(): String {
        val response = fakeErrorResponse<String>(404, """{"message":"User not found","code":"USR_404"}""")
        val result = handleApiResponse(response)
            .mapError { failure ->
                when (failure) {
                    is InfrastructureFailure.ServerError -> when (failure.code) {
                        404 -> UserFailure.NotFound
                        else -> failure
                    }
                    else -> failure
                }
            }
        return formatResult("Domain Mapping (404 → UserNotFound)", result)
    }

    private suspend fun demoChaining(): String {
        val result: Result<String> = safeCall { 42 }
            .map { "The answer is $it" }
            .mapError { InfrastructureFailure.Timeout }

        val sb = StringBuilder()
        sb.appendLine("=== Result Chaining ===")
        result
            .onSuccess { sb.appendLine("SUCCESS: $it") }
            .onError { sb.appendLine("ERROR: $it") }
        sb.appendLine("Type: ${result::class.simpleName}")
        return sb.toString()
    }

    private fun <T> formatResult(label: String, result: Result<T>): String {
        val sb = StringBuilder()
        sb.appendLine("=== $label ===")
        when (result) {
            is Result.Success -> {
                sb.appendLine("Status: SUCCESS")
                sb.appendLine("Data: ${result.data}")
            }
            is Result.Error -> {
                sb.appendLine("Status: ERROR")
                sb.appendLine("Failure: ${result.failure}")
                sb.appendLine("Type: ${result.failure::class.simpleName}")
                when (val f = result.failure) {
                    is InfrastructureFailure.ServerError -> {
                        sb.appendLine("HTTP Code: ${f.code}")
                        f.apiError?.let { sb.appendLine("API Error: ${it.message} (${it.code})") }
                    }
                    is InfrastructureFailure.Unauthorized -> {
                        f.apiError?.let { sb.appendLine("API Error: ${it.message} (${it.code})") }
                    }
                    else -> {}
                }
            }
        }
        return sb.toString()
    }

    private fun <T> fakeErrorResponse(code: Int, errorBody: String?): ApiResponseContract<T> {
        return object : ApiResponseContract<T> {
            override val isSuccessful = false
            override val code = code
            override val body: T? = null
            override val errorBody = errorBody
        }
    }
}
