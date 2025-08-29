/*
 * Copyright 2014-2025 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.helpers.FormattingTuple;

import com.grey.loggers.slf4j_stdio.json.LogPrinterJson;
import com.grey.loggers.slf4j_stdio.text.LogPrinterText;
import com.grey.loggers.slf4j_stdio.utils.TimeFormatter;

public class LoggerAdapter
	extends org.slf4j.helpers.LegacyAbstractLogger
	implements java.io.Flushable
{
	private static final long serialVersionUID = 1L;
	private static final String PRINTER_TYPE = System.getenv().getOrDefault(Defs.ENVPREFIX+"TYPE", "TEXT");
	private static final Defs.LOGLEVEL DFLT_LEVEL = Defs.LOGLEVEL.valueOf(System.getenv().getOrDefault(Defs.ENVPREFIX+"LEVEL", Defs.LOGLEVEL.INFO.name()));
	private static final TimeFormatter DFLT_TIMEFORMATTER = new TimeFormatter(System.getenv().getOrDefault(Defs.ENVPREFIX+"TIMEFORMAT", "UTC"));

	private static final LogPrinter DEFAULT_LOGWRITER = createLogWriter(System.out);

	private static final StreamWrapper DefaultStream = new StreamWrapper();
	public static PrintStream getDefaultStream() {return DefaultStream.get();}
	public static void setDefaultStream(PrintStream ps) {DefaultStream.set(ps);}

	private final Clock clock = Clock.systemUTC();
	private final Defs.LOGLEVEL cfglvl;
	private final LogPrinter logPrinter;
	private final TimeFormatter timeFormatter;

	private final List<Consumer<LogEvent>> listeners = new ArrayList<>();
	private volatile boolean haveListeners;

	static {
		System.out.println(Defs.DIAGLOG_PREFIX+"Default Level="+DFLT_LEVEL+", TimeFormat="+DFLT_TIMEFORMATTER);
	}

	public LoggerAdapter(String lname, PrintStream strm, Defs.LOGLEVEL lvl, TimeFormatter timeFormatter) {
		this.name = lname;
		this.cfglvl = (lvl == null ? DFLT_LEVEL : lvl);
		this.timeFormatter = (timeFormatter == null ? DFLT_TIMEFORMATTER : timeFormatter);

		if (strm == null) {
			strm = getDefaultStream();
			if (strm == null) strm = System.out;
		}

		if (strm == System.out) {
			logPrinter = DEFAULT_LOGWRITER;
		} else {
			logPrinter = createLogWriter(strm);
		}
	}

	public LoggerAdapter(String lname) {
		this(lname, null, null, null);
	}

	@Override
	protected String getFullyQualifiedCallerName() {return null;}

	private boolean isActive(Defs.LOGLEVEL lvl) {return cfglvl.ordinal() >= lvl.ordinal();}

	@Override
	public boolean isTraceEnabled() {return isActive(Defs.LOGLEVEL.TRACE);}
	@Override
	public boolean isDebugEnabled() {return isActive(Defs.LOGLEVEL.DEBUG);}
	@Override
	public boolean isInfoEnabled() {return isActive(Defs.LOGLEVEL.INFO);}
	@Override
	public boolean isWarnEnabled() {return isActive(Defs.LOGLEVEL.WARN);}
	@Override
	public boolean isErrorEnabled() {return isActive(Defs.LOGLEVEL.ERROR);}

	@Override
	public void flush() throws java.io.IOException {
		logPrinter.flush();
	}

	@Override
	protected void handleNormalizedLoggingCall(org.slf4j.event.Level slf4jLevel, org.slf4j.Marker marker, String fmt, Object[] args, Throwable ex) {
		Defs.LOGLEVEL lvl = mapSlf4jLogLevel(slf4jLevel);
		if (!isActive(lvl)) return;

		Instant timestamp = clock.instant();
		FormattingTuple tp = org.slf4j.helpers.MessageFormatter.arrayFormat(fmt, args);
		String formattedMsg = tp.getMessage();

		// listeners will probably only ever exist in test environments, so avoid locking in the general case
		if (haveListeners) {
			LogEvent logEvent = new LogEvent(timestamp, slf4jLevel, fmt, args, formattedMsg, ex, Thread.currentThread());
			synchronized (listeners) {
				for (Consumer<LogEvent> l : listeners) {
					l.accept(logEvent);
				}
			}
		}

		String timeText = timeFormatter.getTime(timestamp);
		logPrinter.renderLog(getName(), timeText, lvl, formattedMsg, ex);
	}

	public final void addListener(Consumer<LogEvent> listener) {
		synchronized (listeners) {
			listeners.remove(listener);
			listeners.add(listener);
		}
		haveListeners = true;
	}

	public final void removeListener(Consumer<LogEvent> listener) {
		synchronized (listeners) {
			for (int idx = 0; idx != listeners.size(); idx++) {
				if (listeners.get(idx) == listener) {
					listeners.remove(idx);
					break;
				}
			}
			if (listeners.isEmpty()) {
				haveListeners = false;
			}
		}
	}

	private static Defs.LOGLEVEL mapSlf4jLogLevel(org.slf4j.event.Level lvl) {
		switch (lvl) {
		case ERROR:
			return Defs.LOGLEVEL.ERROR;
		case WARN:
			return Defs.LOGLEVEL.WARN;
		case INFO:
			return Defs.LOGLEVEL.INFO;
		case DEBUG:
			return Defs.LOGLEVEL.DEBUG;
		case TRACE:
			return Defs.LOGLEVEL.TRACE;
		default:
			return Defs.LOGLEVEL.ERROR;
		}
	}

	private static LogPrinter createLogWriter(PrintStream strm) {
		if (PRINTER_TYPE.equalsIgnoreCase("text")) {
			return new LogPrinterText(strm);
		} else if (PRINTER_TYPE.equalsIgnoreCase("json")) {
			return new LogPrinterJson(strm);
		} else {
			throw new IllegalArgumentException("Invalid Printer type: "+Defs.ENVPREFIX+"TYPE"+"="+PRINTER_TYPE);
		}
	}


	private static class StreamWrapper {
		private PrintStream strm;
		public synchronized PrintStream get() {
			return strm;
		}
		public synchronized void set(PrintStream ps) {
			strm = ps;
		}
	}
}
