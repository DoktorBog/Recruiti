package com.recruiti.project

class HtmlTableBuilder {
    private val rows = mutableListOf<String>()

    fun tr(content: TrBuilder.() -> Unit) {
        val trBuilder = TrBuilder().apply(content)
        rows.add("<tr>${trBuilder.build()}</tr>")
    }

    fun build(): String = "<table rules=\"all\">${rows.joinToString("\n")}</table>"
}

class TrBuilder {
    private val cells = mutableListOf<String>()

    fun td(content: () -> String) {
        cells.add("<td style=\"padding-left: 5px; padding-bottom: 3px;\"><strong style=\"font-size:13px;\">${content()}</strong></td>")
    }

    fun clickableTd(url: String, content: () -> String) {
        cells.add("<td style=\"padding-left: 5px; padding-bottom: 3px;\"><a href=\"$url\" target=\"_blank\">${content()}</a></td>")
    }

    fun build(): String = cells.joinToString("\n")
}

fun table(content: HtmlTableBuilder.() -> Unit): String {
    return HtmlTableBuilder().apply(content).build()
}