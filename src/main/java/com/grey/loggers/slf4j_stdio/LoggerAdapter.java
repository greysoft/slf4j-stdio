/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

import java.io.PrintStream;
import java.time.Clock;

import com.grey.loggers.slf4j_stdio.text.LogPrinterText;

public class LoggerAdapter
	extends org.slf4j.helpers.MarkerIgnoringBase
	implements java.io.Flushable
{
	private static final long serialVersionUID = 1L;
	private static final Defs.LOGLEVEL DFLT_LEVEL = Defs.LOGLEVEL.valueOf(System.getenv().getOrDefault(Defs.ENVPREFIX+"LEVEL", Defs.LOGLEVEL.INFO.name()));
	private static final String PRINTER_TYPE = System.getenv().getOrDefault(Defs.ENVPREFIX+"TYPE", "TEXT");

	private final Clock clock = Clock.systemUTC();
	private final String logname;
	private final Defs.LOGLEVEL cfglvl;
	private final LogPrinter logPrinter;

	static {
		System.out.println(Defs.DIAGLOG_PREFIX+"Default Level="+DFLT_LEVEL);
	}

	public LoggerAdapter(String lname, PrintStream logger, Defs.LOGLEVEL lvl) {
		this.logname = lname;
		this.cfglvl = (lvl == null ? DFLT_LEVEL : lvl);
		if (logger == null) logger = System.out;
		
		if (PRINTER_TYPE.equalsIgnoreCase("text")) {
			logPrinter = new LogPrinterText(logger);
		} else {
			throw new IllegalArgumentException("Invalid Printer type: "+Defs.ENVPREFIX+"TYPE"+"="+PRINTER_TYPE);
		}
	}

	public LoggerAdapter(String lname) {
		this(lname, null, null);
	}

	private boolean isActive(Defs.LOGLEVEL lvl) {return cfglvl.ordinal() >= lvl.ordinal();}

	@Override
	public String getName() {return logname;}

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
	public void trace(String msg) {
		trace(msg, (Throwable)null);
	}
	@Override
	public void trace(String msg, Throwable ex) {
		log(Defs.LOGLEVEL.TRACE, msg, ex);
	}
	@Override
	public void trace(String fmt, Object arg) {
		trace(fmt, arg, null);
	}
	@Override
	public void trace(String fmt, Object arg1, Object arg2) {
		trace(fmt, arg1, arg2, null);
	}
	@Override
	public void trace(String fmt, Object... args) {
		formatAndLog(Defs.LOGLEVEL.TRACE, fmt, args);
	}

	@Override
	public void debug(String msg) {
		debug(msg, (Throwable)null);
	}
	@Override
	public void debug(String msg, Throwable ex) {
		log(Defs.LOGLEVEL.DEBUG, msg, ex);
	}
	@Override
	public void debug(String fmt, Object arg) {
		debug(fmt, arg, null);
	}
	@Override
	public void debug(String fmt, Object arg1, Object arg2) {
		debug(fmt, arg1, arg2, null);
	}
	@Override
	public void debug(String fmt, Object... args) {
		formatAndLog(Defs.LOGLEVEL.DEBUG, fmt, args);
	}

	@Override
	public void info(String msg) {
		info(msg, (Throwable)null);
	}
	@Override
	public void info(String msg, Throwable ex) {
		log(Defs.LOGLEVEL.INFO, msg, ex);
	}
	@Override
	public void info(String fmt, Object arg) {
		info(fmt, arg, null);
	}
	@Override
	public void info(String fmt, Object arg1, Object arg2) {
		info(fmt, arg1, arg2, null);
	}
	@Override
	public void info(String fmt, Object... args) {
		formatAndLog(Defs.LOGLEVEL.INFO, fmt, args);
	}

	@Override
	public void warn(String msg) {
		warn(msg, (Throwable)null);
	}
	@Override
	public void warn(String msg, Throwable ex) {
		log(Defs.LOGLEVEL.WARN, msg, ex);
	}
	@Override
	public void warn(String fmt, Object arg) {
		warn(fmt, arg, null);
	}
	@Override
	public void warn(String fmt, Object arg1, Object arg2) {
		warn(fmt, arg1, arg2, null);
	}
	@Override
	public void warn(String fmt, Object... args) {
		formatAndLog(Defs.LOGLEVEL.WARN, fmt, args);
	}

	@Override
	public void error(String msg) {
		error(msg, (Throwable)null);
	}
	@Override
	public void error(String msg, Throwable ex) {
		log(Defs.LOGLEVEL.ERROR, msg, ex);
	}
	@Override
	public void error(String fmt, Object arg) {
		error(fmt, arg, null);
	}
	@Override
	public void error(String fmt, Object arg1, Object arg2) {
		error(fmt, arg1, arg2, null);
	}
	@Override
	public void error(String fmt, Object... args) {
		formatAndLog(Defs.LOGLEVEL.ERROR, fmt, args);
	}

	@Override
	public void flush() throws java.io.IOException {
		logPrinter.flush();
	}

	private void log(Defs.LOGLEVEL lvl, String s, Throwable ex) {
		if (!isActive(lvl)) return;
		long t = clock.millis();
		logPrinter.renderLog(logname, t, lvl, s, ex);
	}

	private void formatAndLog(Defs.LOGLEVEL lvl, String fmt, Object[] args) {
		org.slf4j.helpers.FormattingTuple tp = org.slf4j.helpers.MessageFormatter.arrayFormat(fmt, args);
		log(lvl, tp.getMessage(), tp.getThrowable());
	}
}
