package com.recruiti.parser.service

import com.recruiti.parser.data.asTable
import com.recruiti.parser.data.createClient
import com.recruiti.parser.domain.ParserRepository
import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.parser() {
    routing {
        get("/parser/query={query}") {
            val queryAsText = call.parameters["query"]
            if (!queryAsText.isNullOrBlank()) {
                val repo = ParserRepository(createClient())
                val result = repo.parseVacanciesDou(queryAsText)
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = result.sortedByDescending { it.dateParsed }.asTable()
                )
            } else {
                call.respondText("Empty query")
            }
        }
    }
}