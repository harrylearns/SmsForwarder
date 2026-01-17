package com.idormy.sms.forwarder.utils.interceptor

import com.idormy.sms.forwarder.App
import com.idormy.sms.forwarder.utils.Log
import com.idormy.sms.forwarder.utils.SendUtils
import com.xuexiang.xhttp2.interceptor.HttpLoggingInterceptor
import com.xuexiang.xhttp2.utils.HttpUtils
import com.xuexiang.xutil.data.DateUtils
import okhttp3.Connection
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.HttpHeaders
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("PrivatePropertyName")
class LoggingInterceptor(private val logId: Long) : HttpLoggingInterceptor("custom") {

    private val TAG: String = LoggingInterceptor::class.java.simpleName

    init {
        level = if (App.isDebug) Level.BODY else Level.PARAM
    }

    override fun log(message: String) {
        Log.d(TAG, message)
        //状态=-1，不更新原状态
        SendUtils.updateLogs(logId, -1, message)
    }

    /**
     * 记录请求日志
     *
     * @param request
     * @param connection
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun logForRequest(request: Request, connection: Connection?) {
        if (level != Level.PARAM) {
            log("------REQUEST------" + "\nAt " + DateUtils.getNowString(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())))
        }
        val logBody = level == Level.BODY || level == Level.PARAM
        val logHeaders = level == Level.BODY || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1

        try {
            val requestStartMessage = "--> ${request.method()} ${request.url()} $protocol"
            log(requestStartMessage)

            if (logHeaders) {
                val headers = request.headers()
                for (i in 0 until headers.size()) {
                    // SECURITY: Sanitize sensitive headers (Authorization, API keys, etc.)
                    val headerName = headers.name(i)
                    val headerValue = if (isSensitiveHeader(headerName)) {
                        "[REDACTED]"
                    } else {
                        headers.value(i)
                    }
                    log("\t${headerName}: ${headerValue}")
                }
            }

            if (logBody && hasRequestBody) {
                if (HttpUtils.isPlaintext(requestBody?.contentType())) {
                    log("\tbody:" + bodyToString(request))
                } else {
                    log("\tbody: maybe [file part] , too large too print , ignored!")
                }
            }
        } catch (e: Exception) {
            onError(e)
        } finally {
            if (level != Level.PARAM) {
                log("--> END ${request.method()}")
            }
        }
    }

    /**
     * 记录响应日志
     *
     * @param response
     * @param tookMs   请求花费的时间
     * @return
     */
    override fun logForResponse(response: Response, tookMs: Long): Response {
        if (level != Level.PARAM) {
            log("------RESPONSE------" + "\nAt " + DateUtils.getNowString(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())))
        }
        val builder = response.newBuilder()
        val clone = builder.build()
        var responseBody = clone.body()
        val logBody = level == Level.BODY || level == Level.PARAM
        val logHeaders = level == Level.BODY || level == Level.HEADERS

        try {
            log("<-- ${clone.code()} ${clone.message()} ${clone.request().url()} ($tookMs ms）")
            if (logHeaders) {
                log(" ")
                val headers = clone.headers()
                for (i in 0 until headers.size()) {
                    // SECURITY: Sanitize sensitive response headers
                    val headerName = headers.name(i)
                    val headerValue = if (isSensitiveHeader(headerName)) {
                        "[REDACTED]"
                    } else {
                        headers.value(i)
                    }
                    log("\t${headerName}: ${headerValue}")
                }
                log(" ")
            }

            if (logBody && HttpHeaders.hasBody(clone)) {
                if (HttpUtils.isPlaintext(responseBody?.contentType())) {
                    val body = responseBody?.string()
                    // SECURITY: Don't log response bodies containing sensitive data patterns
                    val sanitizedBody = if (body != null && containsSensitiveData(body)) {
                        "[RESPONSE BODY CONTAINS SENSITIVE DATA - REDACTED]"
                    } else {
                        body
                    }
                    log("\tbody:$sanitizedBody")
                    responseBody = ResponseBody.create(responseBody?.contentType(), body ?: "")
                    return response.newBuilder().body(responseBody).build()
                } else {
                    log("\tbody: maybe [file part] , too large too print , ignored!")
                }
                if (level != Level.PARAM) {
                    log(" ")
                }
            }
        } catch (e: Exception) {
            onError(e)
        } finally {
            if (level != Level.PARAM) {
                log("<-- END HTTP")
            }
        }
        return response
    }

    /**
     * Check if header contains sensitive information
     * SECURITY: Prevents logging of credentials and tokens
     */
    private fun isSensitiveHeader(headerName: String): Boolean {
        val lowerName = headerName.lowercase()
        return lowerName.contains("authorization") ||
                lowerName.contains("token") ||
                lowerName.contains("api-key") ||
                lowerName.contains("apikey") ||
                lowerName.contains("api_key") ||
                lowerName.contains("secret") ||
                lowerName.contains("password") ||
                lowerName.contains("credential") ||
                lowerName.contains("cookie") ||
                lowerName.contains("session")
    }

    /**
     * Check if response body contains sensitive data patterns
     * SECURITY: Prevents logging of tokens, passwords, API keys
     */
    private fun containsSensitiveData(body: String): Boolean {
        val lowerBody = body.lowercase()
        return lowerBody.contains("\"token\"") ||
                lowerBody.contains("\"password\"") ||
                lowerBody.contains("\"api_key\"") ||
                lowerBody.contains("\"apikey\"") ||
                lowerBody.contains("\"secret\"") ||
                lowerBody.contains("\"access_token\"") ||
                lowerBody.contains("\"refresh_token\"") ||
                lowerBody.contains("\"private_key\"") ||
                lowerBody.contains("\"credential\"")
    }

}