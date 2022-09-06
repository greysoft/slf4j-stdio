/*
 * Copyright 2014-2022 Yusef Badri - All rights reserved.
 * grey-slf4j-logstdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

public class LoggerFactory
	implements org.slf4j.ILoggerFactory
{
	static {
		System.setProperty("slf4j.detectLoggerNameMismatch", "true");
	}

	@Override
	public org.slf4j.Logger getLogger(String name) {
		return new LoggerAdapter(name);
	}
}
