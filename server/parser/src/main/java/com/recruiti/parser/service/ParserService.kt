package com.recruiti.parser.service

import com.recruiti.parser.domain.ParserRepository
import com.recruiti.parser.network.createClient
import com.recruiti.parser.service.DateRange.Companion.asDateRange
import com.recruiti.project.data.asTable
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

val REFRESH_MILLIS: Long = 1.minutes.inWholeMilliseconds

fun Application.parser() {
    setupRouting()
    setupRefreshJob()
}

fun Application.setupRouting() {
    routing {
        get("/parser/query={query}") {
            val queryAsText = call.parameters["query"].orEmpty()
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: Int.MAX_VALUE
            val dateRange = call.request.queryParameters["dateRange"].asDateRange()

            if (queryAsText.isNotBlank()) {
                val repo = ParserRepository(createClient())

                try {
                    val result = repo.parseVacancies(queryAsText)
                    val filteredResults = result
                        .sortedByDescending { it.dateParsed }
                        .filterByDateRange(dateRange)
                        .take(count)

                    call.respondText(
                        contentType = ContentType.parse("text/html"),
                        text = filteredResults.asTable()
                    )
                } catch (e: Exception) {
                    call.respondText("Error occurred during parsing: ${e.message}")
                }
            } else {
                call.respondText("Empty query", status = HttpStatusCode.BadRequest)
            }
        }
    }
}

fun Application.setupRefreshJob() {
    val job = launch(Dispatchers.IO) {
        while (true) {
            try {
                val repo = ParserRepository(createClient())
                val result = repo.parseVacancies("Android") // Example default query
                println("Data refreshed at: ${System.currentTimeMillis()}\nresult = $result")

            } catch (e: Exception) {
                println("Error during refresh: ${e.message}")
            }
            delay(REFRESH_MILLIS)
        }
    }
    environment.monitor.subscribe(ApplicationStopping) {
        job.cancel()
    }
}