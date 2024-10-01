package com.recruiti.parser.domain.parser

import com.recruiti.project.data.Vacancy

interface VacancyParser {
    suspend fun parseVacancies(query: String): List<Vacancy>
}