package com.recruiti.parser.data

import com.recruiti.project.table
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Vacancy(
    val position: String,
    val company: String,
    val description: String,
    val salary: String,
    val link: String,
    val date: String,
    val location: String
) {
    val dateParsed: LocalDate? = runCatching {
        LocalDate.parse(
            date + " " + LocalDate.now().year,
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("uk"))
        )
    }.onFailure { e ->
        print(e)
    }.getOrNull()
}