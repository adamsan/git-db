package hu.adamsan.gitdb.render

import hu.adamsan.gitdb.render.Align.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

enum class Align { LEFT, CENTER, RIGHT }

class Table {
    private var header: MutableList<String> = ArrayList()

    private var data: MutableList<List<String>> = ArrayList()

    fun addHeader(vararg headers: String) = this.header.addAll(headers)

    fun addRow(vararg values: Any?) = data.add(values.map { it?.toString() ?: "" })

    fun render(): String = render(header.map { _ -> CENTER }) // by default render aligns all columns to center

    private fun calculateMaxLengthsPerColumn(): List<Int> {
        val headerLengths = header.map { it.length }
        val lengths = data.map { rows -> rows.map { it.length } }
        return lengths.fold(headerLengths, { acc, rowLengths -> acc.zip(rowLengths).map { max(it.first, it.second) } })
    }

    fun padMiddle(s: String, len: Int, pad: Char = ' '): String {
        val d = (len - s.length) / 2.0
        val before = "$pad".repeat(ceil(d).toInt())
        val after = "$pad".repeat(floor(d).toInt())
        return before + s + after
    }

    fun render(alignments: List<Align>): String {
        val maxLengths = calculateMaxLengthsPerColumn()
        val headerLine = maxLengths.zip(header).joinToString("|") { padMiddle(it.second, it.first) }
        val bodyLines = data.map { row ->
            maxLengths.zip(row).zip(alignments)
                    .map { p ->
                        when (p.second) {
                            CENTER -> padMiddle(s = p.first.second, len = p.first.first)
                            LEFT -> p.first.second.padEnd(p.first.first)
                            RIGHT -> p.first.second.padStart(p.first.first)
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
