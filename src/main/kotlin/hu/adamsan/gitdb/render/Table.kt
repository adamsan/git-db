package hu.adamsan.gitdb.render

import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max


class Table {
    var header: List<String> = Collections.emptyList()

    var data: MutableList<List<String>> = ArrayList()

    fun addHeader(header: List<String>) {
        this.header = header
    }

    fun addHeader(vararg headers: String) = this.addHeader(headers.asList())

    fun addRow(row: List<String>) = data.add(row)

    fun addRow(vararg values: Any?) = this.addRow(values.asList().map { it?.toString() ?: "" })

    fun render(): String {
        val maxLengths = calculateMaxLengthsPerColumn()
        val headerLine = maxLengths.zip(header)
                .map { p -> printMiddle(p.second, p.first) }
                .joinToString("|")
        val bodyLines = data.map { row ->
            maxLengths.zip(row)
                    .map { p -> printMiddle(p.second, p.first) }
                    .joinToString("|")
        }
        return headerLine +
                "\n" +
                "-".repeat(maxLengths.sum() + maxLengths.size) +
                "\n" + bodyLines.joinToString("\n")
    }

    private fun calculateMaxLengthsPerColumn(): List<Int> {
        val headerMaxLengths = header.map { it.length }
        val lengths = data.map { rows -> rows.map { it.length } }
        return lengths.fold(headerMaxLengths, { acc, lengths -> acc.zip(lengths).map { a -> max(a.first, a.second) } })
    }

    fun printMiddle(string: String, len: Int, paddingChar: Char = ' '): String {
        val d = (len - string.length) / 2.0
        val before = "$paddingChar".repeat(ceil(d).toInt())
        val after = "$paddingChar".repeat(floor(d).toInt())
        return before + string + after
    }

    fun printLeft(string: String, len: Int, paddingChar: Char = ' '): String {
        val after = "$paddingChar".repeat(len - string.length)
        return string + after
    }

    fun printRight(string: String, len: Int, paddingChar: Char = ' '): String {
        val before = "$paddingChar".repeat(len - string.length)
        return before + string
    }

    fun render(alignments: List<Align>): String {
        val maxLengths = calculateMaxLengthsPerColumn()
        val headerLine = maxLengths.zip(header)
                .map { p -> printMiddle(p.second, p.first) }
                .joinToString("|")
        val bodyLines = data.map { row ->
            maxLengths.zip(row).zip(alignments)
                    .map { p ->
                        when (p.second) {
                            Align.CENTER -> printMiddle(p.first.second, p.first.first)
                            Align.RIGHT -> printRight(p.first.second, p.first.first)
                            Align.LEFT -> printLeft(p.first.second, p.first.first)
                        }
                    }
                    .joinToString("|")
        }
        return headerLine +
                "\n" +
                "-".repeat(maxLengths.sum() + maxLengths.size) +
                "\n" + bodyLines.joinToString("\n")
    }
}

enum class Align {
    LEFT, CENTER, RIGHT
}