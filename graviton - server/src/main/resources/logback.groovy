import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.ALL
import static ch.qos.logback.classic.Level.ERROR

appender("FILE", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "logs/login/%d/log.%i.zip"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "50 MB"
        }
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss:SSS} [%thread] %logger{35} - %msg%n"
    }
}
appender("ERROR_FILE", RollingFileAppender) {
    filter(ThresholdFilter) {
        level = ERROR
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "logs/login/error/%d/log.%i.zip"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = "50 MB"
        }
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss:SSS} [%thread] %logger{35} - %msg%n"
    }
}
appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss} [%thread] %logger{36} - %msg%n"
    }
}
logger("org.graviton", ALL, ["STDOUT", "FILE", "ERROR_FILE"])
logger("user", ALL)