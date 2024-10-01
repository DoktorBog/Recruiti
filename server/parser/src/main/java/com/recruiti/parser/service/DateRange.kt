package com.recruiti.parser.service

import com.recruiti.project.data.Vacancy
import java.time.LocalDate

sealed class DateRange(val value: String) {
    data object LastWeek : DateRange("lastWeek")
    data object LastMonth : DateRange("lastMonth")
    data object LastThreeMonths : DateRange("lastThreeMonths")
    data object AllTime : DateRange("allTime")

    companion object {
        fun String?.asDateRange(): DateRange {
            return when (this) {
                LastWeek.value -> LastWeek
                LastMonth.value -> LastMonth
                LastThreeMonths.value -> LastThreeMonths
                else -> AllTime
            }
        }
    }
}

fun List<Vacancy>.filterByDateRange(dateRange: DateRange): List<Vacancy> {
    if (dateRange is DateRange.AllTime) {
        return this
    }
    val currentDate = LocalDate.now()
    val dateThreshold = when (dateRange) {
        DateRange.LastWeek -> currentDate.minusWeeks(1)
        DateRange.LastMonth -> currentDate.minusMonths(1)
        DateRange.LastThreeMonths -> currentDate.minusMonths(3)
        else -> null
    }
    return this.asSequence()
        .filter { vacancy ->
            val dateParsed = vacancy.dateParsed
            dateParsed != null && dateParsed.isAfter(dateThreshold)
        }
        .toList()
}