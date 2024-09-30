package com.recruiti.parser.data

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

fun td(value: String) = """
    <td style="padding-left: 5px;
                 padding-bottom: 3px;">
        <strong style="font-size:13px;">$value</strong><br /></td>
""".trimIndent()

fun clickable(value: String) = """
    <a href="$value" target="_blank">$value</a>
""".trimIndent()

fun Vacancy.asRow() = """
    <tr>
       ${td(position)}
       ${td(company)}
       ${td(description)}
       ${td(salary)}
       ${td(clickable(link))}
       ${td(dateParsed?.format(DateTimeFormatter.ofPattern("d-MM")) ?: date)}
       ${td(location)}
    </tr>
    """.trimIndent()

fun List<Vacancy>.asTable() = this.toMutableList().apply {
    add(0, Vacancy("POSITION", "COMPANY", "DESCRIPTION", "SALARY", "LINK", "DATE", "LOCATION"))
}.joinToString(
    prefix = "<table rules=\"all\">",
    postfix = "</table>",
    separator = "\n",
    transform = Vacancy::asRow
)