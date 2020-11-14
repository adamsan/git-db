package hu.adamsan.gitdb.logging

import java.util.logging.Level
import java.util.logging.Logger


object LoggingUtil {

    fun getLogger(name: String): Logger {
        val log = Logger.getLogger(name)
        log.level = getLogLevel()
        return log
    }

    private fun getLogLevel(): Level {
        val key = "logging.level.root"
        val levelStr = (System.getProperty(key) ?: System.getenv(key) ?: "INFO") .toUpperCase()
// java util log levels vs log4j levels
//        FINEST  -> TRACE
//        FINER   -> DEBUG
//        FINE    -> DEBUG
//        INFO    -> INFO
//        WARNING -> WARN
//        SEVERE  -> ERROR
        return when {
            levelStr == "TRACE" -> Level.FINEST
            levelStr == "DEBUG" -> Level.FINE
            levelStr == "INFO" -> Level.INFO
            levelStr == "WARN" -> Level.WARNING
            levelStr == "ERROR" -> Level.SEVERE
            else -> throw IllegalArgumentException("Unknown logging.level.root parameter set: $levelStr")
        }
    }
}