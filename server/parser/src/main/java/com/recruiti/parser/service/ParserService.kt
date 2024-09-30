package com.recruiti.parser.service

import com.recruiti.parser.data.asTable
import com.recruiti.parser.data.createClient
import com.recruiti.parser.domain.ParserRepository
import com.recruiti.parser.service.DateRange.Companion.asDateRange
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.parser() {
    routing {
        get("/parser/query={query}") {
            val queryAsText = call.parameters["query"].orEmpty()
            val count = call.request.queryParameters["count"]?.toIntOrNull() ?: Int.MAX_VALUE
            val dateRange = call.request.queryParameters["dateRange"].asDateRange()

            if (queryAsText.isNotBlank()) {
                val repo = ParserRepository(createClient())

                try {
                    val result = repo.parseVacanciesDou(queryAsText)
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