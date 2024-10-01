package com.recruiti.parser.domain

import com.recruiti.parser.domain.parser.DouVacancyParser
import com.recruiti.parser.domain.parser.VacancyParser
import com.recruiti.project.data.Vacancy
import io.ktor.client.HttpClient

class ParserRepository(
    client: HttpClient,
) {
    private val parsers: List<VacancyParser> = listOf(DouVacancyParser(client))

    suspend fun parseVacancies(query: String): List<Vacancy> {
        val allVacancies = mutableListOf<Vacancy>()
        for (parser in parsers) {
            val vacancies = parser.parseVacancies(query)
            allVacancies.addAll(vacancies)
        }
        return allVacancies
    }
}