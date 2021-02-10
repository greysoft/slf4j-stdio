slf4j-logstdio is a logging framework that prints all logs to standard-output (ie. the console) and more specifically it is an SLF4J adapter library.
That means you do not call slf4j-logstdio directly, but so long as you have it on your classpath, any logging calls to the SLF4J facade will get transparently routed to slf4j-logstdio.

So why do we need yet another SLF4J-based logger?

1) slf4j-logstdio does not require any configuration files, and is entirely driven from the environment, which can make it easier to control.

2) slf4j-logstdio allows you to log thread IDs, something which the other frameworks resolutely refuse to, and which can be invaluable for debugging multi-threaded applications, ie. just about any modern real-world application.

3) slf4j-logstdio allows you to output the logs in JSON format, if you like that sort of thing.

Configuration
--------------
slf4j-logstdio is configured via the following environment variables.

- GREY_LOGGER_LEVEL:
This env var represents the minimum severity of logs to print, and can take any of the following values, to set the appropriate SLF4J logging filter, eg. a level of INFO means DEBUG logs will not be printed.
ERROR, WARN, INFO, DEBUG, TRACE, OFF
The default is INFO and OFF means no logs will be printed at all.

- GREY_LOGGER_FORMAT
This env var controls the format of the output logs by specifying the presence and position of tokens which get substituted when each log is printed.
This is the default format, and it represents all the available tokens, which are enclosed in braces. The rest of format string represents literal text.
	{TIME} {LVL} TID-{TID}/{TNAME} {LNAME} {MSG}
The token meanings are as follows:
- TIME: Timestamp, with format depending on GREY_LOGGER_TIMEZONE
- LVL: The severity level of this log message
- TID: Thread ID
- TNAME: Thread name
- LNAME: The SLF4J logger name
- MSG: The actual text of your log message

- GREY_LOGGER_TIMEFORMAT
This controls how the log date/time is formatted.
If absent or set to "UTC" (not case-sensitive) log times are printed as an ISO8601 UTC timestamp, with millisecond precision.
If set to "LOCAL" (not case-sensitive) log times are printed as a local date and time, with millisecond precision.
If set to "milliseconds" or "seconds" (not case-sensitive) log times are printed as the epoch milliseconds or seconds respectively.
Else this is assumed to be a timezone (case-sensitive Java ZoneId) and log times are printed as a local date and time of that timezone, with millisecond precision.

- GREY_LOGGER_TYPE
This controls the type of output.
If absent or set to "TEXT", we will output standard textual logs, one per line.
If set to "JSON", the logs will be in JSON form, ie. each log will be a JSON object, with attributes for the timestamp, serverity level, message, etc.
