package com.grey.loggers.slf4j_stdio;

import java.time.Instant;
import java.util.Arrays;

import org.slf4j.event.Level;

public class LogEvent {
	private final Instant timestamp;
	private final Level level;
	private final String format;
	private final Object[] args;
	private final String formattedMessage; //constructed from format and args
	private final Throwable error;
	private final Thread thread;

	public LogEvent(Instant timestamp, Level level, String format, Object[] args, String formattedMessage, Throwable error, Thread thread) {
		this.timestamp = timestamp;
		this.level = level;
		this.format = format;
		this.args = args;
		this.formattedMessage = formattedMessage;
		this.error = error;
		this.thread = thread;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public Level getLevel() {
		return level;
	}

	public String getFormat() {
		return format;
	}

	public Object[] getArgs() {
		return args;
	}

	public String getFormattedMessage() {
		return formattedMessage;
	}

	public Throwable getError() {
		return error;
	}

	public Thread getThread() {
		return thread;
	}

	@Override
	public String toString() {
		return "LogEvent["
				+"timestamp=" + timestamp
				+", level=" + level
				+", format=" + format
				+ ", args=" + (args == null ? null : args.length+"/"+Arrays.toString(args))
				+", formattedMessage=" + formattedMessage
				+", error=" + error
				+", thread=" + thread.getId()+"/"+thread
				+"]";
	}
}
