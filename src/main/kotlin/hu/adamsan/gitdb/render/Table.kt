package hu.adamsan.gitdb.render

import hu.adamsan.gitdb.render.Align.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

enum class Align { LEFT, CENTER, RIGHT }

class Table {
    private var header: List<String> = Collections.emptyList()

    private var data: MutableList<List<String>> = ArrayList()

    fun addHeader(header: List<String>) {
        this.header = header
    }

    fun addHeader(vararg headers: String) = this.addHeader(headers.asList())

    fun addRow(row: List<String>) = data.add(row)

    fun addRow(vararg values: Any?) = this.addRow(values.asList().map { it?.toString() ?: "" })

    fun render(): String {
        val maxLengths = calculateMaxLengthsPerColumn()
        val headerLine = maxLengths.zip(header)
                .map { p -> padMiddle(p.second, p.first) }
                .joinToString("|")
        val bodyLines = data.map { row ->
            maxLengths.zip(row)
                    .map { p -> padMiddle(p.second, p.first) }
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
        return lengths.fold(headerMaxLengths, { acc, rowLengths -> acc.zip(rowLengths).map { a -> max(a.first, a.second) } })
    }

    fun padMiddle(s: String, len: Int, pad: Char = ' '): String {
        val d = (len - s.length) / 2.0
        val before = "$pad".repeat(ceil(d).toInt())
        val after = "$pad".repeat(floor(d).toInt())
        return before + s + after
    }

    private fun padLeft(s: String, len: Int, pad: Char = ' '): String = s + "$pad".repeat(len - s.length)

    private fun padRight(s: String, len: Int, pad: Char = ' '): String = "$pad".repeat(len - s.length) + s

    fun render(alignments: List<Align>): String {
        val maxLengths = calculateMaxLengthsPerColumn()
        val headerLine = maxLengths.zip(header)
                .map { p -> padMiddle(p.second, p.first) }
                .joinToString("|")
        val bodyLines = data.map { row ->
            maxLengths.zip(row).zip(alignments)
                    .map { p ->
                        when (p.second) {
                            CENTER -> padMiddle(s = p.first.second, len = p.first.first)
                            LEFT -> padLeft(s = p.first.second, len = p.first.first)
                            RIGHT -> padRight(s = p.first.second, len = p.first.first)
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
