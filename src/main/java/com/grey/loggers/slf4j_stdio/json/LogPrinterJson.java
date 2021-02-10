/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio.json;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.grey.loggers.slf4j_stdio.Defs;
import com.grey.loggers.slf4j_stdio.LogPrinter;

public class LogPrinterJson implements LogPrinter {

	private static final ObjectWriter Serialiser;
	static {
		ObjectMapper om = new ObjectMapper();
		TypeReference<Map<String, ?>> tref = new TypeReference<Map<String, ?>>(){};
		Serialiser = om.writerFor(tref);
	}

	private final PrintStream logStream;

	public LogPrinterJson(PrintStream logStream) {
		this.logStream = logStream;
	}

	@Override
	public void renderLog(String logname, String timestamp, Defs.LOGLEVEL lvl, String msg, Throwable error) {
		Thread thrd = Thread.currentThread();

		Map<String,Object> log = new HashMap<>();
		log.put("time", timestamp);
		log.put("level", lvl.name());
		log.put("tid", thrd.getId());
		log.put("tname", thrd.getName());
		log.put("logname", logname);
		if (msg != null && !msg.isEmpty()) log.put("msg", msg);

		if (error != null) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw, false);
			error.printStackTrace(pw);
			pw.close();
			log.put("error", sw.getBuffer().toString());
		}

		String json;
		try {
			json = Serialiser.writeValueAsString(log);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to serialise log="+log, ex);
		}
		writeLog(json);
	}

	@Override
	public void flush() throws java.io.IOException {
		logStream.flush();
	}

	private void writeLog(String msg) {
		logStream.println(msg);
	}
}
