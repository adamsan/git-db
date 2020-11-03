package hu.adamsan.gitdb.render

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TableTest {

    @Test
    fun render() {
        val t = Table()
        t.addHeader("id", "name", "path")
        t.addRow(1, "foo", "foo-123-345-456-65756-757")
        t.addRow(33, "bar", "foo-123-345-456-65756-757")
        t.addRow(444, "foobarbaz", "foo-123-345-456-65756-757  24 234")
        t.addRow(444, "foobarbaz0", "1234567890123456789012345678901234567890")

        val expected =
"""
 id|   name   |                  path                  
--------------------------------------------------------
 1 |    foo   |        foo-123-345-456-65756-757       
 33|    bar   |        foo-123-345-456-65756-757       
444| foobarbaz|    foo-123-345-456-65756-757  24 234   
444|foobarbaz0|1234567890123456789012345678901234567890
""".trimIndent()
        assertEquals(expected, t.render())
        println(t.render())
    }

    @Test
    fun printMiddle() {
        assertEquals("__a__", Table().printMiddle("a", 5, '_'))
        assertEquals("_abc_", Table().printMiddle("abc", 5, '_'))
        assertEquals("__a_", Table().printMiddle("a", 4, '_'))
    }
}