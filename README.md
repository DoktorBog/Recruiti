# Vacancy Parser API

![Kotlin](https://img.shields.io/badge/Kotlin-1.5.31-blue.svg?logo=kotlin&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-2.0.0-green.svg?logo=ktor&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg?logo=docker&logoColor=white)

A simple **Kotlin-based API** built with **Ktor** that scrapes job vacancies from `Dou.ua` and serves them as an HTML table. The API supports query-based searches, date range filtering, and result limits.

## API Endpoints

### `GET /parser/query={query}`

Fetch job vacancies based on a search query.

- **Parameters**:
    - `query` (required): The search term.
    - `count` (optional): Limit the number of results.
    - `dateRange` (optional): Filter results by time period (`lastWeek`, `lastMonth`, `lastThreeMonths`, `allTime`).

## `ParserService.kt`

`ParserService.kt` handles the logic for scraping and parsing vacancies from **Dou.ua**. It formats the data into a structured response and returns it as an HTML table.

---

> Future plans include adding more data sources to widen job coverage.