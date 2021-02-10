/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

public interface LogPrinter {
	public void renderLog(String logname, String timestamp, Defs.LOGLEVEL lvl, String msg, Throwable ex);
	public void flush() throws java.io.IOException;
}
