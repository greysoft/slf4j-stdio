/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio.text;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.grey.loggers.slf4j_stdio.Defs;
import com.grey.loggers.slf4j_stdio.LogPrinter;

public class LogPrinterText implements LogPrinter {
	private enum FMT_TERM {TOKEN, LITERAL}
	private enum FMT_TOKEN {TIME, LVL, TID, TNAME, LNAME, MSG}

	private static String TOKEN_START = "{";
	private static String TOKEN_END = "}";

	private static final String DFLT_FORMAT = makeFormatExpression(FMT_TOKEN.TIME, " ", FMT_TOKEN.LVL, " TID-", FMT_TOKEN.TID, "/", FMT_TOKEN.TNAME,
			" ", FMT_TOKEN.LNAME, " ", FMT_TOKEN.MSG);

	private static final String LOGFORMAT = System.getenv().getOrDefault(Defs.ENVPREFIX+"FORMAT", DFLT_FORMAT);
	private static final List<FormatTerm> DFLT_FORMAT_TERMS = parseLogFormat(LOGFORMAT);

	static {
		System.out.println(Defs.DIAGLOG_PREFIX+"Default Format="+LOGFORMAT);
	}

	private final PrintStream logStream;
	private final List<FormatTerm> formatTerms;

	public LogPrinterText(PrintStream logStream, String fmt) {
		this.logStream = logStream;
		this.formatTerms = (fmt == null ? DFLT_FORMAT_TERMS : parseLogFormat(fmt));
	}

	public LogPrinterText(PrintStream logStream) {
		this(logStream, null);
	}

	@Override
	public void renderLog(String logname, String timestamp, Defs.LOGLEVEL lvl, String msg, Throwable error) {
		Thread thrd = Thread.currentThread();
		StringBuilder sb = new StringBuilder();

		for (FormatTerm term : formatTerms) {
			String txt;
			switch (term.getType()) {
			case TOKEN:
				FMT_TOKEN token = (FMT_TOKEN)term.getValue();
				if (token == FMT_TOKEN.TIME) {
					txt = timestamp;
				} else if (token == FMT_TOKEN.LVL) {
					txt = lvl.name();
				} else if (token == FMT_TOKEN.TID) {
					txt = String.valueOf(thrd.getId());
				} else if (token == FMT_TOKEN.TNAME) {
					txt = thrd.getName();
				} else if (token == FMT_TOKEN.LNAME) {
					txt = logname;
				} else if (token == FMT_TOKEN.MSG) {
					txt = msg;
				} else {
					throw new IllegalStateException("Missing case for format-token="+token);
				}
				break;
			case LITERAL:
				txt = term.getValue().toString();
				break;
			default:
				throw new IllegalStateException("Missing case for format-term="+term.getType());
			}
			sb.append(txt);
		}
		writeLog(sb.toString(), error);
	}

	@Override
	public void flush() throws java.io.IOException {
		logStream.flush();
	}

	private void writeLog(String msg, Throwable ex) {
		logStream.println(msg);
		if (ex != null) ex.printStackTrace(logStream);
	}

	private static List<FormatTerm> parseLogFormat(String fmt) {
		// Replace our start-token with a non-printable char that shouldn't occur.
		// Will need to temporarily remove its escape sequence using another non-printable char.
		// No need to escape the end-token, as it's only interpreted after a recognised start symbol.
		String escapedOpen = String.valueOf((char)17);
		String encodedOpen = String.valueOf((char)18);
		fmt = fmt.replace(TOKEN_START+TOKEN_START, escapedOpen);
		fmt = fmt.replace(TOKEN_START, encodedOpen);
		fmt = fmt.replace(escapedOpen, TOKEN_START); //restore the escaped open-tokens

		List<FormatTerm> terms = new ArrayList<>();
		int pos = 0;
		while (pos != fmt.length()) {
			int pos2 = fmt.indexOf(encodedOpen, pos);
			if (pos2 == -1) pos2 = fmt.length();
			FormatTerm term;
			if (pos2 == pos) {
				pos2 = fmt.indexOf(TOKEN_END, pos);
				if (pos2 == -1) throw new IllegalArgumentException("Unterminated start-token="+TOKEN_START+" in log-format expression - "+fmt);
				String s = fmt.substring(pos+1, pos2);
				pos2 += TOKEN_END.length();
				FMT_TOKEN token = FMT_TOKEN.valueOf(s.toUpperCase());
				term = new FormatTerm(FMT_TERM.TOKEN, token);
			} else {
				String s = fmt.substring(pos, pos2);
				term = new FormatTerm(FMT_TERM.LITERAL, s);
			}
			terms.add(term);
			pos = pos2;
		}
		return terms;
	}

	private static String makeFormatExpression(Object...args) {
		StringBuilder sb = new StringBuilder();
		for (Object arg : args) {
			if (arg instanceof FMT_TOKEN) {
				sb.append(TOKEN_START).append(arg.toString()).append(TOKEN_END);
			} else {
				sb.append(arg.toString());
			}
		}
		return sb.toString();
	}


	private static class FormatTerm {
		private final FMT_TERM type;
		private final Object value;
		public FormatTerm(FMT_TERM type, Object value) {
			this.type = type;
			this.value = value;
		}
		public FMT_TERM getType() {
			return type;
		}
		public Object getValue() {
			return value;
		}
		@Override
		public String toString() {
			return "FormatTerm/"+getType()+"="+getValue();
		}
	}
}
