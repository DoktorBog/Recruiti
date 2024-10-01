package com.recruiti.parser.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson

fun createClient(): HttpClient {
    return HttpClient(CIO) {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            //filter { request ->
            //    request.url.host.contains("ktor.io")
            //}
            //sanitizeHeader { header ->
            //    header == HttpHeaders.Authorization
            //}
        }
        install(ContentNegotiation) {
            gson()
        }
    }
}