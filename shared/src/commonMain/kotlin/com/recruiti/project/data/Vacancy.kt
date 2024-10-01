package com.recruiti.project.data

import com.recruiti.project.draw.table
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

fun List<Vacancy>.asTable(): String = table {
    tr {
        td { "POSITION" }
        td { "COMPANY" }
        td { "DESCRIPTION" }
        td { "SALARY" }
        td { "LINK" }
        td { "DATE" }
        td { "LOCATION" }
    }

    this@asTable.forEach { vacancy ->
        tr {
            td { vacancy.position }
            td { vacancy.company }
            td { vacancy.description }
            td { vacancy.salary }
            clickableTd(vacancy.link) { vacancy.link }
            td { vacancy.dateParsed?.format(DateTimeFormatter.ofPattern("d-MM")) ?: vacancy.date }
            td { vacancy.location }
        }
    }
}