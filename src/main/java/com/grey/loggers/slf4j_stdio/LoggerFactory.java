/*
 * Copyright 2014-2021 Yusef Badri - All rights reserved.
 * grey-slf4j-stdio is distributed under the terms of the GNU Affero General Public License, Version 3 (AGPLv3).
 */
package com.grey.loggers.slf4j_stdio;

public class LoggerFactory
	implements org.slf4j.ILoggerFactory
{
	@Override
	public org.slf4j.Logger getLogger(String name) {
	    return new LoggerAdapter(name);
	}
}