package com.recruiti.parser.domain

import com.recruiti.parser.data.HtmlResponse
import com.recruiti.parser.data.Vacancy
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

const val VACANCIES_PER_REQUEST = 40
const val BASE_URL = "https://jobs.dou.ua/vacancies/?search="

class ParserRepository(
    private val client: HttpClient,
) {

    suspend fun parseVacanciesDou(query: String): List<Vacancy> {
        // Storage for parsed vacancies
        val vacancies = mutableListOf<Vacancy>()

        // Perform the initial GET request
        val url = "$BASE_URL$query"
        val response: HttpResponse = client.get(url)
        val document: Document = Jsoup.parse(response.bodyAsText())
        val csrfToken = response.setCookie()[0].value

        val foundVacancies = getTotalAmountOfVacancies(document)
        val totalShifts = getTotalShifts(foundVacancies)

        // Selectors for parsing data from vacancies list
        val vacancySelector = "li.l-vacancy"
        val positionSelector = ".title a.vt"
        val companySelector = ".title a.company"
        val descriptionSelector = ".sh-info"
        val salarySelector = ".title span.salary"
        val locationSelector = ".title span.cities"
        val dateSelector = ".date"
        val linkSelector = ".title a.vt"

        for (shift in 0..totalShifts) {
            val offset = calculateOffset(shift)

            val htmlResponse: HtmlResponse =
                client.submitForm(
                    url = "https://jobs.dou.ua/vacancies/xhr-load/?search=$query",
                    formParameters = parameters {
                        append("csrfmiddlewaretoken", csrfToken)
                        append("count", offset.toString())
                    },
                ) {
                    method = HttpMethod.Post
                    headers {
                        append(
                            HttpHeaders.Cookie,
                            response.headers.getAll(HttpHeaders.SetCookie)
                                .toString().substringAfter("[").substringBefore("]")
                        )
                        append(HttpHeaders.AcceptLanguage, "en")
                        append(HttpHeaders.Referrer, url)
                    }
                }.body()

            val ajaxDocument = Jsoup.parse(htmlResponse.html)

            // Parse the vacancies from AJAX response
            val ajaxVacancies: Elements = ajaxDocument.select(vacancySelector)

            for (vacancyElement in ajaxVacancies) {
                print(vacancyElement.toString() + "\n\n")
                val position = vacancyElement.select(positionSelector).text().replace("&nbsp;", " ")
                val company = vacancyElement.select(companySelector).text().replace("\u00A0", "")
                val link = vacancyElement.select(linkSelector).attr("href")

                val description = vacancyElement.select(descriptionSelector).text().ifEmpty {
                    "Description not available"
                }

                val salary = vacancyElement.select(salarySelector).text().ifEmpty {
                    "Not specified"
                }

                val location = vacancyElement.select(locationSelector).text().ifEmpty {
                    "Not specified"
                }

                val date = vacancyElement.select(dateSelector).text().ifEmpty {
                    "Not specified"
                }

                vacancies.add(Vacancy(position, company, description, salary, link, date, location))
            }
        }

        client.close()
        return vacancies
    }

    private fun getTotalAmountOfVacancies(document: Document): Int {
        val headerText = document.select(".b-inner-page-header h1").text()
        val vacancyCount = headerText.substringBefore(" ").toIntOrNull()
        return vacancyCount ?: 0
    }

    private fun getTotalShifts(totalVacancies: Int): Int {
        return totalVacancies / VACANCIES_PER_REQUEST
    }

    private fun calculateOffset(shift: Int): Int {
        return (shift * VACANCIES_PER_REQUEST)
    }
}